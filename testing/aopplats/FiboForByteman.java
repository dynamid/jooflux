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

import java.util.Scanner;

public class FiboForByteman {

    public static long fib(int n) throws Exception {
        if (n <= 1) return n;
        else return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] arg) throws Exception {
        long timer;
        int number = 40;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Return");
        scanner.nextLine(); 

        for (;;) {
            timer = System.nanoTime();
            System.out.println("Recursive calculation result (Fibonacci(" + number + ")): " + FiboForByteman.fib(number));
            timer = System.nanoTime() - timer;
            System.out.println("TestRecursive - Class execution total time: " + timer + "ns (" + (timer / 1000000.0) + "ms)");
        }
    }
}
