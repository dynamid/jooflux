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

import fr.insalyon.telecom.jooflux.internal.JooFluxUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class InvokeMethodTransformer extends MethodTransformer {

    private static final String BOOTSTRAP_CLASS = "fr/insalyon/telecom/jooflux/InvokeBootstrap";
    private static final String BOOTSTRAP_SIGNATURE = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;";

    private static long totalMethodTransformed;

    public InvokeMethodTransformer(MethodTransformer methodTransformer) {
        super(methodTransformer);
    }

    @Override
    public void transform(MethodNode methodNode) {
        @SuppressWarnings("unchecked")
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insnNode = iterator.next();
            if (!(insnNode instanceof MethodInsnNode)) {
                continue;
            }
            MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
            if (!InvokeClassFilter.isAllowed(methodInsnNode.owner)) {
                Logger.info("Filtered (method): " + methodInsnNode.owner + "#" + methodInsnNode.name);
                continue;
            }
            if (JooFluxUtils.INTERCEPT_CONSTRUCTOR && isInvokeConstructor(methodInsnNode)) {
                methodNode.instructions.insert(
                        methodInsnNode,
                        this.generateInvokeDynamicConstructor(methodInsnNode.name,
                                methodInsnNode.owner,
                                methodInsnNode.desc));
                methodNode.instructions.remove(methodInsnNode);
                Logger.info("Rewrote: " + methodInsnNode.owner + "#" + methodInsnNode.name);
                totalMethodTransformed++;
                Logger.info("\n      - Total method transformed: " + totalMethodTransformed);
            } else if (JooFluxUtils.INTERCEPT_INVOKESTATIC && isInvokeStatic(methodInsnNode)) {
                methodNode.instructions.insert(
                        methodInsnNode,
                        generateInvokeDynamicStatic(
                                methodInsnNode.name,
                                methodInsnNode.owner,
                                methodInsnNode.desc));
                methodNode.instructions.remove(methodInsnNode);
                Logger.info("Rewrote: " + methodInsnNode.owner + "#" + methodInsnNode.name);
                totalMethodTransformed++;
                Logger.info("\n      - Total method transformed: " + totalMethodTransformed);
            } else if (JooFluxUtils.INTERCEPT_INVOKEVIRTUAL && isInvokeVirtual(methodInsnNode)) {
                methodNode.instructions.insert(
                        methodInsnNode,
                        generateInvokeDynamicVirtualInterfaceSpecial(
                                methodInsnNode.name,
                                methodInsnNode.owner,
                                methodInsnNode.desc,
                                "dynvokeVirtual"));
                methodNode.instructions.remove(methodInsnNode);
                Logger.info("Rewrote: " + methodInsnNode.owner + "#" + methodInsnNode.name);
                totalMethodTransformed++;
                Logger.info("\n      - Total method transformed: " + totalMethodTransformed);
            } else if (JooFluxUtils.INTERCEPT_INVOKEINTERFACE && isInvokeInterface(methodInsnNode)) {
                methodNode.instructions.insert(
                        methodInsnNode,
                        generateInvokeDynamicVirtualInterfaceSpecial(
                                methodInsnNode.name,
                                methodInsnNode.owner,
                                methodInsnNode.desc,
                                "dynvokeInterface"));
                methodNode.instructions.remove(methodInsnNode);
                Logger.info("Rewrote: " + methodInsnNode.owner + "#" + methodInsnNode.name);
                totalMethodTransformed++;
                Logger.info("\n      - Total method transformed: " + totalMethodTransformed);
            } else if (JooFluxUtils.INTERCEPT_INVOKESPECIAL && isInvokeSpecial(methodInsnNode)) {
                methodNode.instructions.insert(
                        methodInsnNode,
                        generateInvokeDynamicVirtualInterfaceSpecial(
                                methodInsnNode.name,
                                methodInsnNode.owner,
                                methodInsnNode.desc,
                                "dynvokeSpecial"));
                methodNode.instructions.remove(methodInsnNode);
                Logger.info("Rewrote: " + methodInsnNode.owner + "#" + methodInsnNode.name);
                totalMethodTransformed++;
                Logger.info("\n      - Total method transformed: " + totalMethodTransformed);
            }
        }
        super.transform(methodNode);
    }

    private boolean isInvokeConstructor(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL
                && (methodInsnNode.name.equals("<init>")
                || methodInsnNode.name.equals("<clinit>"));
    }

    private boolean isInvokeStatic(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC;
    }

    private boolean isInvokeVirtual(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL;
    }

    private boolean isInvokeInterface(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKEINTERFACE;
    }

    private boolean isInvokeSpecial(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL
                && (!methodInsnNode.name.equals("<init>"))
                && (!methodInsnNode.name.equals("<clinit>"));
    }

    private InsnList generateInvokeDynamicConstructor(String name, String owner, String desc) {
        InsnList insnList = new InsnList();
        Handle methodHandle = new Handle(
                Opcodes.H_INVOKESTATIC,
                BOOTSTRAP_CLASS,
                "dynvokeConstructor",
                BOOTSTRAP_SIGNATURE
        );
        String descReceiver = Type.getMethodDescriptor(Type.getObjectType(owner), Type.getArgumentTypes(desc));
        insnList.add(new InvokeDynamicInsnNode(owner + "." + name, descReceiver, methodHandle, ""));
        return insnList;
    }

    private InsnList generateInvokeDynamicStatic(String name, String owner, String desc) {
        InsnList insnList = new InsnList();
        Handle methodHandle = new Handle(
                Opcodes.H_INVOKESTATIC,
                BOOTSTRAP_CLASS,
                "dynvokeStatic",
                BOOTSTRAP_SIGNATURE
        );
        insnList.add(new InvokeDynamicInsnNode(owner + "." + name, desc, methodHandle, ""));
        return insnList;
    }

    private InsnList generateInvokeDynamicVirtualInterfaceSpecial(String name, String owner, String desc, String bootstrapMethod) {
        InsnList insnList = new InsnList();
        Handle methodHandle = new Handle(
                Opcodes.H_INVOKESTATIC,
                BOOTSTRAP_CLASS,
                bootstrapMethod,
                BOOTSTRAP_SIGNATURE
        );
        List<Type> argsList = new ArrayList<Type>(Arrays.asList(new Type[]{Type.getObjectType(owner)}));
        argsList.addAll(Arrays.asList(Type.getArgumentTypes(desc)));
        String descReceiver = Type.getMethodDescriptor(Type.getReturnType(desc), argsList.toArray(new Type[argsList.size()]));
        insnList.add(new InvokeDynamicInsnNode(owner + "." + name, descReceiver, methodHandle, ""));
        return insnList;
    }
}
