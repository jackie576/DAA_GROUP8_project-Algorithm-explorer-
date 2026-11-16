// Generates the move sequence that solves the Tower of Hanoi puzzle optimally.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package recursion;

// these imports are needed for the List and ArrayList classes used to store the sequence of moves.
import java.util.ArrayList;
import java.util.List;
public class TowerOfHanoi {

    private TowerOfHanoi() {
    }

    // Computes the theoretical minimum number of moves required for the puzzle.
    public static long minimumMoves(int disks) {
        return (1L << disks) - 1;
    }

    // Generates the optimal sequence of moves that transfers every disk from peg 0 to peg 2.
    public static List<int[]> solve(int disks) {
        List<int[]> moves = new ArrayList<>();
        hanoi(disks, 0, 2, 1, moves);
        return moves;
    }

    // Recursive helper method that implements the Tower of Hanoi algorithm, moving disks from one peg to another while using a third peg as an auxiliary.
    private static void hanoi(int n, int from, int to, int via,
                              List<int[]> moves) {
        if (n == 0) {
            return;
        }
        hanoi(n - 1, from, via, to, moves);
        moves.add(new int[]{from, to});
        hanoi(n - 1, via, to, from, moves);
    }
}
