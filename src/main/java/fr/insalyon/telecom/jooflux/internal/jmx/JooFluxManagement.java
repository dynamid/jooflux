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

package fr.insalyon.telecom.jooflux.internal.jmx;

import fr.insalyon.telecom.jooflux.internal.Aspects;
import fr.insalyon.telecom.jooflux.internal.CallSiteRegistry;
import org.pmw.tinylog.Logger;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Set;

public class JooFluxManagement implements JooFluxManagementMXBean {

    private CallSiteRegistry registry = CallSiteRegistry.getInstance();

    @Override
    public String getName() {
        return "JooFlux managed bean";
    }

    @Override
    public int getNumberOfRegisteredCallSites() {
        return registry.numberOfRegisteredCallSites();
    }

    @Override
    public Set<String> getRegisteredCallSiteKeys() {
        return registry.callSiteKeys();
    }

    @Override
    public String getCallSiteType(String target) {
        return registry.callSiteTypeFor(target);
    }

    @Override
    public void changeCallSiteTarget(String methodType, String oldTarget, String newTarget) {
        String[] split = newTarget.split("\\.");
        String newClassName = split[0].replaceAll("/", ".");
        split = split[1].split(":");
        String newMethodName = split[0];
        String newTypeName = split[1];

        split = oldTarget.split("\\.");
        String oldClassName = split[0].replaceAll("/", ".");
        split = split[1].split(":");
        String oldMethodName = split[0];
        String oldTypeName = split[1];

        Logger.trace(oldClassName + " -> " + newClassName);
        Logger.trace(oldMethodName + " -> " + newMethodName);
        Logger.trace(oldTypeName + " -> " + newTypeName);

        try {
            Class<?> cls = getClass().getClassLoader().loadClass(newClassName);
            MethodType mt = MethodType.fromMethodDescriptorString(newTypeName, null);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = null;
            switch (methodType) {
                case "static":
                    handle = lookup.findStatic(cls, newMethodName, mt);
                    break;
                case "virtual":
                    handle = lookup.findVirtual(cls, newMethodName, mt);
//                    System.out.println(handle);
//                    MethodType mttmp = handle.type();
//                    handle = handle.asType(mttmp.changeParameterType(0, Class.forName(oldClassName)));
//                    System.out.println(handle);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong method type: " + methodType);
            }
            if (handle != null) {
                Set<CallSite> sites = registry.callSitesFor(oldTarget);
                if (sites != null) {
                    for (CallSite site : sites) {
                        site.setTarget(handle);
                    }
                } else {
                    throw new RuntimeException("There are no call sites for: " + oldTarget);
                }
            } else {
                throw new RuntimeException("Could not get the method handle: " + cls + "." + newMethodName + ":" + newTypeName);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void applyBeforeAspect(String callSitesKey, String aspectClass, String aspectMethod) {
        try {
            Class<?> klass = getClass().getClassLoader().loadClass(aspectClass);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.findStatic(klass, aspectMethod, MethodType.methodType(Object[].class, Object[].class));
            Set<CallSite> callSites = registry.callSitesFor(callSitesKey);
            if (callSites == null) {
                throw new IllegalArgumentException("No call sites have been registered for key: " + callSitesKey);
            }
            for (CallSite callSite : callSites) {
                callSite.setTarget(Aspects.before(callSite.getTarget(), handle));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void applyAfterAspect(String callSitesKey, String aspectClass, String aspectMethod) {
        try {
            Class<?> klass = getClass().getClassLoader().loadClass(aspectClass);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.findStatic(klass, aspectMethod, MethodType.methodType(Object.class, Object.class));
            Set<CallSite> callSites = registry.callSitesFor(callSitesKey);
            if (callSites == null) {
                throw new IllegalArgumentException("No call sites have been registered for key: " + callSitesKey);
            }
            for (CallSite callSite : callSites) {
                callSite.setTarget(Aspects.after(callSite.getTarget(), handle));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
