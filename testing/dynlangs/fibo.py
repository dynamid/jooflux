# JooFlux
#    
# Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
# Copyright (c) 2012 Julien Ponge, INSA-Lyon
# Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

# Helper
def timed(f):
    from functools import wraps
    from time import time
    
    @wraps(f)
    def wrapper(*args, **kwds):
        start = time()
        result = f(*args, **kwds)
        elapsed = time() - start
        print "%s took %f secs to finish" % (f.__name__, elapsed)
        return result
    return wrapper

# Fibonacci for Jython
def fibo(n):
    if n <= 1:
        return n
    else:
        return fibo(n - 1) + fibo(n - 2)

# Main
@timed
def main():
    print(fibo(40))

if __name__ == '__main__':
    for i in range(1, 11):
        main()
