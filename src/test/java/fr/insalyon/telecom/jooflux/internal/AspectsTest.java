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

import org.junit.Before;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AspectsTest {

    static class Sum {

        public static int sum(int a, int b) {
            return a + b;
        }

        public static Object[] callHook(Object[] args) {
            int a = (Integer) args[0];
            int b = (Integer) args[1];
            args[0] = a + 1;
            args[1] = b + 1;
            return args;
        }

        public static Object returnHook(Object retval) {
            int val = (Integer) retval;
            return val * 10;
        }
    }

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    MethodHandle sumMH;
    MethodHandle beforeMH;
    MethodHandle afterMH;

    @Before
    public void setup() throws Exception {
        sumMH = lookup.findStatic(Sum.class, "sum",
                methodType(int.class, int.class, int.class));
        beforeMH = lookup.findStatic(Sum.class, "callHook",
                methodType(Object[].class, Object[].class));
        afterMH = lookup.findStatic(Sum.class, "returnHook",
                methodType(Object.class, Object.class));
    }

    @Test
    public void check_before() throws Throwable {
        MethodHandle sumWithBefore = Aspects.before(sumMH, beforeMH);
        int result = (Integer) sumWithBefore.invokeWithArguments(1, 2);
        assertThat(result, is(5));
    }

    @Test
    public void check_after() throws Throwable {
        MethodHandle sumWithAfter = Aspects.after(sumMH, afterMH);
        int result = (Integer) sumWithAfter.invokeWithArguments(1, 2);
        assertThat(result, is(30));
    }

    @Test
    public void check_before_plus_after() throws Throwable {
        MethodHandle sumWithBeforeAndAfter = Aspects.after(
                Aspects.before(sumMH, beforeMH), afterMH);
        int result = (Integer) sumWithBeforeAndAfter.invokeWithArguments(1, 2);
        assertThat(result, is(50));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_wrong_before_handle() throws Throwable {
        Aspects.before(sumMH, sumMH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_wrong_after_handle() throws Throwable {
        Aspects.after(sumMH, sumMH);
    }
}
