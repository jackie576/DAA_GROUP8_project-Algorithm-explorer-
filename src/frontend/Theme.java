// Defines the shared color palette and visual styling used across the interface.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// imports the necessary classes for working with colors, fonts, and Swing components.
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

//

// Shared colour, spacing, and border styles used by the desktop UI.
public class Theme {

    // Main colour palette
    // Page background between the boxes: light cream-yellow. Change it here only.
    public static final Color BACKGROUND = new Color(248, 242, 218);
    // Text that sits directly on the page background
    public static final Color ON_BACKGROUND = new Color(43, 39, 34);
    public static final Color ON_BACKGROUND_MUTED = new Color(110, 100, 78);
    // Boxes, sidebar, top bar and inputs: light gray with a hint of sand
    public static final Color SURFACE = new Color(236, 233, 225);
    public static final Color ACCENT = new Color(32, 199, 217);
    public static final Color ACCENT_LIGHT = new Color(224, 247, 249);
    public static final Color TEXT = new Color(43, 39, 34);
    public static final Color MUTED = new Color(124, 116, 105);
    public static final Color BORDER = new Color(205, 199, 183);
    public static final Color SUCCESS = new Color(22, 138, 150);

    // Sorting visualisation colours (solid, no gradients)
    public static final Color BAR_UNSORTED = new Color(36, 140, 210);
    public static final Color BAR_COMPARING = new Color(240, 140, 30);
    public static final Color BAR_SORTED = new Color(46, 160, 90);
    public static final Color GAUGE_TIME = new Color(110, 170, 50);
    public static final Color GRID = new Color(214, 209, 196);

    // Corner radius used by every box and button (small = sharper)
    public static final int RADIUS = 4;

    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 25);

    public static final Font HEADING_FONT =
            new Font("Segoe UI", Font.BOLD, 17);

    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    public static final Font SMALL_FONT =
            new Font("Segoe UI", Font.PLAIN, 12);

    private Theme() {
    }

    private static Font cantarell;

    //

    // Cantarell Extra Bold at the given size. Looks for fonts/Cantarell-ExtraBold.ttf

    // next to the program, then an installed Cantarell, then falls back to a bold

    // sans-serif so the app still runs without the font.

    //
    public static Font marqueeFont(float size) {
        if (cantarell == null) {
            cantarell = loadCantarell();
        }
        return cantarell.deriveFont(size);
    }

    private static Font loadCantarell() {
        for (String path : new String[]{"fonts/Cantarell-ExtraBold.ttf",
                "../fonts/Cantarell-ExtraBold.ttf", "Cantarell-ExtraBold.ttf"}) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                try {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, file);
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .registerFont(font);
                    return font;
                } catch (Exception ignored) {
                    // try the next option
                }
            }
        }
        java.util.List<String> installed = java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames());
        for (String name : new String[]{"Cantarell ExtraBold",
                "Cantarell Extra Bold", "Cantarell"}) {
            if (installed.contains(name)) {
                return new Font(name, Font.BOLD, 12);
            }
        }
        return new Font("Segoe UI", Font.BOLD, 12);
    }

    // Thin solid border with a small corner radius.
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        public RoundBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }
    }

    // Creates a JPanel with a light background, rounded corners, and padding, suitable for containing other components.
  public static JPanel createCard() {
    JPanel panel = new JPanel();
    panel.setBackground(SURFACE);

    panel.setBorder(
        new CompoundBorder(
            new RoundBorder(BORDER, RADIUS),
            new EmptyBorder(18, 18, 18, 18)
        )
    );

    return panel;
}

    // Titled white box: heading on the left, optional component on the right.
    public static JPanel titledCard(String title, JComponent body,
                                    JComponent headerRight) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new CompoundBorder(
                new RoundBorder(BORDER, RADIUS),
                new EmptyBorder(12, 14, 10, 14)));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(heading(title), BorderLayout.WEST);
        if (headerRight != null) {
            header.add(headerRight, BorderLayout.EAST);
        }
        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // Creates a JLabel with the specified text, using the heading font and text color defined in the theme.
    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(TEXT);
        return label;
    }

    // Creates a JLabel with the specified text, using the normal font and text color defined in the theme.
    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(NORMAL_FONT);
        label.setForeground(TEXT);
        return label;
    }

    // Creates a JButton with the specified text, using the normal font and text color defined in the theme, and a background color based on whether it is a primary button or not.
   public static JButton createButton(String text) {
    JButton button = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), RADIUS * 2, RADIUS * 2);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    button.setFont(NORMAL_FONT);
    button.setForeground(Color.WHITE);
    button.setBackground(SUCCESS);
    button.setFocusPainted(false);
    button.setBorder(new EmptyBorder(11, 20, 11, 20));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setOpaque(false);
    button.setContentAreaFilled(false);

    return button;
}    
    // Creates a JButton with the specified text, using the normal font and text color defined in the theme, and a background color based on whether it is a primary button or not.
    public static JButton button(String text, boolean primary) {
        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 17, 11, 17));
        button.setOpaque(true);

        if (primary) {
            button.setBackground(ACCENT);
            button.setForeground(SURFACE);
        } else {
            button.setBackground(ACCENT_LIGHT);
            button.setForeground(ACCENT);
        }

        return button;
    }

    // Creates a JTextField with the specified initial value, using the normal font and text color defined in the theme, and a background color and border suitable for input fields.
    public static JTextField input(String initialValue) {
        JTextField field = new JTextField(initialValue);

        field.setFont(NORMAL_FONT);
        field.setForeground(TEXT);
        field.setBackground(SURFACE);
        field.setCaretColor(ACCENT);
        field.setBorder(new CompoundBorder(
                new RoundBorder(BORDER, RADIUS),
                new EmptyBorder(10, 12, 10, 12)
        ));

        return field;
    }

    public static JComboBox<String> combo(String[] options) {
        JComboBox<String> combo = new JComboBox<>(options);

        combo.setFont(NORMAL_FONT);
        combo.setBackground(SURFACE);
        combo.setForeground(TEXT);

        return combo;
    }

    public static JTextArea outputArea() {
        JTextArea area = new JTextArea(15, 35);

        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setForeground(TEXT);
        area.setBackground(new Color(250, 249, 246));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(12, 12, 12, 12));

        return area;
    }

    // Creates a JPanel with a light background, rounded corners, and padding, suitable for containing other components.
    public static JPanel modulePanel(
            String title,
            String description,
            JPanel controls,
            JTextArea output
    ) {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ON_BACKGROUND);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(SMALL_FONT);
        descriptionLabel.setForeground(ON_BACKGROUND_MUTED);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(7));
        header.add(descriptionLabel);

        JPanel controlsCard = createCard();
        controlsCard.setLayout(new BoxLayout(
                controlsCard, BoxLayout.Y_AXIS
        ));
        controlsCard.add(controls);

        JPanel outputCard = createCard();
        outputCard.setLayout(new BorderLayout(10, 10));

        JLabel outputHeading = heading("RESULTS & OUTPUT");
        outputCard.add(outputHeading, BorderLayout.NORTH);
        outputCard.add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setBackground(BACKGROUND);
        body.add(controlsCard, BorderLayout.WEST);
        body.add(outputCard, BorderLayout.CENTER);

        controlsCard.setPreferredSize(new Dimension(265, 0));

        main.add(header, BorderLayout.NORTH);
        main.add(body, BorderLayout.CENTER);

        return main;
    }

// Creates a JComboBox with the specified items, using the normal font and text color defined in the theme, and a background color suitable for dropdowns.
public static JComboBox<String> createComboBox(String[] items) {
    JComboBox<String> comboBox = new JComboBox<>(items);
    comboBox.setFont(NORMAL_FONT);
    comboBox.setBackground(SURFACE);
    comboBox.setForeground(TEXT);
    return comboBox;
}

// Creates a JTextField with the specified number of columns, using the normal font and text color defined in the theme, and a background color and border suitable for input fields.
public static JTextField createTextField(int columns) {
    JTextField field = new JTextField(columns);
    field.setFont(NORMAL_FONT);
    field.setForeground(TEXT);
    field.setBackground(SURFACE);
    field.setBorder(
        new javax.swing.border.CompoundBorder(
            new RoundBorder(BORDER, RADIUS),
            new javax.swing.border.EmptyBorder(9, 10, 9, 10)
        )
    );
    return field;
}
// Creates a JTextArea with the specified number of rows and columns, using the normal font and text color defined in the theme, and a background color and border suitable for output areas.
public static JTextArea createOutputArea() {
    JTextArea area = new JTextArea();
    area.setFont(new Font("Consolas", Font.PLAIN, 13));
    area.setForeground(TEXT);
    area.setBackground(new Color(250, 251, 251));
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBorder(
        new javax.swing.border.EmptyBorder(12, 12, 12, 12)
    );
    return area;
}
// Creates a JLabel with the specified text, using the normal font and text color defined in the theme.
public static JLabel createLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(NORMAL_FONT);
    label.setForeground(TEXT);
    return label;
}

}
