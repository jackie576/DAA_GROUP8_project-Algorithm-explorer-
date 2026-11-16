// Creates the main application shell and switches among the explorer screens.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

//

// Application shell that hosts the explorer tabs and shared navigation controls.
public class MainWindow extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> navigationButtons =
            new LinkedHashMap<>();

    // Builds the full explorer shell and switches the visible page when the user clicks navigation.
    public MainWindow() {

        setTitle("Algorithm Explorer | GROUP 8");
        setSize(1250, 780);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);
        add(new GroupBanner(), BorderLayout.SOUTH);

        showPage("recursion");
    }

    // Creates the sidebar with navigation buttons for different sections of the explorer.
    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(Theme.SURFACE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        JLabel logo = new JLabel("    ALGORITHM");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Theme.ACCENT);

        JLabel logo2 = new JLabel("       EXPLORER");
        logo2.setFont(new Font("Segoe UI", Font.BOLD, 19));
        logo2.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel("       LEARN • VISUALIZE • ANALYZE");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        subtitle.setForeground(Theme.MUTED);

        sidebar.add(logo);
        sidebar.add(logo2);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        JLabel menuLabel = new JLabel("  MAIN MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuLabel.setForeground(Theme.MUTED);

        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(15));

        addNavigation(sidebar, "Recursion", "recursion");
        addNavigation(sidebar, "Data Structures", "data");
        addNavigation(sidebar, "Sorting", "sorting");
        addNavigation(sidebar, "Complexity Analysis", "analysis");

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    // Adds a navigation button to the sidebar that switches to the specified page when clicked.
    private void addNavigation(
            JPanel sidebar, String title, String page
    ) {

        JButton button = new JButton("   " + title);

        button.setMaximumSize(new Dimension(220, 48));
        button.setPreferredSize(new Dimension(215, 48));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 12, 12, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showPage(page));

        navigationButtons.put(page, button);

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(9));
    }

    // Creates the main content area with a top bar and a content panel.
    private JPanel createMainArea() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.BACKGROUND);

        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Theme.SURFACE);
        topbar.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel pageLabel = new JLabel("INTERACTIVE LEARNING PLATFORM");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageLabel.setForeground(Theme.MUTED);

        JLabel status = new JLabel("●  SYSTEM READY");
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(Theme.SUCCESS);

        topbar.add(pageLabel, BorderLayout.WEST);
        topbar.add(status, BorderLayout.EAST);

        contentPanel.setBackground(Theme.BACKGROUND);

        contentPanel.add(new RecursionPanel(), "recursion");
        contentPanel.add(new DataStructuresPanel(), "data");
        contentPanel.add(new SortingPanel(), "sorting");
        contentPanel.add(new AnalysisPanel(), "analysis");

        main.add(topbar, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);

        return main;
    }

    // Displays the selected panel and updates the active navigation button styling.
    private void showPage(String page) {

        cardLayout.show(contentPanel, page);

        for (Map.Entry<String, JButton> entry
                : navigationButtons.entrySet()) {

            JButton button = entry.getValue();

            if (entry.getKey().equals(page)) {
                button.setBackground(Theme.ACCENT);
                button.setForeground(Theme.SURFACE);
            } else {
                button.setBackground(Theme.SURFACE);
                button.setForeground(Theme.TEXT);
            }

            button.setOpaque(true);
        }
    }
}
