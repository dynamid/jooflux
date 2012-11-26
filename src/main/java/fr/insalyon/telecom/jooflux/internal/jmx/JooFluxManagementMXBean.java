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

import java.util.Set;

public interface JooFluxManagementMXBean {

    public String getName();

    public int getNumberOfRegisteredCallSites();

    public Set<String> getRegisteredCallSiteKeys();

    public String getCallSiteType(String target);

    public void changeCallSiteTarget(String methodType, String oldTarget, String newTarget);

    public void applyBeforeAspect(String callSitesKey, String aspectClass, String aspectMethod);

    public void applyAfterAspect(String callSitesKey, String aspectClass, String aspectMethod);
}
