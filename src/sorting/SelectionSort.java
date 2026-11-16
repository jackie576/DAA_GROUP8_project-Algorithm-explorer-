// Implements selection sort by repeatedly choosing the smallest remaining value.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// this import is needed for the List class used to store the sequence of sorting steps.
import java.util.List;

//

// Selection sort repeatedly chooses the smallest remaining item and places it in order.
public class SelectionSort implements SortingAlgorithm {

    public String name() { return "Selection Sort"; }

    public String description() {
        return "Find the smallest remaining element and place it at the front of the unsorted part.";
    }

    public String complexity() { return "Best O(n²)  |  Average O(n²)  |  Worst O(n²)"; }

    // Sorts the input array using the selection sort algorithm, recording each comparison and swap in a list of SortStep objects, and returns the list of steps for visualization.
    public List<SortStep> sort(int[] data, boolean ascending) {
        SortRecorder r = new SortRecorder(data, ascending);
        int n = r.length();

        for (int i = 0; i < n - 1; i++) {
            int best = i;

            for (int j = i + 1; j < n; j++) {
                if (r.before(j, best)) {
                    best = j;
                }
            }

            r.swap(i, best);
            r.markSorted(i);
        }
        return r.finish();
    }
}
