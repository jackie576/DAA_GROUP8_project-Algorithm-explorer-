# Algorithm Explorer

**An Interactive Java-Based Platform for Learning, Visualizing, and Analyzing Algorithms and Data Structures.**

## 1. Project Overview

Algorithm Explorer is a desktop application developed using Java Swing to make learning algorithms and data structures more interactive and practical.

The application provides an environment where users can execute algorithms, observe how they work, visualize operations, and analyze their performance.

It combines algorithm implementation, step-by-step visualization, and performance benchmarking in one graphical user interface.

The project is organized into four main modules:

- Recursion
- Data Structures
- Sorting Algorithms
- Complexity Analysis

## 2. Key Features

### 2.1 Recursion Explorer

Allows users to execute and explore recursive and iterative algorithms.

Implemented algorithms:

- Fibonacci (Naive Recursive)
- Fibonacci (Memoized)
- Fibonacci (Bottom-up)
- Factorial (Recursive)
- Factorial (Iterative)
- Tower of Hanoi

**Visualization:**
- Graphical representation of the Tower of Hanoi puzzle.
- Three pegs with disks displayed in their respective positions.
- Automatic disk movement.
- Move counter showing the progress of the solution.

### 2.2 Data Structures Explorer

Provides practical implementations of fundamental data structures.

Implemented structures and operations include:

- Array-based Stack
- Linear Queue
- Circular Queue
- Singly Linked List
- Doubly Linked List
- Balanced Brackets Checking

Users can interact with the structures and observe how their operations affect the stored elements.

### 2.3 Sorting Laboratory

Provides an interactive environment for exploring and comparing sorting algorithms.

Implemented sorting algorithms:

- Bubble Sort
- Selection Sort
- Insertion Sort
- Merge Sort
- Quick Sort

**Features:**
- User-provided array input.
- Random number generation.
- Step-by-step sorting execution.
- Automatic sorting animation.
- Visual representation of array elements.
- Display of comparisons and movements.
- Comparison of multiple sorting algorithms.

Quick Sort uses the first element as its pivot in the standard implementation.

### 2.4 Complexity Analysis

Provides a benchmarking environment for evaluating sorting algorithm performance.

**Features:**
- Execution time measurement.
- Comparison counting.
- Element movement counting.
- Testing with different input arrangements.
- Comparison of algorithm performance.

Input arrangements include:

- Random
- Sorted
- Reverse Sorted

Benchmark array sizes include:

- 100
- 500
- 1,000
- 2,000
- 5,000
- 10,000

Each benchmark is repeated three times, and average results are calculated to support performance comparisons.

## 3. Technologies Used

| Technology | Purpose |
|---|---|
| Java | Core programming language |
| Java Swing | Desktop graphical user interface |
| AWT | GUI layout, graphics and rendering |
| Java Collections | Supporting internal data handling |
| VS Code | Development environment |
| JDK 21 | Java development and execution |

The application uses Java's built-in libraries and does not require external dependencies.


## 4. System Requirements

To run the application, you need:

- Java Development Kit (JDK) 26 or a compatible newer JDK.
- A computer running Windows, Linux, or macOS.
- A terminal or command prompt or vs code.
- The project source code.

## 5. Installation and Setup

### Step 1: Clone the Repository

Open your terminal and execute:

```bash
git clone https://github.com/jackie576/DAA_GROUP8_project-Algorithm-explorer- 
```

### Step 2: Navigate to the Project Folder

```bash
cd repository name.
```



### Step 3: Run the Application
look for main.java in the source code and run in vs code.

The Algorithm Explorer graphical interface should now launch.

## 6. How to Use the Application

1. Launch the application.
2. Select the required module from the navigation panel.
3. Choose an algorithm or data structure.
4. Enter the required input.
5. Execute the operation.
6. Observe the results, visualizations, and performance information displayed by the application.

For sorting, users can watch the sorts happen graphically.

For Tower of Hanoi, users can enter the number of disks and watch the recursive solution execute graphically and if you want you can play manually.

## 7. Algorithm Complexity Reference

| Algorithm | Best Case | Average Case | Worst Case |
|---|---|---|---|
| Bubble Sort | O(n) | O(n²) | O(n²) |
| Selection Sort | O(n²) | O(n²) | O(n²) |
| Insertion Sort | O(n) | O(n²) | O(n²) |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) |
| Quick Sort | O(n log n) | O(n log n) | O(n²) |

These are the standard theoretical time complexities. Actual execution times may vary depending on the input and computer hardware.

## 8. Educational Objectives

Algorithm Explorer was developed to support practical understanding of:

- Recursive and iterative problem-solving.
- Fundamental data structures and their operations.
- Sorting algorithms and their execution processes.
- Time and space complexity.
- Algorithm performance measurement.
- Comparing theoretical complexity with experimental results.
- Java programming and graphical user interface development.

## 9. Project Status

**Development Status: Completed**

The four main modules have been developed and integrated into the desktop application.

## 10. Contributors

This project was developed as part of a group academic project for the Design and Analysis of Algorithms course.

OTHIENO DAVID
GHAI SAMUEL MACH
NAKIRE JACKLINE
TAYEBWA PEDSON
SEMWANGA ROBERT
KYEMBA MOHAMMED

## 11. License

This project was developed for educational and academic purposes.
