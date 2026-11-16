// Implements insertion sort by building the sorted portion one value at a time.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// this import is needed for the List class used to store the sequence of sorting steps.
import java.util.List;

//

// Insertion sort builds a sorted prefix by inserting each next value into place.
public class InsertionSort implements SortingAlgorithm {

    public String name() { return "Insertion Sort"; }

    public String description() {
        return "Take each element in turn and slide it left into its place among the already ordered ones.";
    }

    public String complexity() { return "Best O(n)  |  Average O(n²)  |  Worst O(n²)"; }

    // Sorts the input array using the insertion sort algorithm, recording each comparison and swap in a list of SortStep objects, and returns the list of steps for visualization.
    public List<SortStep> sort(int[] data, boolean ascending) {
        SortRecorder r = new SortRecorder(data, ascending);
        int n = r.length();

        for (int i = 1; i < n; i++) {
            int j = i;

            while (j > 0 && r.before(j, j - 1)) {
                r.swap(j, j - 1);
                j--;
            }
        }
        return r.finish();
    }
}
