// Runs benchmark trials across several sizes and compares algorithm cost.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package analysis;

// these are the imports for the classes used in this file.
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

// Runs timed experiments. These are plain counting versions of the five
public class PerformanceAnalyzer {
    
    //method that contains the names of the algorithms to be benchmarked.
    public static final String[] ALGORITHMS = {
        "Bubble Sort", "Selection Sort", "Insertion Sort",
        "Merge Sort", "Quick Sort"
    };

    // These are the names of the input arrangements.
    public static final String RANDOM = "Random Input";
    public static final String SORTED = "Sorted Input";
    public static final String REVERSE = "Reverse Sorted Input";

    //private constructor to prevent instantiation of the PerformanceAnalyzer class.
    public PerformanceAnalyzer() {
    }

    // Generates an array of n random integers.
    public static int[] generate(String arrangement, int n, long seed) {
        int[] data = new int[n];
        Random random = new Random(seed);
        for (int i = 0; i < n; i++) {
            switch (arrangement) {
                case SORTED: data[i] = i + 1; break;
                case REVERSE: data[i] = n - i; break;
                default: data[i] = 1 + random.nextInt(n * 10);
            }
        }
        return data;
    }

    // Up to eight increasing sizes ending at max.
    public static int[] sizes(int max) {
        Set<Integer> set = new LinkedHashSet<>();
        for (int i = 1; i <= 8; i++) {
            set.add(Math.max(1, (int) Math.round(max * i / 8.0)));
        }
        return set.stream().mapToInt(Integer::intValue).toArray();
    }

    // Runs every algorithm once so the JVM has compiled them before timing.
    public static void warmUp() {
        int[] data = generate(RANDOM, 1500, 7);
        for (String name : ALGORITHMS) {
            for (int i = 0; i < 3; i++) {
                sort(name, data.clone(), new OperationCounter());
            }
        }
    }

    // Runs a single benchmark trial for the given algorithm and input data.
    public static BenchmarkResult benchmark(String algorithm, int[] data) {
        OperationCounter counter = new OperationCounter();
        int repeats = data.length <= 3000 ? 3 : 1;
        long best = Long.MAX_VALUE;
 
        // Run the algorithm several times and keep the best time.
        for (int r = 0; r < repeats; r++) {
            int[] copy = data.clone();
            counter.reset();
            long start = System.nanoTime();
            sort(algorithm, copy, counter);
            best = Math.min(best, System.nanoTime() - start);
        }
        return new BenchmarkResult(algorithm, data.length, counter.comparisons,
                counter.moves, best / 1_000_000.0);
    }

    // Sorts the given array using the requested  algorithm.
    static void sort(String algorithm, int[] a, OperationCounter c) {

        // this switch Calls the requested sorting method.
        switch (algorithm) {
            case "Bubble Sort": bubble(a, c);
             break;
            case "Selection Sort": selection(a, c);
             break;
            case "Insertion Sort": insertion(a, c);
             break;
            case "Merge Sort": mergeSort(a, new int[a.length], 0, a.length - 1, c);
             break;
            case "Quick Sort": quick(a, 0, a.length - 1, c);
             break;
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    // Swaps two elements in an array.
    private static void swap(int[] a, int i, int j, OperationCounter c) {
        if (i == j) {
            return;
        }
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        c.moves++;
    }

    // method to implement the bubble sort algorithm.
    private static void bubble(int[] a, OperationCounter c) {
        int n = a.length;
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - pass; j++) {
                c.comparisons++;
                if (a[j + 1] < a[j]) {
                    swap(a, j, j + 1, c);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    // method to implement the selection sort algorithm.
    private static void selection(int[] a, OperationCounter c) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int best = i;
            for (int j = i + 1; j < n; j++) {
                c.comparisons++;
                if (a[j] < a[best]) {
                    best = j;
                }
            }
            swap(a, i, best, c);
        }
    }

    // method to implement the insertion sort algorithm.
    private static void insertion(int[] a, OperationCounter c) {
        for (int i = 1; i < a.length; i++) {
            int j = i;
            while (j > 0) {
                c.comparisons++;
                if (a[j] < a[j - 1]) {
                    swap(a, j, j - 1, c);
                    j--;
                } else {
                    break;
                }
            }
        }
    }

    // method to implement the merge sort algorithm.
    private static void mergeSort(int[] a, int[] tmp, int lo, int hi,
                                  OperationCounter c) {
        if (lo >= hi) {
            return;
        }
        int mid = (lo + hi) >>> 1;
        mergeSort(a, tmp, lo, mid, c);
        mergeSort(a, tmp, mid + 1, hi, c);

        System.arraycopy(a, lo, tmp, lo, hi - lo + 1);
        int left = lo;
        int right = mid + 1;
        int out = lo;
        while (left <= mid && right <= hi) {
            c.comparisons++;
            if (tmp[right] < tmp[left]) {
                a[out++] = tmp[right++];
            } else {
                a[out++] = tmp[left++];
            }
            c.moves++;
        }
        while (left <= mid) {
            a[out++] = tmp[left++];
            c.moves++;
        }
        while (right <= hi) {
            a[out++] = tmp[right++];
            c.moves++;
        }
    }

    // method to implement the quick sort algorithm.
    // Same comparisons as the visual quick sort; recurses into the smaller side.
    private static void quick(int[] a, int lo, int hi, OperationCounter c) {
        while (lo < hi) {
            int store = lo;
            for (int j = lo; j < hi; j++) {
                c.comparisons++;
                if (a[j] < a[hi]) {
                    swap(a, store, j, c);
                    store++;
                }
            }
            swap(a, store, hi, c);

            if (store - lo < hi - store) {
                quick(a, lo, store - 1, c);
                lo = store + 1;
            } else {
                quick(a, store + 1, hi, c);
                hi = store - 1;
            }
        }
    }
}
