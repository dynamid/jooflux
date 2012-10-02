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

function fibonacci(n) {
  if (n <= 1)
    return n;
  else
    return fibonacci(n-2) + fibonacci(n-1);
}

for (i=0;i<10;i++){
  var startTime = new Date().getTime();
  var elapsedTime = 0;  
  console.log("Fibo(40)=" + fibonacci(40));
  elapsedTime = new Date().getTime() - startTime;
  console.log("Took: " + elapsedTime + "ms");
}

