aspect ATracer {

  after() returning(long l): call(static long fib(int)) {
    // System.out.println("Returning from fib(): " + l);
    // if (l < 0) System.out.println("Error!");
  }
}
