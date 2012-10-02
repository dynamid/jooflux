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

import java.util.Hashtable;
import java.util.logging.Logger;

public class CachingClassLoader extends ClassLoader {

    private static final Logger logger = Logger.getLogger(CachingClassLoader.class.getName());

    private Hashtable<String, Class<?>> classes = new Hashtable<>();

    public CachingClassLoader() {
    }

    /**
     * This is a simple version for external clients since they
     * will always want the class resolved before it is returned
     * to them.
     */
    public Class loadClass(String className) throws ClassNotFoundException {
        return (loadClass(className, true));
    }

    /**
     * This is the required version of loadClass which is called
     * both from loadClass above and from the internal function
     * FindClassFromClass.
     */
    public synchronized Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
        Class result;

        logger.info("        >>>>>> Load class : " + className);

        /* Check our local cache of classes */
        result = (Class) classes.get(className);
        if (result != null) {
            logger.info("        >>>>>> returning cached result.");
            return result;
        }

        /* Check with the primordial class loader */
        try {
            result = super.findSystemClass(className);
            logger.info("        >>>>>> returning system class (in CLASSPATH).");
        } catch (ClassNotFoundException e) {
            logger.info("        >>>>>> Not a system class.");
        }

        if (resolveIt) {
            resolveClass(result);
        }

        classes.put(className, result);
        return result;
    }
}
