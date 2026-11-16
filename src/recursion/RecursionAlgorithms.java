// Runs recursive examples such as Fibonacci and factorial with measured counts.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package recursion;

// this imports the BigInteger class from the java.math package, which is used for arbitrary-precision integer arithmetic.
import java.math.BigInteger;

//

// Runs recursive algorithms such as Fibonacci and factorial while tracking work done.
public class RecursionAlgorithms {

    public static class Result {
        public final String value;
        public final long operations;

        public Result(String value, long operations) {
            this.value = value;
            this.operations = operations;
        }
    }

    // Selects and executes a recursive algorithm, then returns the answer and operation count.
    public static Result run(String algorithm, int n) {

        if (n < 0) {
            throw new IllegalArgumentException("Input cannot be negative.");
        }

        switch (algorithm) {

            case "Fibonacci - Naive":
                if (n > 40) {
                    throw new IllegalArgumentException(
                        "Naive Fibonacci is limited to n = 40."
                    );
                }
                Counter naiveCounter = new Counter();
                long naiveResult = fibonacciNaive(n, naiveCounter);
                return new Result(
                    String.valueOf(naiveResult),
                    naiveCounter.operations
                );

            case "Fibonacci - Memoized":
                if (n > 92) {
                    throw new IllegalArgumentException(
                        "Fibonacci is limited to n = 92."
                    );
                }
                Counter memoCounter = new Counter();
                long[] memo = new long[n + 1];
                boolean[] calculated = new boolean[n + 1];

                long memoResult = fibonacciMemoized(
                    n, memo, calculated, memoCounter
                );

                return new Result(
                    String.valueOf(memoResult),
                    memoCounter.operations
                );

            case "Fibonacci - Bottom-up":
                if (n > 92) {
                    throw new IllegalArgumentException(
                        "Fibonacci is limited to n = 92."
                    );
                }
                Counter bottomCounter = new Counter();
                long bottomResult = fibonacciBottomUp(n, bottomCounter);

                return new Result(
                    String.valueOf(bottomResult),
                    bottomCounter.operations
                );

            case "Factorial - Recursive":
                if (n > 5000) {
                    throw new IllegalArgumentException(
                        "Recursive factorial is limited to n = 5000 "
                        + "to avoid a stack overflow."
                    );
                }
                Counter recursiveCounter = new Counter();
                BigInteger recursiveResult =
                    factorialRecursive(n, recursiveCounter);

                return new Result(
                    recursiveResult.toString(),
                    recursiveCounter.operations
                );

            case "Factorial - Iterative":
                Counter iterativeCounter = new Counter();
                BigInteger iterativeResult =
                    factorialIterative(n, iterativeCounter);

                return new Result(
                    iterativeResult.toString(),
                    iterativeCounter.operations
                );

            default:
                throw new IllegalArgumentException(
                    "Please select a valid algorithm."
                );
        }
    }

    // Fibonacci using simple recursion
    private static long fibonacciNaive(int n, Counter counter) {
        counter.operations++;

        if (n <= 1) {
            return n;
        }

        return fibonacciNaive(n - 1, counter)
             + fibonacciNaive(n - 2, counter);
    }

    // Fibonacci using memoization
    private static long fibonacciMemoized(
            int n, long[] memo, boolean[] calculated, Counter counter) {

        counter.operations++;

        if (n <= 1) {
            return n;
        }

        if (calculated[n]) {
            return memo[n];
        }

        memo[n] = fibonacciMemoized(
            n - 1, memo, calculated, counter
        ) + fibonacciMemoized(
            n - 2, memo, calculated, counter
        );

        calculated[n] = true;
        return memo[n];
    }

    // Fibonacci using bottom-up dynamic programming
    private static long fibonacciBottomUp(int n, Counter counter) {

        if (n <= 1) {
            counter.operations++;
            return n;
        }

        long[] values = new long[n + 1];

        values[0] = 0;
        values[1] = 1;

        for (int i = 2; i <= n; i++) {
            counter.operations++;
            values[i] = values[i - 1] + values[i - 2];
        }

        return values[n];
    }

    // Factorial using recursion
    private static BigInteger factorialRecursive(
            int n, Counter counter) {

        counter.operations++;

        if (n <= 1) {
            return BigInteger.ONE;
        }

        return BigInteger.valueOf(n).multiply(
            factorialRecursive(n - 1, counter)
        );
    }

    // Factorial using iteration
    private static BigInteger factorialIterative(
            int n, Counter counter) {

        BigInteger result = BigInteger.ONE;

        for (int i = 2; i <= n; i++) {
            counter.operations++;
            result = result.multiply(BigInteger.valueOf(i));
        }

        return result;
    }

    // Counts basic algorithm operations
    private static class Counter {
        long operations = 0;
    }
}
