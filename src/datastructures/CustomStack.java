// Implements a stack using a resizeable array for visual learning.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that models a stack using a resizeable array.
public class CustomStack {

    // class attributes.
    private int[] stack;
    private int top;
    private int capacity;

    // class constructor.
    public CustomStack() {
        capacity = 10;
        stack = new int[capacity];
        top = -1;
    }

    // Doubles the capacity of the stack array when it is full.
    private void resize() {
        int[] newStack = new int[capacity * 2];

        for (int i = 0; i <= top; i++) {
            newStack[i] = stack[i];
        }

        capacity *= 2;
        stack = newStack;
    }

    // Adds a value to the top of the stack and records the exact state change.
    public OperationReport push(int value) {

        OperationReport report = new OperationReport();

        if (top == capacity - 1) {
            resize();
            report.addStep("Stack capacity increased to " + capacity);
        }

        top++;
        stack[top] = value;

        report.addStep("Pushed " + value + " onto the stack.");
        report.addStep("New top position is " + top);

        report.finish();
        return report;
    }

    // Removes the current top item and reports whether the stack was empty.
    public OperationReport pop() {

        OperationReport report = new OperationReport();

        if (top == -1) {
            report.addStep("Stack underflow: Stack is empty.");
        } else {
            int removed = stack[top];
            top--;

            report.addStep("Removed element: " + removed);
            report.addStep("Updated top position: " + top);
        }

        report.finish();
        return report;
    }

    // Returns the top value without removing it so the UI can inspect the stack safely.
    public OperationReport peek() {

        OperationReport report = new OperationReport();

        if (top == -1) {
            report.addStep("Stack is empty.");
        } else {
            report.addStep("Top element is: " + stack[top]);
        }

        report.finish();
        return report;
    }

    // Displays the current contents of the stack in order from top to bottom.
    public OperationReport display() {

        OperationReport report = new OperationReport();

        if (top == -1) {
            report.addStep("Stack is empty.");
        } else {
            report.addStep("Displaying stack from top to bottom:");

            for (int i = top; i >= 0; i--) {
                report.addStep("Position " + i + ": " + stack[i]);
            }
        }

        report.finish();
        return report;
    }

    // Elements from bottom to top (for drawing).
    public int[] getSnapshot() {
        return java.util.Arrays.copyOf(stack, top + 1);
    }

    public String getContents() {

        if (top == -1) return "[ Empty Stack ]";

        StringBuilder result = new StringBuilder("TOP → ");

        for (int i = top; i >= 0; i--) {
            result.append(stack[i]).append(" ");
        }

        return result.toString();
    }
}
