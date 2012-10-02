aspect ATracer {

  after() returning(long l): call(static long fib(int)) {
    // System.out.println("Returning from fib(): " + l);
    // if (l < 0) System.out.println("Error!");
  }

  before(int i): call(static long fib(int)) && args(i) {
    // System.out.println("Calling fib(" + i + ")");
    // if (i < 0) System.out.println("Error!");
  }
}
