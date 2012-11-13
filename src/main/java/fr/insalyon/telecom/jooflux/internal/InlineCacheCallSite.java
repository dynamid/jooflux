/*
 * JooFlux
 *
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.jooflux.internal;

import org.pmw.tinylog.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodType.methodType;

/**
 * A polymorphic inline cache for dispatching virtual methods.
 *
 * This code is an adaptation of the inline cache implementation proposed by Remi Forax
 * in his jsr292-cookbook project.
 *
 * @see <a href="http://code.google.com/p/jsr292-cookbook/">jsr292-cookbook</a>
 */
public class InlineCacheCallSite extends MutableCallSite {

    private final MethodHandles.Lookup lookup;
    private final String methodName;
    private int depth = 0;

    private static final int MAX_DEPTH = 3;
    private static final MethodHandle GUARD;
    private static final MethodHandle FALLBACK;

    static {
        MethodHandles.Lookup staticLookup = MethodHandles.publicLookup();
        try {
            GUARD = staticLookup.findStatic(
                    InlineCacheCallSite.class,
                    "guard",
                    methodType(boolean.class, Class.class, Object.class)
            );
            FALLBACK = staticLookup.findStatic(
                    InlineCacheCallSite.class,
                    "fallback",
                    methodType(Object.class, InlineCacheCallSite.class, Object[].class)
            );
        } catch (Throwable t) {
            throw new Error("Could not load the inline cache method handle building blocks", t);
        }
    }

    private MethodHandle fallback() {
        MethodType type = type();
        return FALLBACK
                .bindTo(this)
                .asCollector(Object[].class, type.parameterCount())
                .asType(type);
    }

    public InlineCacheCallSite(MethodHandles.Lookup lookup, String methodName, MethodType type) {
        super(type);
        this.lookup = lookup;
        this.methodName = methodName;
        superSetTarget(fallback());
    }

    @Override
    public void setTarget(MethodHandle newTarget) {
        MethodType expectedReceiverType = newTarget.type();
        Class<?> expectedReceiverClass = expectedReceiverType.parameterType(0);

        MethodHandle test = GUARD.bindTo(expectedReceiverClass);
        test = test.asType(test.type().changeParameterType(0, expectedReceiverClass));

        MethodHandle guard = MethodHandles.guardWithTest(test, newTarget, fallback());
        depth = 1;

        superSetTarget(guard);
    }

    private void superSetTarget(MethodHandle guard) {
        super.setTarget(guard);
    }

    public static boolean guard(Class<?> clazz, Object receiver) {
        return receiver.getClass() == clazz;
    }

    public static Object fallback(InlineCacheCallSite callSite, Object[] args) throws Throwable {
        MethodType type = callSite.type();
        Object receiver = args[0];
        Class<?> receiverClass = receiver.getClass();

        if (callSite.depth >= MAX_DEPTH) {
            Logger.warn("Megamorphic call site: receiver=" + receiverClass.getCanonicalName() +
                    " methodName=" + callSite.methodName +
                    " type=" + type);
            MethodHandle target = fetchMethodHandle(callSite, type.parameterType(0), type).asType(type);
            callSite.superSetTarget(target);
            return target.invokeWithArguments(args);
        }

        MethodHandle target = fetchMethodHandle(callSite, receiverClass, type);
        target = target.asType(type);
        MethodHandle test = GUARD.bindTo(receiverClass);
        test = test.asType(test.type().changeParameterType(0, type.parameterType(0)));

        MethodHandle guard = MethodHandles.guardWithTest(test, target, callSite.getTarget());
        callSite.depth = callSite.depth + 1;

        callSite.superSetTarget(guard);
        return target.invokeWithArguments(args);
    }

    private static MethodHandle fetchMethodHandle(InlineCacheCallSite callSite, Class<?> receiverClass, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle target;
        try {
            target = callSite.lookup.findVirtual(receiverClass, callSite.methodName,
                    type.dropParameterTypes(0, 1));
        } catch (IllegalAccessException e) {
            Method method = receiverClass.getMethod(callSite.methodName, type.dropParameterTypes(0, 1).parameterArray());
            method.setAccessible(true);
            target = callSite.lookup.unreflect(method);
        }
        return target;
    }
}
