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
import java.lang.invoke.VolatileCallSite;
import java.util.HashMap;
import java.util.Map;

public class JooFluxUtils {

    public static boolean INTERCEPT_CONSTRUCTOR = false;
    public static boolean INTERCEPT_INVOKESTATIC = true;
    public static boolean INTERCEPT_INVOKEVIRTUAL = true;
    public static boolean INTERCEPT_INVOKEINTERFACE = false;
    public static boolean INTERCEPT_INVOKESPECIAL = false;

    public static enum InvocationType {
        INVOKECONSTRUCTOR, INVOKESTATIC, INVOKEVIRTUAL, INVOKEINTERFACE, INVOKESPECIAL
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

    public static CallSite makeCallSiteAndRegister(InvocationType invocationType, String name, String type, MethodHandle methodHandle) {
        VolatileCallSite callSite = new VolatileCallSite(methodHandle);
        if (REGISTER.get(invocationType)) {
            Logger.info("Registered:" + name + ":" + type + " => " + methodHandle.toString());
            CallSiteRegistry.getInstance().put(name + ":" + type, callSite);
        }
        return callSite;
    }
}