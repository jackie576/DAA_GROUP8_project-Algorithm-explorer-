package sorting;

public class InsertionSort {

    // Sorts the input array in ascending order and returns a sorted copy.
    public int[] sort(int[] input) {
        int[] arr = input.clone(); // Preserve the original input array.

        // Start at index 1 because a one-element section is already sorted.
        for (int i = 1; i < arr.length; i++) {

            int key = arr[i]; // Value to insert into the sorted left-hand section.
            int j = i - 1;    // Begin comparing with the value immediately before key.

            // Shift larger values one position to the right to make room for key.
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j]; // Move the larger value one place right.
                j--;                 // Continue checking towards the start.
            }

            // Insert key into the correct position in the sorted section.
            arr[j + 1] = key;
        }

        return arr; // Return the sorted copy.
    }
}
