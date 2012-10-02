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

import fr.insalyon.telecom.jooflux.internal.jmx.JooFluxManagement;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class InvokeInterceptorAgent {

    public static void premain(String agentArguments, Instrumentation instrumentation) throws Throwable {
        instrumentation.addTransformer(new InvokeClassFileTransformer());

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("fr.insalyon.telecom.jooflux.internal.jmx:type=JooFluxManagement");
        mBeanServer.registerMBean(new JooFluxManagement(), name);
    }
}
