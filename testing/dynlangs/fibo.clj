; JooFlux
;    
; Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
; Copyright (c) 2012 Julien Ponge, INSA-Lyon
; Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

; Fibonacci, implemented with a dumb recursion.
; Clojure can do much better with tail recursion and the `loop` instruction BTW.

; This is the fast version with Clojure 1.3+
(defn fastfibo ^long [^long n]
    (if (<= n 1)
        n
        (+ (fastfibo (- n 1)) (fastfibo (- n 2)))
    )
)

; This is the regular, slower Clojure version
(defn classicfibo [n]
    (if (<= n 1)
        n
        (+ (classicfibo (- n 1)) (classicfibo (- n 2)))
    )
)

; This is the fastest version with no value range checkings on +/-
(defn fastestfibo ^long [^long n]
     (if (<= n 1)
       n
       (unchecked-add (fastestfibo (unchecked-dec n)) (fastestfibo (unchecked-subtract n 2)))
      )
)


; Main
(println ">>> (classicfibo n)\n")
(dotimes [n 10] (time (println (classicfibo 40))))

(println ">>> (fastfibo n)\n")
(dotimes [n 10] (time (println (fastfibo 40))))

(println ">>> (fastestfibo n)\n")
(dotimes [n 10] (time (println (fastestfibo 40))))
