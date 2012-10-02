/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.self.switcher;

public class Main {

    public static void tick1(int value) {
        System.out.println(">>> " + value);
    }

    public static void tick2(int value) {
        System.out.println("value = " + value);
    }

    public static void main(String... args) throws InterruptedException {
        int counter = 0;
        while (true) {
            tick1(counter);
            counter = counter + 1;
            Thread.sleep(2000);
        }
    }
}
