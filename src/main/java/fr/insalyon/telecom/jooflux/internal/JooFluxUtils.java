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

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VolatileCallSite;
import java.util.HashMap;
import java.util.Map;

public class JooFluxUtils {

    public static final boolean INTERCEPT_CONSTRUCTOR = false;
    public static final boolean INTERCEPT_INVOKESTATIC = true;
    public static final boolean INTERCEPT_INVOKEVIRTUAL = true;
    public static final boolean INTERCEPT_INVOKEINTERFACE = true;
    public static final boolean INTERCEPT_INVOKESPECIAL = false;

    public static Class<?> classDefinition(MethodHandles.Lookup lookup, String name) throws ClassNotFoundException {
        ClassLoader classLoader = lookup.lookupClass().getClassLoader();
        return Class.forName(name, true, classLoader);
    }

    public static enum InvocationType {
        INVOKECONSTRUCTOR("constructor"),
        INVOKESTATIC("static"),
        INVOKEVIRTUAL("virtual"),
        INVOKEINTERFACE("vitual"),
        INVOKESPECIAL("special");

        private final String command;

        private InvocationType(String command) {
            this.command = command;
        }

        public String command() {
            return command;
        }
    }

    private static final Map<InvocationType, Boolean> REGISTER = new HashMap<InvocationType, Boolean>() {
        {
            put(InvocationType.INVOKECONSTRUCTOR, true);
            put(InvocationType.INVOKESTATIC, true);
            put(InvocationType.INVOKEVIRTUAL, true);
            put(InvocationType.INVOKEINTERFACE, true);
            put(InvocationType.INVOKESPECIAL, true);
        }
    };

    public static String extractPackageName(String name) {
        return name.split("\\.")[0].replace('/', '.');
    }

    public static String extractMethodName(String name) {
        return name.split("\\.")[1];
    }

    public static void registerCallSite(CallSite callSite, InvocationType invocationType, String name, String type) {
        if (REGISTER.get(invocationType)) {
            Logger.info("Registered:" + name + ":" + type + " => " + callSite.getTarget().toString());
            CallSiteRegistry.getInstance().put(callSiteId(name, type), invocationType.command(), callSite);
        }
    }

    private static String callSiteId(String name, String type) {
        return name + ":" + type;
    }

    public static CallSite makeCallSiteAndRegister(InvocationType invocationType, String name, String type, MethodHandle methodHandle) {
        VolatileCallSite callSite = new VolatileCallSite(methodHandle);
        if (REGISTER.get(invocationType)) {
            Logger.info("Registered:" + name + ":" + type + " => " + methodHandle.toString());
            CallSiteRegistry.getInstance().put(callSiteId(name, type), invocationType.command(), callSite);
        }
        return callSite;
    }
}