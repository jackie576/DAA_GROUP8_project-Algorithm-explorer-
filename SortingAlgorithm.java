// Defines the base contract for sorting algorithms and their captured steps.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// this import is needed for the List class used to store the sequence of sorting steps.
import java.util.List;

//

// Shared contract for all sorting algorithms used by the visualisation panels.
public interface SortingAlgorithm {

    String name();

    String description();

    String complexity();

    // Label for the second counter: swaps for most sorts, writes for merge sort.
    default String moveLabel() {
        return "Swaps";
    }

    // Sorts the input array using the algorithm, recording each comparison and swap in a list of SortStep objects, and returns the list of steps for visualization.
    List<SortStep> sort(int[] data, boolean ascending);
}
