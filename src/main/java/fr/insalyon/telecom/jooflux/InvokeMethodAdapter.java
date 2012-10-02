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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

public class InvokeMethodAdapter extends MethodVisitor {

    private MethodVisitor methodVisitor;

    public InvokeMethodAdapter(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor methodVisitor) {
        super(access, new MethodNode(access, name, desc, signature, exceptions));
        this.methodVisitor = methodVisitor;
    }

    @Override
    public void visitEnd() {
        MethodNode methodNode = (MethodNode) this.mv;
        InvokeMethodTransformer invokeMethodTransformer = new InvokeMethodTransformer(null);
        invokeMethodTransformer.transform(methodNode);
        methodNode.accept(this.methodVisitor);
    }
}
