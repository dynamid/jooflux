/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.fibonacci;

public class TestRecursive {

    public static void main(String[] arg) throws Exception {
        long timer;
        int number = 40;

        // Wait Fibo for the method call to be registered
        // Fibonacci.fib(number);
        // Waiting for the dynamic plug
        // Scanner scanner = new Scanner(System.in);
        // System.out.println("Press Return");
        // scanner.nextLine();

        for (int i = 0; i < 10; i++) {
            System.out.println("=== Run #" + i + "==");
            timer = System.nanoTime();
            System.out.println("Recursive calculation result (Fibonacci(" + number + ")): " + Fibonacci.fib(number));
            timer = System.nanoTime() - timer;
            System.out.println("TestRecursive - Class execution total time: " + timer + "ns (" + (timer / 1000000.0) + "ms)");
        }
    }
}
