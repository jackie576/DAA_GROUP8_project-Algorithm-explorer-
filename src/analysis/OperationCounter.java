// Tracks comparisons and move counts while an algorithm runs.
// This file is mainly seen in the ui.
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package analysis;

// the operation counter class.
public class OperationCounter {
    
    //class attributes.
    public long comparisons;
    public long moves;
    
    //method that resets the comparisons and move counts to zero.
    public void reset() {
        comparisons = 0;
        moves = 0;
    }
}
