# JooFlux
#    
# Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
# Copyright (c) 2012 Julien Ponge, INSA-Lyon
# Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

def fib(n)
  if n <= 1
    n
  else
    fib(n - 1) + fib(n - 2)
  end
end

for i in 1..10 do
  start = Time.now
  puts fib(40)
  finish = Time.now
  puts "Took: #{finish - start}s"
end

