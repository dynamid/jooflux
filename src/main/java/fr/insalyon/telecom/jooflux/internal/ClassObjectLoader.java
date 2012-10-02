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

package fr.insalyon.telecom.jooflux.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Helper class to load class definitions from a classpath (folder or JAR archive).
 * <p/>
 * If a class definition shall be reloaded from the same classpath, such as when
 * a .class file has been updated in a folder, then a new <code>ClassObjectLoader</code>
 * shall be created.
 * <p/>
 * This class delegates to a <code>URLClassLoader</code>.
 *
 * @see java.net.URLClassLoader
 */
public class ClassObjectLoader {

    private final URLClassLoader classLoader;

    public ClassObjectLoader(URL classpath) {
        this.classLoader = URLClassLoader.newInstance(new URL[]{classpath});
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    public Class<?>[] loadClasses(String... names) throws ClassNotFoundException {
        Class<?>[] classes = new Class<?>[names.length];
        for (int i = 0; i < names.length; i++) {
            classes[i] = loadClass(names[i]);
        }
        return classes;
    }

    public void close() throws IOException {
        classLoader.close();
    }
}
