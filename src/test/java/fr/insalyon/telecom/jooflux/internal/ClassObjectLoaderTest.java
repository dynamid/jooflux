/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.jooflux.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ClassObjectLoaderTest {

    URL classpath;
    ClassObjectLoader loader;

    @Before
    public void before() throws MalformedURLException {
        classpath = new File("target/classes").toURI().toURL();
        loader = new ClassObjectLoader(classpath);
    }

    @After
    public void teardown() throws IOException {
        loader.close();
    }

    @Test
    public void load_a_bunch_of_classes() throws Throwable {
        String[] classes = {"fr.insalyon.telecom.jooflux.InvokeBootstrap",
                "fr.insalyon.telecom.jooflux.InvokeClassAdapter",
                "fr.insalyon.telecom.jooflux.InvokeClassFilter"};

        assertThat(loader.loadClass(classes[1]), notNullValue());

        Class<?>[] loadedClasses = loader.loadClasses(classes);
        for (Class<?> cls : loadedClasses) {
            assertThat(cls, notNullValue());
        }
    }

    @Test(expected = ClassNotFoundException.class)
    public void fail_on_missing_class() throws ClassNotFoundException {
        loader.loadClass("plop.da.Plop");
    }
}
