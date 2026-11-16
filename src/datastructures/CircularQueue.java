// Models a fixed-size circular queue with wrap-around indexing.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that models a fixed-size circular queue with wrap-around indexing.
public class CircularQueue {

    // class attributes.
    private int[] queue;
    private int front;
    private int rear;
    private int size;
    private final int capacity;

    // class constructor.
    public CircularQueue() {
        capacity = 10;
        queue = new int[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    // Inserts a value into the circular buffer, wrapping around once the rear reaches the end.
    public OperationReport enqueue(int value) {

        OperationReport report = new OperationReport();

        if (size == capacity) {

            report.addStep("Queue overflow: Circular queue is full.");

        } else {

            rear = (rear + 1) % capacity;
            queue[rear] = value;
            size++;

            report.addStep("Inserted " + value + " at position " + rear);
            report.addStep("Rear moved to position " + rear);
            report.addStep("Queue size: " + size);
        }

        report.finish();
        return report;
    }

    // Removes the item at the front and advances the ring index to keep the queue circular.
    public OperationReport dequeue() {

        OperationReport report = new OperationReport();

        if (size == 0) {

            report.addStep("Queue underflow: Queue is empty.");

        } else {

            int removed = queue[front];

            report.addStep("Removed element: " + removed);

            front = (front + 1) % capacity;
            size--;

            report.addStep("Front moved to position " + front);
            report.addStep("Remaining elements: " + size);

            if (size == 0) {
                front = 0;
                rear = -1;
            }
        }

        report.finish();
        return report;
    }

    // Returns the current front value without removing it so the queue can be inspected.
    public OperationReport peek() {

        OperationReport report = new OperationReport();

        if (size == 0) {
            report.addStep("Circular queue is empty.");
        } else {
            report.addStep("Front element: " + queue[front]);
        }

        report.finish();
        return report;
    }

    // Displays the current contents of the circular queue in order from front to rear.
    public OperationReport display() {

        OperationReport report = new OperationReport();

        if (size == 0) {
            report.addStep("Circular queue is empty.");
        } else {

            for (int i = 0; i < size; i++) {

                int index = (front + i) % capacity;

                report.addStep(
                        "Position " + index + ": " + queue[index]
                );
            }
        }

        report.finish();
        return report;
    }

    // Raw slots, so the ring can be drawn with its wrap-around.
    // This is used for the visual representation of the circular queue in the UI.
    public int[] getSlots() {
         return queue.clone();
   }

   // Returns the current front index of the circular queue.
    public int getFront() {
         return front; 
    }

    // Returns the current rear index of the circular queue.
    public int getRear() {
         return rear; 
    }

    // Returns the current size of the circular queue.
    public int getSize() { 
        return size; 
    }

    // Returns the maximum capacity of the circular queue.
    public int getCapacity() {
         return capacity; 
    }

    // Returns a string representation of the circular queue's contents from front to rear.
    public String getContents() {

        if (size == 0) return "[ Empty Circular Queue ]";

        StringBuilder result = new StringBuilder("FRONT → ");

        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            result.append(queue[index]).append(" ");
        }

        result.append("← REAR");

        return result.toString();
    }
}
