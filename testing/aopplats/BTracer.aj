aspect BTracer {

  before(int i): call(static long fib(int)) && args(i) {
    // System.out.println("Calling fib(" + i + ")");
    // if (i < 0) System.out.println("Error!");
  }
}
