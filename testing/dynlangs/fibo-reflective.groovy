/* JooFlux
 *    
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
class ReflectiveFibo {

    def method

    ReflectiveFibo() {
        method = getMetaClass().pickMethod("reffibo", [long.class] as Class[])
    }

    def long reflectivefibo(long n) {
        if (n <= 1)
            n
        else
            invokeMethod("reflectivefibo", n - 1) + invokeMethod("reflectivefibo", n - 2)
    }

    def long reffibo(long n) {
        if (n <= 1)
            n
        else
            method.invoke(this, n - 1) + method.invoke(this, n - 2)
    }

}
println ">>> (reflectivefibo n) (invokeMethod)"
rf = new ReflectiveFibo()
for (i in 1..10) {
    long start = System.nanoTime()
    println rf.reflectivefibo(40)
    long finish = System.nanoTime()
    println "Took ${(finish - start) / 1000000.0}ms"
}
println ">>> (reflectivefibo n) (pickMethod+invoke)"
for (i in 1..10) {
    long start = System.nanoTime()
    println rf.reffibo(40)
    long finish = System.nanoTime()
    println "Took ${(finish - start) / 1000000.0}ms"
}
