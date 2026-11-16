// Implements quick sort by partitioning around a pivot and recursing.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// this import is needed for the List class used to store the sequence of sorting steps.
import java.util.List;

//

// Quick sort partitions the array around a pivot and recursively sorts both sides.
public class QuickSort implements SortingAlgorithm {

    public String name() { return "Quick Sort"; }

    public String description() {
        return "Pick a pivot, move smaller elements before it and larger after it, then repeat on each side.";
    }

    public String complexity() { return "Best O(n log n)  |  Average O(n log n)  |  Worst O(n²)"; }

    // Sorts the input array using the quick sort algorithm, recording each comparison and swap in a list of SortStep objects, and returns the list of steps for visualization.
    public List<SortStep> sort(int[] data, boolean ascending) {
        SortRecorder r = new SortRecorder(data, ascending);
        quick(r, 0, r.length() - 1);
        return r.finish();
    }

    private void quick(SortRecorder r, int lo, int hi) {
        if (lo > hi) {
            return;
        }
        if (lo == hi) {
            r.markSorted(lo);
            return;
        }

        int store = lo;
        for (int j = lo; j < hi; j++) {
            if (r.before(j, hi)) {
                r.swap(store, j);
                store++;
            }
        }
        r.swap(store, hi);
        r.markSorted(store);

        quick(r, lo, store - 1);
        quick(r, store + 1, hi);
    }
}
