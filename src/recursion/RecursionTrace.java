// Captures the recursive call path and operation counts for visualization.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package recursion;

// these impoorts bring in the necessary classes from the Java standard library for use in this file.
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//

// Records every call and return of the Fibonacci and factorial algorithms so
public class RecursionTrace {

    public enum Type { CALL, RETURN, STEP }

    public static final class Event {
        public final Type type;
        public final String label;
        public final String detail;
        public final int depth;      // stack size after this event
        public final long ops;       // operations so far
        public final boolean cached; // memoised answer reused

        Event(Type type, String label, String detail, int depth,
              long ops, boolean cached) {
            this.type = type;
            this.label = label;
            this.detail = detail;
            this.depth = depth;
            this.ops = ops;
            this.cached = cached;
        }
    }

    // Represents the entire trace of a recursive algorithm, including all events, the final result, the total number of operations, and the maximum depth reached during execution.
    public static final class Trace {
        public final List<Event> events;
        public final String result;
        public final long operations;
        public final int maxDepth;

        Trace(List<Event> events, String result, long operations, int maxDepth) {
            this.events = events;
            this.result = result;
            this.operations = operations;
            this.maxDepth = maxDepth;
        }
    }

    public static final int NAIVE_LIMIT = 10;
    public static final int OTHER_LIMIT = 25;

    private final List<Event> events = new ArrayList<>();
    private long ops;
    private int depth;
    private int maxDepth;
    private long[] memo;
    private boolean[] calculated;

    private RecursionTrace() {
    }

    public static boolean canAnimate(String algorithm, int n) {
        if (n < 0) {
            return false;
        }
        return algorithm.equals("Fibonacci - Naive")
                ? n <= NAIVE_LIMIT : n <= OTHER_LIMIT;
    }

    // Throws IllegalArgumentException if the input is too large to animate.
    public static Trace trace(String algorithm, int n) {
        if (!canAnimate(algorithm, n)) {
            throw new IllegalArgumentException("Too large to animate.");
        }
        RecursionTrace t = new RecursionTrace();
        String result;

        switch (algorithm) {
            case "Fibonacci - Naive":
                result = String.valueOf(t.fibNaive(n));
                break;
            case "Fibonacci - Memoized":
                t.memo = new long[n + 1];
                t.calculated = new boolean[n + 1];
                result = String.valueOf(t.fibMemo(n));
                break;
            case "Fibonacci - Bottom-up":
                result = String.valueOf(t.fibBottomUp(n));
                break;
            case "Factorial - Recursive":
                result = t.factRecursive(n).toString();
                break;
            case "Factorial - Iterative":
                result = t.factIterative(n).toString();
                break;
            default:
                throw new IllegalArgumentException("Please select a valid algorithm.");
        }
        return new Trace(t.events, result, t.ops, t.maxDepth);
    }

    // Records a single event in the trace, including the type of event, the label, the detail, the current depth of the call stack, the number of operations performed so far, and whether the result was retrieved from a cache.
    private void add(Type type, String label, String detail, boolean cached) {
        events.add(new Event(type, label, detail, depth, ops, cached));
    }

    // Records the entry into a recursive call, updating the operation count, depth, and maximum depth, and adding a CALL event to the trace.
    private void enter(String label, boolean cached) {
        ops++;
        depth++;
        maxDepth = Math.max(maxDepth, depth);
        add(Type.CALL, label, "", cached);
    }

    // Records the exit from a recursive call, updating the depth, and adding a RETURN event to the trace.
    private void leave(String label, String value) {
        depth--;
        add(Type.RETURN, label, value, false);
    }

    // Implements the naive recursive Fibonacci algorithm, recording each call and return in the trace, and returning the Fibonacci number for the given input n.
    private long fibNaive(int n) {
        String label = "fib(" + n + ")";
        enter(label, false);
        long r = n <= 1 ? n : fibNaive(n - 1) + fibNaive(n - 2);
        leave(label, String.valueOf(r));
        return r;
    }

    // Implements the memoized recursive Fibonacci algorithm, recording each call and return in the trace, and returning the Fibonacci number for the given input n, using memoization to avoid redundant calculations.
    private long fibMemo(int n) {
        String label = "fib(" + n + ")";
        boolean hit = n > 1 && calculated[n];
        enter(label, hit);
        long r;
        if (n <= 1) {
            r = n;
        } else if (hit) {
            r = memo[n];
        } else {
            r = fibMemo(n - 1) + fibMemo(n - 2);
            memo[n] = r;
            calculated[n] = true;
        }
        leave(label, String.valueOf(r));
        return r;
    }

    // Implements the bottom-up Fibonacci algorithm, recording each step in the trace, and returning the Fibonacci number for the given input n, using an iterative approach to build up the solution from the base cases.
    private long fibBottomUp(int n) {
        depth = 1;
        maxDepth = 1;
        if (n <= 1) {
            ops++;
            add(Type.STEP, "n = " + n, "F(" + n + ") = " + n, false);
            return n;
        }
        long[] v = new long[n + 1];
        v[1] = 1;
        for (int i = 2; i <= n; i++) {
            ops++;
            v[i] = v[i - 1] + v[i - 2];
            add(Type.STEP, "i = " + i,
                    "F(" + i + ") = F(" + (i - 1) + ") + F(" + (i - 2) + ") = " + v[i],
                    false);
        }
        return v[n];
    }

    // Implements the recursive factorial algorithm, recording each call and return in the trace, and returning the factorial of the given input n.
    private BigInteger factRecursive(int n) {
        String label = "fact(" + n + ")";
        enter(label, false);
        BigInteger r = n <= 1 ? BigInteger.ONE
                : BigInteger.valueOf(n).multiply(factRecursive(n - 1));
        leave(label, r.toString());
        return r;
    }

    // Implements the iterative factorial algorithm, recording each step in the trace, and returning the factorial of the given input n.
    private BigInteger factIterative(int n) {
        depth = 1;
        maxDepth = 1;
        BigInteger r = BigInteger.ONE;
        if (n < 2) {
            add(Type.STEP, "n = " + n, "result = 1", false);
            return r;
        }
        for (int i = 2; i <= n; i++) {
            ops++;
            r = r.multiply(BigInteger.valueOf(i));
            add(Type.STEP, "i = " + i, "result = " + r, false);
        }
        return r;
    }
}
