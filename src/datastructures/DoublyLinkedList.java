// Stores ordered values in a doubly linked list with next and previous links.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that models a doubly linked list with next and previous links.
public class DoublyLinkedList {

    // this is the class that models a node in a doubly linked list with next and previous links.
    private class Node {
        int data;
        Node next;
        Node previous;

        Node(int data) {
            this.data = data;
            this.next = null;
            this.previous = null;
        }
    }

    // class attributes.
    private Node head;
    private Node tail;

    // class constructor.
    public DoublyLinkedList() {
        head = null;
        tail = null;
    }

    // Inserts a new node at the end of the list and reconnects both links for traversal.
    public OperationReport insert(int value) {

        OperationReport report = new OperationReport();

        Node newNode = new Node(value);

        if (head == null) {

            head = newNode;
            tail = newNode;

            report.addStep("Created first node: " + value);

        } else {

            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;

            report.addStep("Linked new node to previous tail.");
            report.addStep("Inserted " + value + " at the end.");
        }

        report.finish();
        return report;
    }

    // Searches for a value and removes it while preserving forward and backward references.
    public OperationReport delete(int value) {

        OperationReport report = new OperationReport();

        Node current = head;

        while (current != null && current.data != value) {

            report.addStep("Checking node: " + current.data);

            current = current.next;
        }

        if (current == null) {

            report.addStep("Value " + value + " not found.");

        } else {

            if (current.previous != null) {
                current.previous.next = current.next;
            } else {
                head = current.next;
            }

            if (current.next != null) {
                current.next.previous = current.previous;
            } else {
                tail = current.previous;
            }

            report.addStep("Deleted node: " + value);
            report.addStep("Updated previous and next references.");
        }

        report.finish();
        return report;
    }

    // Walks the list from the head until it finds the target value or reaches the end.
    public OperationReport search(int value) {

        OperationReport report = new OperationReport();

        Node current = head;
        int position = 0;

        while (current != null) {

            report.addStep("Checking position "
                    + position + ": " + current.data);

            if (current.data == value) {
                report.addStep("Found at position " + position);
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

    // Displays the current contents of the list from head to tail.
    public OperationReport displayForward() {

        OperationReport report = new OperationReport();

        Node current = head;

        if (current == null) {
            report.addStep("List is empty.");
        }

        while (current != null) {
            report.addStep("Node: " + current.data);
            current = current.next;
        }

        report.finish();
        return report;
    }

    // Displays the current contents of the list from tail to head.
    public OperationReport displayBackward() {

        OperationReport report = new OperationReport();

        Node current = tail;

        if (current == null) {
            report.addStep("List is empty.");
        }

        while (current != null) {
            report.addStep("Node: " + current.data);
            current = current.previous;
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

    // Returns a string representation of the list for debugging and visualization.
    public String getContents() {

        if (head == null) return "[ Empty List ]";

        StringBuilder result = new StringBuilder("NULL ⇄ ");

        Node current = head;

        while (current != null) {

            result.append(current.data).append(" ⇄ ");
            current = current.next;
        }

        result.append("NULL");

        return result.toString();
    }
}
