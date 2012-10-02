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

public class NonReflexiveClassWriter extends ClassWriter {
    public NonReflexiveClassWriter(int i) {
        super(i);
    }

    public NonReflexiveClassWriter(ClassReader classReader, int i) {
        super(classReader, i);
    }

    @Override
    protected String getCommonSuperClass(String s, String s1) {
        // By default, all object extend java.lang.Object
        // Can be better -> TODO
        return "java/lang/Object";
    }
}
