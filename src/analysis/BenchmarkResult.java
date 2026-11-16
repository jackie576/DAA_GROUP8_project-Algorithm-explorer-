// Stores one timed benchmark result for a single algorithm on one input size.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package analysis;

// This class is a simple data structure that holds the results of a benchmark for a sorting algorithm.
public class BenchmarkResult {
     
    //class attributes.
    public final String algorithm;
    public final int size;
    public final long comparisons;
    public final long moves;
    public final double millis;
    
    //class constructor that initializes the attributes of the BenchmarkResult object.
    public BenchmarkResult(String algorithm, int size, long comparisons,
                           long moves, double millis) {
        this.algorithm = algorithm;
        this.size = size;
        this.comparisons = comparisons;
        this.moves = moves;
        this.millis = millis;
    }
}
