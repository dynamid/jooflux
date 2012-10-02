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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.LoggingLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class InvokeClassFileTransformer implements ClassFileTransformer {

    private long totalTransformationTime;
    private long totalClassTransformed;

    public InvokeClassFileTransformer() {
        super();
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> redefiningClass, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
        byte[] resultBytes;
        if (!InvokeClassFilter.isAllowed(className)) {
            Logger.info("Filtered (class): " + className);
            resultBytes = bytes;
        } else {
            Logger.info("Transformation starting for " + className);

            long methodTransformationTime = System.nanoTime();
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new NonReflexiveClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            // To check how it is better performing or not ?
            // ClassWriter classWriter = new NonReflexiveClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            InvokeClassAdapter invokeClassAdapter = new InvokeClassAdapter(0, classWriter);
            classReader.accept(invokeClassAdapter, 0);
            resultBytes = classWriter.toByteArray();

            methodTransformationTime = System.nanoTime() - methodTransformationTime;
            totalTransformationTime = totalTransformationTime + methodTransformationTime;
            totalClassTransformed++;
            Logger.info("Transformation ended for " + className
                    + "\n      - Class rewrite time: " + methodTransformationTime + "ns (" + (methodTransformationTime / 1000000.0) + "ms)"
                    + "\n      - Class rewrite total time: " + totalTransformationTime + "ns (" + (totalTransformationTime / 1000000.0) + "ms)"
                    + "\n      - Total class transformed: " + totalClassTransformed
            );

            if (Logger.getLoggingLevel().equals(LoggingLevel.TRACE)) {
                StringWriter stringWriter = new StringWriter();
                TraceClassVisitor tracer = new TraceClassVisitor(new PrintWriter(stringWriter));
                ClassReader transformedReader = new ClassReader(resultBytes);
                transformedReader.accept(tracer, 0);
                Logger.trace("Transformed bytecode for " + className + ":\n" + stringWriter.toString());
            }
        }
        return resultBytes;
    }
}