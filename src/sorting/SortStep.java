// Stores one atomic change from a sorting run for animation playback.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package sorting;

// One recorded moment of a sort: what happened and the array after it.
public final class SortStep {
    public enum Type { COMPARE, SWAP, WRITE, DONE }

    public final Type type;
    public final int a;
    public final int b;
    public final int[] array;
    public final boolean[] sorted;
    public final int comparisons;
    public final int moves;
    public final String description;

    public SortStep(Type type, int a, int b, int[] array, boolean[] sorted,
                    int comparisons, int moves, String description) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.array = array;
        this.sorted = sorted;
        this.comparisons = comparisons;
        this.moves = moves;
        this.description = description;
    }
}
