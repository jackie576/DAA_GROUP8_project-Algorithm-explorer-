// Displays the recursion examples and lets the user choose a problem to explore.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import javax.swing.*;

// This class provides a panel for exploring recursion algorithms, allowing users to select and view different examples.
public class RecursionPanel extends JPanel {

    private final CardLayout cards = new CardLayout();
    private final JPanel stack = new JPanel(cards);
    private final RecursionRunPanel run;
    private final HanoiPanel hanoi;

    public RecursionPanel() {

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        // List of algorithms to be displayed in the recursion panel.
        String[] algorithms = {
            "Fibonacci - Naive",
            "Fibonacci - Memoized",
            "Fibonacci - Bottom-up",
            "Factorial - Recursive",
            "Factorial - Iterative",
            "Tower of Hanoi"
        };

        run = new RecursionRunPanel(algorithms, this::choose);
        hanoi = new HanoiPanel(algorithms, this::choose);

        stack.setOpaque(false);
        stack.add(run, "run");
        stack.add(hanoi, "hanoi");
        add(stack, BorderLayout.CENTER);
    }

    // Keeps both pages' algorithm lists in step and shows the right page.
    private void choose(String name) {
        if ("Tower of Hanoi".equals(name)) {
            hanoi.select(name);
            cards.show(stack, "hanoi");
        } else {
            run.select(name);
            cards.show(stack, "run");
        }
    }
}
