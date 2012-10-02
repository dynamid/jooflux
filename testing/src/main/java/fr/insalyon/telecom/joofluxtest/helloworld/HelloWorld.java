/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.helloworld;

public class HelloWorld implements Displayable {

    private int hiddenNumber;

    public HelloWorld() {
        System.out.println("Hello World ! (constructor)");
        this.hiddenNumber = 0;
        this.displayVirtual("Hello World ! (invokevirtual - this target)");
        this.displayPrivate("Hello World ! (invokespecial - private method)");
    }

    public HelloWorld(String toDisplay) {
        System.out.println("Hello World ! (constructor - one arg)");
        this.hiddenNumber = 0;
        this.displayPrivate(toDisplay);
    }

    public HelloWorld(int number, HelloWorld otherHelloWorld) {
        System.out.println("Hello World ! (constructor - multi-args and clone)");
        otherHelloWorld.displayVirtual("Hello World ! (invokevirtual - other target)");
        this.hiddenNumber = number;
    }

    public void display(String toDisplay) {
        this.displayPrivate(toDisplay);
    }

    private void displayPrivate(String toDisplay) {
        System.out.println(toDisplay);
    }

    public static void displayStatic() {
        System.out.println("Hello World ! (invokestatic - no args)");
    }

    public static void displayStatic(String toDisplay) {
        System.out.println(toDisplay);
    }

    public void displayVirtual(String toDisplay) {
        System.out.println(toDisplay);
    }

    public String displayVirtualReturn(String toDisplay, int number) {
        this.hiddenNumber = number;
        System.out.println(toDisplay);
        return new String("Hello World ! (invokevirtual - returned string)");
    }

    public String displayVirtualReturnMultipleArgs(String toDisplay, String toDisplayMore, int number) {
        System.out.println("Hello World ! (same number? " + (this.hiddenNumber == number) + ") (invokevirtual - multiple arguments - private attribute access)");
        for (int i = 0; i < number; i++) {
            System.out.println(toDisplay + "+" + toDisplayMore + "+" + i + " (invokevirtual - multiple arguments - with return)");
        }
        return new String(toDisplay + "+" + toDisplayMore + "+" + number + " (invokevirtual - multiple arguments - returned string)");
    }
}
