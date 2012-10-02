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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.pmw.tinylog.Logger;

public class InvokeClassAdapter extends ClassVisitor {

    private boolean isInterface;
//    private boolean isClassLoader;

    public InvokeClassAdapter(int access, ClassVisitor visitor) {
        super(access, visitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.cv.visit(Opcodes.V1_7, access, name, signature, superName, interfaces);
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
//        this.isClassLoader = "java/lang/ClassLoader".equals(superName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc, signature, exceptions);
        if (this.isInterface) {
            Logger.info("Filtered (interface): " + name);
//        } else if (this.isClassLoader) {
//            logger.info("Filtered (classloader): " + name);
        } else if (methodVisitor != null) {
            methodVisitor = new InvokeMethodAdapter(access, name, desc, signature, exceptions, methodVisitor);
        }
        return methodVisitor;
    }
}
