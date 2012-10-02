/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.classloading;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

public class TestClassLoading {
    public static void main(String[] arg) throws Exception {
        Class c = Class.forName("fr.insalyon.telecom.joofluxtest.classloading.ClassToLoadOne");
        ClassToLoadOne classToLoadOne1 = (ClassToLoadOne) c.newInstance();
        classToLoadOne1.test("ClassToLoadOne ! (test 1)");

        ClassToLoadOne classToLoadOne2 = new ClassToLoadOne();
        classToLoadOne2.test("ClassToLoadOne ! (test 2)");
        classToLoadOne2.test2();

        new ClassToLoadThree();

        URLClassLoader loader = new URLClassLoader(new URL[]{new URL("http://perso.citi.insa-lyon.fr/flemouel/classes/")});
        String[] classToNetworkLoad = new String[]{"Tester"};
        Class<?> c1 = loader.loadClass(classToNetworkLoad[0]);
        Object o1 = c1.newInstance();
        Method m = c1.getMethod("main", new Class[]{classToNetworkLoad.getClass()});
        m.setAccessible(true);
        int mods = m.getModifiers();
        if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("main");
        }
        try {
            m.invoke(null, new Object[]{classToNetworkLoad});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        CachingClassLoader simpleClassLoader = new CachingClassLoader();
        Object o2;
        String tst = "fr.insalyon.telecom.joofluxtest.classloading.ClassToLoadOne";
        o2 = (simpleClassLoader.loadClass(tst)).newInstance();
        ((ClassToLoadOne) o2).test("Simple Class Loader - ClassToLoadOne ! (test)");
        Object o3;
        o3 = (simpleClassLoader.loadClass(tst)).newInstance();
        ((ClassToLoadOne) o3).test("Simple Class Loader - ClassToLoadOne ! (test)");
    }
}
