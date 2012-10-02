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

public class TestHelloWorld {

    public static void main(String[] arg) throws Exception {
        long timer;

        timer = System.nanoTime();
        HelloWorld h = new HelloWorld();
        h.displayVirtual("Hello World ! (invokevirtual - object target)");
        HelloWorld h0 = new HelloWorld("Hello World ! (constructor - one argument)");
        HelloWorld h1 = new HelloWorld(3, h0); // Works !
        HelloWorld h2 = new HelloWorld(3, new HelloWorld()); // Don not work :(
        Displayable d = new HelloWorld();
        d.display("Hello World ! (invokeinterface - object target)");
        HelloWorld.displayStatic();
        HelloWorld.displayStatic("Hello World ! (invokestatic - one argument)");
        System.out.println(h.displayVirtualReturn("Hello World ! (invokevirtual - with return)", 5));
        String res = h.displayVirtualReturnMultipleArgs(new String("Hello"), new String("World !"), 5);
        System.out.println(res);
        HelloWorldAbstract helloWorldAbstract = new HelloWorldConcrete();
        helloWorldAbstract.foo("Hello World ! (abstract)");
        timer = System.nanoTime() - timer;
        System.out.println("TestHelloWorld - Class execution total time: " + timer + "ns (" + (timer / 1000000.0) + "ms)");
    }
}