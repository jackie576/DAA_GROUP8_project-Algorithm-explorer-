// Records each sort step so the animation can replay the process accurately.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// these imports are needed for the List and ArrayList classes used to store the sequence of sorting steps.
import java.util.ArrayList;
import java.util.List;

//

// Helper used by every sorting algorithm. The algorithm does its normal work

// through this class, which performs each operation on its own copy of the
public class SortRecorder {

    private final int[] array;
    private final boolean[] sorted;
    private final boolean ascending;
    private final List<SortStep> steps = new ArrayList<>();
    private int comparisons;
    private int moves;

    public SortRecorder(int[] data, boolean ascending) {
        this.array = data.clone();
        this.sorted = new boolean[data.length];
        this.ascending = ascending;
    }

    public int length() {
        return array.length;
    }

    public int get(int i) {
        return array[i];
    }

    // Compares two values and decides whether the first item belongs before the second.
    public boolean before(int idxA, int valA, int idxB, int valB) {
        comparisons++;
        record(SortStep.Type.COMPARE, idxA, idxB,
                "Comparing indices " + idxA + " and " + idxB);
        return ascending ? valA < valB : valA > valB;
    }

    public boolean before(int i, int j) {
        return before(i, array[i], j, array[j]);
    }

    // Swaps two entries in the working array and records the move for animation.
    public void swap(int i, int j) {
        if (i == j) {
            return;
        }
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
        moves++;
        record(SortStep.Type.SWAP, i, j,
                "Swapped " + array[j] + " and " + array[i]);
    }

    // Writes a value into a slot and tracks that the array changed as part of the sort.
    public void write(int i, int value) {
        array[i] = value;
        moves++;
        record(SortStep.Type.WRITE, i, -1,
                "Wrote " + value + " at index " + i);
    }

    // Marks a position as final; shown from the next recorded step on.
    public void markSorted(int i) {
        sorted[i] = true;
    }

    // Finalizes the recorded run and marks every index as sorted for the final frame.
    public List<SortStep> finish() {
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = true;
        }
        record(SortStep.Type.DONE, -1, -1, "Array is sorted");
        return steps;
    }

    private void record(SortStep.Type type, int a, int b, String text) {
        steps.add(new SortStep(type, a, b, array.clone(), sorted.clone(),
                comparisons, moves, text));
    }
}
