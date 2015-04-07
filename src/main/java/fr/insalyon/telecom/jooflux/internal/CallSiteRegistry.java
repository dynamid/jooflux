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

import java.lang.invoke.CallSite;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores references to bootstrapped call sites by target.
 * <p/>
 * Targets are represented as Strings in the form
 * <code>fr/insalyon/telecom/joofluxtest/helloworld/HelloWorld.displayPrivate:(HelloWorld,String)void</code>.
 * <p/>
 * WARNING: the current implementation leaks memory. While a ConcurrentHashMap is being used for thread safety,
 * we should use weak references for call sites that shall be garbage-collected because of class unloading.
 */
public class CallSiteRegistry {

    private CallSiteRegistry() {
        super();
    }

    // Thread-safe thanks to the JVM internals!
    private static class CallSiteRegistrySingletonHolder {
        private static final CallSiteRegistry instance = new CallSiteRegistry();
    }

    public static CallSiteRegistry getInstance() {
        return CallSiteRegistrySingletonHolder.instance;
    }

    private final ConcurrentHashMap<String, Registration> registry = new ConcurrentHashMap<>();

    public void put(String target, String type, CallSite callSite) {
        registry.putIfAbsent(target, new Registration(target, type));
        registry.get(target).getCallSites().add(callSite);
    }

    public int numberOfRegisteredCallSites() {
        return registry.size();
    }

    public Set<String> callSiteKeys() {
        return registry.keySet();
    }

    public Registration callSiteRegistrationFor(String key) {
        return registry.get(key);
    }

    public String callSiteTypeFor(String key) {
        return registry.get(key).getType();
    }

    public Set<CallSite> callSitesFor(String key) {
        return registry.get(key).getCallSites();
    }
}
