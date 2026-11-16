// Implements a queue with a simple array-backed data structure.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that models a simple queue with dynamic resizing and shifting.
public class CustomQueue {

    // class attributes.
    private int[] queue;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    // constructor
    public CustomQueue() {
        capacity = 10;
        queue = new int[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    // Resizes the queue array to double its current capacity.
    private void resize() {

        int[] newQueue = new int[capacity * 2];

        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[front + i];
        }

        capacity *= 2;
        queue = newQueue;
        front = 0;
        rear = size - 1;
    }

    // Appends a value to the rear of the queue and performs any needed resize or shift.
    public OperationReport enqueue(int value) {

        OperationReport report = new OperationReport();

        if (rear == capacity - 1) {

            if (front > 0) {

                for (int i = 0; i < size; i++) {
                    queue[i] = queue[front + i];
                }

                front = 0;
                rear = size - 1;

                report.addStep("Shifted queue elements to free space.");

            } else {
                resize();
                report.addStep("Queue capacity increased to " + capacity);
            }
        }

        rear++;
        queue[rear] = value;
        size++;

        report.addStep("Inserted " + value + " at the rear.");
        report.addStep("Queue size is now " + size);

        report.finish();
        return report;
    }

    // Removes the oldest value from the front and updates the remaining queue state.
    public OperationReport dequeue() {

        OperationReport report = new OperationReport();

        if (size == 0) {
            report.addStep("Queue underflow: Queue is empty.");
        } else {

            int removed = queue[front];
            front++;
            size--;

            report.addStep("Removed " + removed + " from the front.");

            if (size == 0) {
                front = 0;
                rear = -1;
            }

            report.addStep("Remaining elements: " + size);
        }

        report.finish();
        return report;
    }

    // Shows the front element without removing it, useful for checking queue state.
    public OperationReport peek() {

        OperationReport report = new OperationReport();

        if (size == 0) {
            report.addStep("Queue is empty.");
        } else {
            report.addStep("Front element: " + queue[front]);
        }

        report.finish();
        return report;
    }

    // Displays the current contents of the queue in order from front to rear.
    public OperationReport display() {

        OperationReport report = new OperationReport();

        if (size == 0) {
            report.addStep("Queue is empty.");
        } else {

            report.addStep("Queue from front to rear:");

            for (int i = 0; i < size; i++) {
                report.addStep("Element: " + queue[front + i]);
            }
        }

        report.finish();
        return report;
    }

    // Elements from front to rear (for drawing).
    public int[] getSnapshot() {
        return java.util.Arrays.copyOfRange(queue, front, front + size);
    }

    public String getContents() {

        if (size == 0) return "[ Empty Queue ]";

        StringBuilder result = new StringBuilder("FRONT → ");

        for (int i = 0; i < size; i++) {
            result.append(queue[front + i]).append(" ");
        }

        result.append("← REAR");

        return result.toString();
    }
}
