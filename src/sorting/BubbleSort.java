// Implements the classic bubble-sort algorithm and records each comparison.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// this import is needed for the List class used to store the sequence of sorting steps.
import java.util.List;

//

// Bubble sort repeatedly compares adjacent items and swaps any out-of-order pair.
public class BubbleSort implements SortingAlgorithm {

    public String name() { return "Bubble Sort"; }

    public String description() {
        return "Repeatedly compare adjacent elements and swap them if they are in the wrong order.";
    }

    public String complexity() { return "Best O(n)  |  Average O(n²)  |  Worst O(n²)"; }

    // Sorts the input array using the bubble sort algorithm, recording each comparison and swap in a list of SortStep objects, and returns the list of steps for visualization.
    public List<SortStep> sort(int[] data, boolean ascending) {
        SortRecorder r = new SortRecorder(data, ascending);
        int n = r.length();

        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;

            for (int j = 0; j < n - 1 - pass; j++) {
                if (r.before(j + 1, j)) {
                    r.swap(j, j + 1);
                    swapped = true;
                }
            }

            r.markSorted(n - 1 - pass);

            if (!swapped) {
                break;
            }
        }
        return r.finish();
    }
}
