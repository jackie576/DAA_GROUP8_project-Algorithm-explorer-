// Stores values in a singly linked list with head-to-tail traversal.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that models a singly linked list with head-to-tail traversal.
public class SinglyLinkedList {

    // this is the class that models a node in a singly linked list with head-to-tail traversal.
    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;

    public SinglyLinkedList() {
        head = null;
    }

    // Appends a value to the end of the list by walking to the last node.
    public OperationReport insert(int value) {

        OperationReport report = new OperationReport();

        Node newNode = new Node(value);

        if (head == null) {

            head = newNode;
            report.addStep("Created first node: " + value);

        } else {

            Node current = head;

            report.addStep("Starting traversal from head.");

            while (current.next != null) {
                current = current.next;
                report.addStep("Moved to node: " + current.data);
            }

            current.next = newNode;

            report.addStep("Inserted " + value + " at the end.");
        }

        report.finish();
        return report;
    }

    // Removes the first matching node while keeping the remaining list connected.
    public OperationReport delete(int value) {

        OperationReport report = new OperationReport();

        if (head == null) {

            report.addStep("List is empty.");

        } else if (head.data == value) {

            head = head.next;
            report.addStep("Deleted head node: " + value);

        } else {

            Node current = head;

            while (current.next != null
                    && current.next.data != value) {

                current = current.next;
                report.addStep("Searching node: " + current.data);
            }

            if (current.next == null) {

                report.addStep("Value " + value + " was not found.");

            } else {

                current.next = current.next.next;
                report.addStep("Deleted node: " + value);
            }
        }

        report.finish();
        return report;
    }

    // Traverses the linked list and reports the index of the first matching value.
    public OperationReport search(int value) {

        OperationReport report = new OperationReport();

        Node current = head;
        int position = 0;

        while (current != null) {

            report.addStep("Checking position " + position
                    + ": " + current.data);

            if (current.data == value) {
                report.addStep("Value found at position " + position);
                report.finish();
                return report;
            }

            current = current.next;
            position++;
        }

        report.addStep("Value " + value + " not found.");

        report.finish();
        return report;
    }

    // Displays the current contents of the linked list from head to tail.
    public OperationReport display() {

        OperationReport report = new OperationReport();

        Node current = head;

        if (current == null) {
            report.addStep("Linked list is empty.");
        }

        while (current != null) {

            report.addStep("Node value: " + current.data);

            current = current.next;
        }

        report.finish();
        return report;
    }

    // Values from head to tail (for drawing).
    public int[] getSnapshot() {
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        for (Node n = head; n != null; n = n.next) {
            list.add(n.data);
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    public String getContents() {

        if (head == null) return "[ Empty List ]";

        StringBuilder result = new StringBuilder("HEAD → ");

        Node current = head;

        while (current != null) {
            result.append(current.data).append(" → ");
            current = current.next;
        }

        result.append("NULL");

        return result.toString();
    }
}
