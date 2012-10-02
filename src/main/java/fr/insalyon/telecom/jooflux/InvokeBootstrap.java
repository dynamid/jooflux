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

package fr.insalyon.telecom.jooflux;

import org.pmw.tinylog.Logger;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import static fr.insalyon.telecom.jooflux.internal.JooFluxUtils.*;

public class InvokeBootstrap {

    private static long totalInitialMethodInterception;

    public static CallSite dynvokeConstructor(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Logger.info("lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args));
        try {
            MethodHandle methodHandle = lookup.findConstructor(
                    Class.forName(extractPackageName(name)),
                    type.changeReturnType(void.class)
            );
            totalInitialMethodInterception++;
            Logger.info("\n      - Total method initial interceptions: " + totalInitialMethodInterception);
            return makeCallSiteAndRegister(InvocationType.INVOKECONSTRUCTOR, name, type.toString(), methodHandle);
        } catch (Throwable t) {
            Logger.error("CallSite bootstrap on lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args), t);
            throw t;
        }
    }

    public static CallSite dynvokeStatic(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Logger.info("lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args));
        try {
            MethodHandle methodHandle = lookup.findStatic(
                    Class.forName(extractPackageName(name)),
                    extractMethodName(name),
                    type
            );
            totalInitialMethodInterception++;
            Logger.info("\n      - Total method initial interceptions: " + totalInitialMethodInterception);
            return makeCallSiteAndRegister(InvocationType.INVOKESTATIC, name, type.toString(), methodHandle);
        } catch (Throwable t) {
            Logger.error("CallSite bootstrap on lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args), t);
            throw t;
        }
    }

    public static CallSite dynvokeVirtual(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Logger.info("lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args));
        try {
            MethodHandle methodHandle = lookup.findVirtual(
                    Class.forName(extractPackageName(name)),
                    extractMethodName(name),
                    type.dropParameterTypes(0, 1)
            );
            totalInitialMethodInterception++;
            Logger.info("\n      - Total method initial interceptions: " + totalInitialMethodInterception);
            return makeCallSiteAndRegister(InvocationType.INVOKEVIRTUAL, name, type.toString(), methodHandle);
        } catch (Throwable t) {
            Logger.error("CallSite bootstrap on lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args), t);
            throw t;
        }
    }

    public static CallSite dynvokeInterface(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Logger.info("lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args));
        try {
            MethodHandle methodHandle = lookup.findVirtual(
                    Class.forName(extractPackageName(name)),
                    extractMethodName(name),
                    type.dropParameterTypes(0, 1)
            );
            totalInitialMethodInterception++;
            Logger.info("\n      - Total method initial interceptions: " + totalInitialMethodInterception);
            return makeCallSiteAndRegister(InvocationType.INVOKEINTERFACE, name, type.toString(), methodHandle);
        } catch (Throwable t) {
            Logger.error("CallSite bootstrap on lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args), t);
            throw t;
        }
    }

    public static CallSite dynvokeSpecial(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Logger.info("lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args));
        try {
            Class<?> packageClass = Class.forName(extractPackageName(name));
            MethodHandle methodHandle = lookup.findSpecial(
                    packageClass,
                    extractMethodName(name),
                    type.dropParameterTypes(0, 1),
                    packageClass
            );
            totalInitialMethodInterception++;
            Logger.info("\n      - Total method initial interceptions: " + totalInitialMethodInterception);
            return makeCallSiteAndRegister(InvocationType.INVOKESPECIAL, name, type.toString(), methodHandle);
        } catch (Throwable t) {
            Logger.error("CallSite bootstrap on lookup=" + lookup + ", name=" + name + ", type=" + type + ", args=" + Arrays.toString(args), t);
            throw t;
        }
    }
}
