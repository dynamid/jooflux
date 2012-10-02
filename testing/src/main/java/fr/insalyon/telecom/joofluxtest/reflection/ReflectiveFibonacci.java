/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.reflection;

import java.lang.reflect.Method;

public class ReflectiveFibonacci {

    private static Method reflective_fib_method;

    public static long reflective_fib(int n) throws Throwable {
        if (n <= 1) {
            return n;
        } else {
            long partial1 = (long) reflective_fib_method.invoke(null, n - 1);
            long partial2 = (long) reflective_fib_method.invoke(null, n - 2);
            return partial1 + partial2;
        }
    }

    public static void main(String... args) throws Throwable {
        reflective_fib_method = ReflectiveFibonacci.class.getMethod("reflective_fib", int.class);

        long timer;
        int number = 40;

        for (int i = 0; i < 10; i++) {
            System.out.println("=== Run #" + i + "==");
            timer = System.nanoTime();
            System.out.println("Reflective recursive calculation result (Fibonacci(" + number + ")): " + reflective_fib(number));
            timer = System.nanoTime() - timer;
            System.out.println("TestRecursive - Class execution total time: " + timer + "ns (" + (timer / 1000000.0) + "ms)");
        }
    }
}
