// Launches the desktop application and opens the main explorer window.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// these are the imports for the classes and packages used in this file.
import frontend.MainWindow;
import javax.swing.SwingUtilities;

// main class that serves as the entry point for the application and launches the main window.
public class Main {

    // Starts the Swing application and opens the main interface window.
    public static void main(String[] args) {

        // Ensures that the GUI is created and updated on the Event Dispatch Thread for thread safety.
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });

    }
}
