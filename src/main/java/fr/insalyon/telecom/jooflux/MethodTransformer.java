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

import org.objectweb.asm.tree.MethodNode;

public class MethodTransformer {

    protected MethodTransformer methodTransformer;

    public MethodTransformer(MethodTransformer methodTransformer) {
        this.methodTransformer = methodTransformer;
    }

    public void transform(MethodNode methodNode) {
        if (this.methodTransformer != null) {
            this.methodTransformer.transform(methodNode);
        }
    }
}
