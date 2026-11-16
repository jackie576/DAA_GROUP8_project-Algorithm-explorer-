// Captures a single data-structure operation and the resulting state change.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that captures a single data-structure operation and the resulting state change.
public class OperationReport {

    // class attributes.
    private final StringBuilder steps;
    private int operationCount;
    private final long startTime;
    private long executionTime;

    // class constructor.
    public OperationReport() {
        steps = new StringBuilder();
        operationCount = 0;
        startTime = System.nanoTime();
    }

    // class that adds a step to the report with a description of the operation performed and the resulting state change.
    public void addStep(String description) {
        operationCount++;
        steps.append("Step ")
             .append(operationCount)
             .append(": ")
             .append(description)
             .append("\n");
    }

    // class that marks the end of the operation report and calculates the total execution time.
    public void finish() {
        executionTime = System.nanoTime() - startTime;
    }

    // class that returns the total number of operations in the report.
    public int getOperationCount() {
        return operationCount;
    }

    // class that returns the total execution time of the operations in nanoseconds.
    public long getExecutionTime() {
        return executionTime;
    }

    // class that returns the steps of the operation report as a string.
    public String getSteps() {
        return steps.toString();
    }

    // class that returns a string representation of the operation report, including the steps, total operations, and execution time.
    @Override
    public String toString() {
        return steps.toString()
                + "\n--------------------------------\n"
                + "Total operations: " + operationCount
                + "\nExecution time: " + executionTime + " ns"
                + "\n--------------------------------";
    }
}
