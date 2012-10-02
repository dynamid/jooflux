# JooFlux
#    
# Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
# Copyright (c) 2012 Julien Ponge, INSA-Lyon
# Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

class Fibonacci

  def initialize
    @fib_fun = Fibonacci.instance_method(:fib).bind(self)
    self
  end

  def go(n)
    @fib_fun.call(n)
  end

  def fib(n)
    if n <= 1
      n
    else
      @fib_fun.call(n - 1) + @fib_fun.call(n - 2)
    end
  end
  
end

for i in 1..10 do
  start = Time.now
  puts Fibonacci.new.go(40)
  finish = Time.now
  puts "Took: #{finish - start}s"
end

