// Shows sorting demos and controls for stepping through the current algorithm.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import sorting.*;
import visualization.AnimationController;
import visualization.PerformanceChart;
import visualization.SortingVisualizer;

//

// Sorting workspace panel that lets the user choose an algorithm and inspect each step.
public class SortingPanel extends JPanel implements AnimationController.Listener {

    private static final int MAX_ELEMENTS = 30;

    private final SortingAlgorithm[] algorithms = {
        new BubbleSort(), new SelectionSort(), new InsertionSort(),
        new MergeSort(), new QuickSort()
    };

    private final JComboBox<String> algorithmBox;
    private final JRadioButton ascending = new JRadioButton("Ascending", true);
    private final JRadioButton descending = new JRadioButton("Descending");
    private final JTextField input = Theme.input("64, 34, 25, 12, 22, 11, 90, 45");
    private final JLabel quickInfo = new JLabel();

    private final SortingVisualizer visualizer = new SortingVisualizer();
    private final PerformanceChart chart = new PerformanceChart();

    private final GaugePanel progressGauge = new GaugePanel("Progress", Theme.ACCENT);
    private final GaugePanel comparisonsGauge = new GaugePanel("Comparisons", Theme.BAR_UNSORTED);
    private final GaugePanel movesGauge = new GaugePanel("Swaps", Theme.BAR_COMPARING);
    private final GaugePanel timeGauge = new GaugePanel("Time", Theme.GAUGE_TIME);

    private final AnimationController controller;
    private final PlaybackControls playback;

    private List<SortStep> steps = new ArrayList<>();
    private int totalComparisons;
    private int totalMoves;

    // class constructor that initializes the sorting panel with controls and visualizations.
    public SortingPanel() {

        controller = new AnimationController(this);
        playback = new PlaybackControls(
                this::togglePause, this::stepOnce, this::reset,
                controller::setSpeed);

        String[] names = new String[algorithms.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = algorithms[i].name();
        }
        algorithmBox = Theme.combo(names);

        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildLeftColumn(), BorderLayout.WEST);
        add(buildRightColumn(), BorderLayout.CENTER);

        algorithmBox.addActionListener(e -> reset());
        ascending.addActionListener(e -> reset());
        descending.addActionListener(e -> reset());

        reset();
    }

    // ---------------------------------------------------------- layout

    private JPanel buildLeftColumn() {

        JPanel controls = Theme.createCard();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        controls.add(section("Algorithm"));
        left(algorithmBox, 38);
        controls.add(algorithmBox);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Order"));
        ButtonGroup group = new ButtonGroup();
        group.add(ascending);
        group.add(descending);
        JPanel order = new JPanel(new GridLayout(1, 2));
        order.setOpaque(false);
        for (JRadioButton b : new JRadioButton[]{ascending, descending}) {
            b.setOpaque(false);
            b.setFont(Theme.NORMAL_FONT);
            b.setForeground(Theme.TEXT);
            b.setFocusPainted(false);
            order.add(b);
        }
        left(order, 30);
        controls.add(order);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Input Array"));
        left(input, 42);
        controls.add(input);

        controls.add(Box.createVerticalStrut(12));
        JButton shuffle = Theme.button("Shuffle", false);
        JButton start = Theme.createButton("Start");
        JPanel startRow = new JPanel(new BorderLayout(6, 0));
        startRow.setOpaque(false);
        startRow.add(start, BorderLayout.CENTER);
        startRow.add(shuffle, BorderLayout.EAST);
        left(startRow, 42);
        controls.add(startRow);

        controls.add(Box.createVerticalStrut(16));
        controls.add(section("Controls"));
        left(playback, 80);
        controls.add(playback);

        JPanel info = Theme.createCard();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(section("Quick Info"));
        quickInfo.setVerticalAlignment(SwingConstants.TOP);
        quickInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(quickInfo);

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setPreferredSize(new Dimension(285, 0));
        controls.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        column.add(controls);
        column.add(Box.createVerticalStrut(12));
        column.add(info);
        column.add(Box.createVerticalGlue());

        start.addActionListener(e -> start());
        shuffle.addActionListener(e -> shuffle());
        input.addActionListener(e -> start());

        return column;
    }

    // method that builds the right column of the sorting panel, which includes gauges, array visualization, and performance graph.
    private JPanel buildRightColumn() {

        JPanel gauges = new JPanel(new GridLayout(1, 4, 12, 0));
        gauges.setOpaque(false);
        gauges.add(progressGauge);
        gauges.add(comparisonsGauge);
        gauges.add(movesGauge);
        gauges.add(timeGauge);

        JPanel arrayCard = chartCard("Array Visualization", visualizer,
                new String[]{"Sorted", "Comparing", "Unsorted"},
                new Color[]{Theme.BAR_SORTED, Theme.BAR_COMPARING, Theme.BAR_UNSORTED});

        JPanel chartCard = chartCard("Performance Graph", chart,
                new String[]{"Comparisons", "Swaps"},
                new Color[]{PerformanceChart.COMPARISONS, PerformanceChart.MOVES});

        JPanel column = new JPanel(new GridBagLayout());
        column.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        c.gridy = 0; c.weighty = 0; c.insets = new Insets(0, 0, 12, 0);
        gauges.setPreferredSize(new Dimension(100, 150));
        gauges.setMinimumSize(new Dimension(100, 150));
        arrayCard.setMinimumSize(new Dimension(100, 150));
        chartCard.setMinimumSize(new Dimension(100, 120));
        column.add(gauges, c);

        c.gridy = 1; c.weighty = 3;
        column.add(arrayCard, c);

        c.gridy = 2; c.weighty = 2; c.insets = new Insets(0, 0, 0, 0);
        column.add(chartCard, c);

        return column;
    }

    // method that builds a chart card, which includes a title, a chart, and a legend.
    private JPanel chartCard(String title, JComponent body,
                             String[] names, Color[] colors) {
        JPanel card = Theme.createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundBorder(Theme.BORDER, Theme.RADIUS),
                BorderFactory.createEmptyBorder(12, 14, 10, 14)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.heading(title), BorderLayout.WEST);
        header.add(new Legend(names, colors), BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // method that creates a section label with the specified text, styled as a heading.
    private JLabel section(String text) {
        JLabel label = Theme.heading(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    private void left(JComponent c, int height) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    // ---------------------------------------------------------- actions

    private SortingAlgorithm selected() {
        return algorithms[algorithmBox.getSelectedIndex()];
    }

    // Parses the input text field into an array of integers, validating the input and throwing exceptions for invalid cases.
    private int[] parseInput() {
        String text = input.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Please enter numbers to sort.");
        }
        String[] parts = text.split("[,\\s]+");
        if (parts.length < 2 || parts.length > MAX_ELEMENTS) {
            throw new IllegalArgumentException(
                    "Enter between 2 and " + MAX_ELEMENTS + " numbers.");
        }
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                values[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "\"" + parts[i] + "\" is not a whole number.");
            }
            if (values[i] < 1 || values[i] > 999) {
                throw new IllegalArgumentException(
                        "Values must be between 1 and 999.");
            }
        }
        return values;
    }

    // Computes all steps for the current input and loads them.
    private void load() {
        int[] data = parseInput();
        steps = selected().sort(data, ascending.isSelected());

        SortStep last = steps.get(steps.size() - 1);
        totalComparisons = last.comparisons;
        totalMoves = last.moves;

        controller.load(steps.size());
        chart.clear();
        chart.setScale(steps.size(), Math.max(totalComparisons, totalMoves));
        visualizer.showArray(data);
        updateGauges(-1);
    }

    // Starts the sorting animation by loading the steps and beginning playback, handling any input errors with a dialog.
    private void start() {
        try {
            load();
            controller.play();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // method that toggles the pause/play state of the animation controller, pausing if it's currently playing and playing if it's paused.
    private void togglePause() {
        if (!controller.isLoaded() || controller.isFinished()) {
            return;
        }
        if (controller.isPlaying()) {
            controller.pause();
        } else {
            controller.play();
        }
    }

    // Steps through the next animation frame, loading the steps if they haven't been loaded yet, and handling any input errors with a dialog.
    private void stepOnce() {
        try {
            if (!controller.isLoaded()) {
                load();
            }
            controller.stepOnce();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // method that resets the state of the sorting panel, clearing the steps, resetting the controller, clearing the chart, resetting gauges, and updating the quick info section.
    private void reset() {
        steps = new ArrayList<>();
        controller.load(0);
        chart.clear();
        chart.setScale(1, 1);
        movesGauge.setLabel(selected().moveLabel());
        quickInfo.setText("<html><div style='width:190px'><b>"
                + selected().name() + "</b><br>" + selected().description()
                + "<br><br><span style='color:#7c7469'>"
                + selected().complexity() + "</span></div></html>");
        quickInfo.setFont(Theme.SMALL_FONT);
        quickInfo.setForeground(Theme.TEXT);
        try {
            visualizer.showArray(parseInput());
        } catch (IllegalArgumentException ex) {
            visualizer.showArray(new int[0]);
        }
        updateGauges(-1);
    }

    // method that shuffles the input text field and resets the state of the sorting panel.
    private void shuffle() {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(1 + random.nextInt(99));
        }
        input.setText(text.toString());
        reset();
    }

    // method that updates the gauges based on the current step index, showing progress, comparisons, swaps, and time.
    private void updateGauges(int index) {
        SortStep s = index >= 0 ? steps.get(index) : null;
        int comps = s == null ? 0 : s.comparisons;
        int moves = s == null ? 0 : s.moves;
        double progress = steps.isEmpty() ? 0 : (index + 1.0) / steps.size();

        progressGauge.setValue(progress, Math.round(progress * 100) + "%");
        comparisonsGauge.setValue(
                totalComparisons == 0 ? 0 : (double) comps / totalComparisons,
                String.valueOf(comps));
        movesGauge.setValue(
                totalMoves == 0 ? 0 : (double) moves / totalMoves,
                String.valueOf(moves));
        updateTime();
    }

    // method that updates the time gauge based on the elapsed time from the animation controller.
    private void updateTime() {
        double seconds = controller.getElapsedSeconds();
        timeGauge.setValue((seconds % 60) / 60, String.format("%.2fs", seconds));
    }

    // ------------------------------------------- AnimationController.Listener

    @Override
    public void onStep(int index) {
        SortStep s = steps.get(index);
        visualizer.showStep(s);
        chart.addPoint(index + 1, s.comparisons, s.moves);
        updateGauges(index);
    }

    @Override
    public void onTick() {
        visualizer.setBlip(controller.blip());
        updateTime();
    }

    @Override
    public void onStateChanged() {
        playback.setPlaying(!controller.isPaused());
    }

    // ------------------------------------------------------------ legend

    // Legend component that displays the names and colors of the different states in the sorting visualization.
    private static class Legend extends JComponent {
        private final String[] names;
        private final Color[] colors;

        Legend(String[] names, Color[] colors) {
            this.names = names;
            this.colors = colors;
            setPreferredSize(new Dimension(names.length * 100, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(Theme.SMALL_FONT);
            FontMetrics fm = g2.getFontMetrics();

            int total = 0;
            for (String n : names) {
                total += 18 + fm.stringWidth(n) + 14;
            }
            int x = getWidth() - total;
            int mid = getHeight() / 2;
            for (int i = 0; i < names.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRect(x, mid - 5, 10, 10);
                g2.setColor(Theme.TEXT);
                g2.drawString(names[i], x + 16, mid + 4);
                x += 18 + fm.stringWidth(names[i]) + 14;
            }
            g2.dispose();
        }
    }
}
