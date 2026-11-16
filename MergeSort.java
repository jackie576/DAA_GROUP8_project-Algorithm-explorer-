// Implements merge sort by splitting values and merging them back in order.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// this import is needed for the List class used to store the sequence of sorting steps.
import java.util.List;

//

// Merge sort divides the array, sorts each half, then merges the results.
public class MergeSort implements SortingAlgorithm {

    public String name() { return "Merge Sort"; }

    public String description() {
        return "Split the array in halves, sort each half, then merge the two sorted halves.";
    }

    public String complexity() { return "Best O(n log n)  |  Average O(n log n)  |  Worst O(n log n)"; }

    @Override
    public String moveLabel() { return "Writes"; }

    // Sorts the input array using the merge sort algorithm, recording each comparison and write in a list of SortStep objects, and returns the list of steps for visualization.
    public List<SortStep> sort(int[] data, boolean ascending) {
        SortRecorder r = new SortRecorder(data, ascending);
        split(r, 0, r.length() - 1);
        return r.finish();
    }

    private void split(SortRecorder r, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int mid = (lo + hi) >>> 1;
        split(r, lo, mid);
        split(r, mid + 1, hi);
        merge(r, lo, mid, hi);
    }

    private void merge(SortRecorder r, int lo, int mid, int hi) {
        int[] tmp = new int[hi - lo + 1];
        for (int k = 0; k < tmp.length; k++) {
            tmp[k] = r.get(lo + k);
        }

        int left = lo;
        int right = mid + 1;
        int out = lo;

        while (left <= mid && right <= hi) {
            int leftValue = tmp[left - lo];
            int rightValue = tmp[right - lo];

            if (r.before(right, rightValue, left, leftValue)) {
                r.write(out++, rightValue);
                right++;
            } else {
                r.write(out++, leftValue);
                left++;
            }
        }
        while (left <= mid) {
            r.write(out++, tmp[left++ - lo]);
        }
        while (right <= hi) {
            r.write(out++, tmp[right++ - lo]);
        }
    }
}
