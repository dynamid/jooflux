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

def classicfibo(n) {
    if (n <= 1)
        n
    else
        classicfibo(n - 1) + classicfibo(n - 2)
}

def fastfibo(long n) {
    if (n <= 1)
        n
    else
        fastfibo(n - 1) + fastfibo(n - 2)
}

long fastestfibo(long n) {
    if (n <= 1)
        n
    else
        fastestfibo(n - 1) + fastestfibo(n - 2)
}

println ">>> (classicfibo n)"
for (i in 1..10) {
    long start = System.nanoTime()
    println classicfibo(40)
    long finish = System.nanoTime()
    println "Took ${(finish - start) / 1000000.0}ms"
}

println ">>> (fastfibo n)"
for (i in 1..10) {
    long start = System.nanoTime()
    println fastfibo(40)
    long finish = System.nanoTime()
    println "Took ${(finish - start) / 1000000.0}ms"
}

println ">>> (fastestfibo n)"
for (i in 1..10) {
    long start = System.nanoTime()
    println fastestfibo(40)
    long finish = System.nanoTime()
    println "Took ${(finish - start) / 1000000.0}ms"
}

