// Runs recursive examples and visualizes the call stack and growth trend.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import recursion.RecursionAlgorithms;
import recursion.RecursionTrace;
import visualization.AnimationController;
import visualization.CallStackVisualizer;
import visualization.Legend;
import visualization.LineChart;

// This class provides a panel for running and visualizing recursive algorithms, including controls for input, playback, and displaying results.
public class RecursionRunPanel extends JPanel
        implements AnimationController.Listener {

    private static final String[] FIB = {
        "Fibonacci - Naive", "Fibonacci - Memoized", "Fibonacci - Bottom-up"
    };
    private static final String[] FACT = {
        "Factorial - Recursive", "Factorial - Iterative"
    };

    private final JComboBox<String> algorithmBox;
    private final JTextField input = Theme.input("6");
    private final JLabel quickInfo = new JLabel();
    private final JLabel resultBar = new JLabel();

    private final CallStackVisualizer stack = new CallStackVisualizer();
    private final LineChart growth = new LineChart();
    private final JPanel legendHolder = new JPanel(new BorderLayout());

    private final GaugePanel progressGauge = new GaugePanel("Progress", Theme.ACCENT);
    private final GaugePanel opsGauge = new GaugePanel("Operations", Theme.BAR_UNSORTED);
    private final GaugePanel depthGauge = new GaugePanel("Depth", Theme.BAR_COMPARING);
    private final GaugePanel timeGauge = new GaugePanel("Time", Theme.GAUGE_TIME);

    private final AnimationController controller;
    private final PlaybackControls playback;

    private RecursionTrace.Trace trace;
    private boolean syncing;

    // class constructor that initializes the recursion run panel with a list of algorithms and a callback for when an algorithm is chosen.
    public RecursionRunPanel(String[] algorithms, Consumer<String> onAlgorithmChosen) {

        controller = new AnimationController(this);
        playback = new PlaybackControls(
                this::togglePause, this::step, this::reset,
                controller::setSpeed);

        algorithmBox = Theme.combo(algorithms);

        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buildLeftColumn(), BorderLayout.WEST);
        add(buildRightColumn(), BorderLayout.CENTER);

        algorithmBox.addActionListener(e -> {
            if (syncing) {
                return;
            }
            String name = (String) algorithmBox.getSelectedItem();
            if ("Tower of Hanoi".equals(name)) {
                onAlgorithmChosen.accept(name);
            } else {
                reset();
            }
        });

        reset();
    }

    // Called by the Recursion page when another page changed the choice.
    public void select(String name) {
        syncing = true;
        algorithmBox.setSelectedItem(name);
        syncing = false;
        reset();
    }

    // ---------------------------------------------------------- layout

    // Builds the left column of the panel, which contains controls for selecting algorithms, inputting values, and controlling playback.
    private JPanel buildLeftColumn() {

        JPanel controls = Theme.createCard();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        controls.add(section("Algorithm"));
        left(algorithmBox, 38);
        controls.add(algorithmBox);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Input (n)"));
        left(input, 42);
        controls.add(input);

        controls.add(Box.createVerticalStrut(12));
        JButton run = Theme.createButton("Run");
        left(run, 42);
        controls.add(run);

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

        run.addActionListener(e -> run(true));
        input.addActionListener(e -> run(true));
        return column;
    }

    // Builds the right column of the panel, which contains visualizations and results.
    private JPanel buildRightColumn() {

        JPanel gauges = new JPanel(new GridLayout(1, 4, 12, 0));
        gauges.setOpaque(false);
        gauges.add(progressGauge);
        gauges.add(opsGauge);
        gauges.add(depthGauge);
        gauges.add(timeGauge);

        JPanel stackCard = Theme.titledCard("Call Stack", stack,
                new Legend(new String[]{"Active", "Waiting", "Cached"},
                        new Color[]{Theme.BAR_COMPARING, Theme.BAR_UNSORTED,
                            Theme.BAR_SORTED}));

        growth.setAxisTitles("Input n", "Operations");
        JPanel growthCard = Theme.titledCard("Operations Growth", growth, legendHolder);
        legendHolder.setOpaque(false);

        JPanel resultCard = Theme.createCard();
        resultCard.setLayout(new BorderLayout());
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundBorder(Theme.BORDER, Theme.RADIUS),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        resultBar.setFont(Theme.NORMAL_FONT);
        resultBar.setForeground(Theme.TEXT);
        resultCard.add(resultBar, BorderLayout.CENTER);

        JPanel column = new JPanel(new GridBagLayout());
        column.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;

        gauges.setPreferredSize(new Dimension(100, 150));
        gauges.setMinimumSize(new Dimension(100, 150));
        stackCard.setMinimumSize(new Dimension(100, 150));
        growthCard.setMinimumSize(new Dimension(100, 120));
        resultCard.setPreferredSize(new Dimension(100, 50));
        resultCard.setMinimumSize(new Dimension(100, 50));

        c.gridy = 0; c.weighty = 0; c.insets = new Insets(0, 0, 12, 0);
        column.add(gauges, c);
        c.gridy = 1; c.weighty = 3;
        column.add(stackCard, c);
        c.gridy = 2; c.weighty = 2;
        column.add(growthCard, c);
        c.gridy = 3; c.weighty = 0; c.insets = new Insets(0, 0, 0, 0);
        column.add(resultCard, c);
        return column;
    }

    // method that creates a section label with the specified text.
    private JLabel section(String text) {
        JLabel label = Theme.heading(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    // method that sets the alignment and maximum size of a component to align it to the left with a specified height.
    private void left(JComponent c, int height) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    // method that sets the alignment and maximum size of a component to align it to the right with a specified height.
    private String selected() {
        return (String) algorithmBox.getSelectedItem();
    }

    // method that checks if the currently selected algorithm is a Fibonacci variant.
    private boolean isFibonacci() {
        return selected().startsWith("Fibonacci");
    }

    // method that resets the state of the recursion run panel, clearing the trace, stack, gauges, and result display.
    private void reset() {
        trace = null;
        controller.load(0);
        stack.clear("Press Run to watch the calls pile up.");
        zeroGauges();
        resultBar.setText("<html><span style='color:#7c7469'>"
                + "Press Run to see the result.</span></html>");
        updateInfo();
        buildGrowth(0);
        growth.clear();
    }

    // method that resets the gauges to their initial state, showing zero progress, operations, depth, and time.
    private void zeroGauges() {
        progressGauge.setValue(0, "0%");
        opsGauge.setValue(0, "0");
        depthGauge.setValue(0, "0");
        timeGauge.setValue(0, "0.00s");
    }

    // Updates the quick info section with details about the selected algorithm, including time and space complexity.
    private void updateInfo() {
        String name = selected();
        String text;
        switch (name) {
            case "Fibonacci - Naive":
                text = "Calls itself twice for every n, so the same values are "
                        + "recomputed again and again.<br><br>Time O(2<sup>n</sup>)"
                        + "  |  Space O(n)";
                break;
            case "Fibonacci - Memoized":
                text = "Remembers answers it already worked out (cached), so each "
                        + "value is computed once.<br><br>Time O(n)  |  Space O(n)";
                break;
            case "Fibonacci - Bottom-up":
                text = "Starts from F(0) and F(1) and builds upward in a loop. "
                        + "No recursion.<br><br>Time O(n)  |  Space O(n)";
                break;
            case "Factorial - Recursive":
                text = "n! = n x (n-1)!. Every call waits for the smaller one "
                        + "to return.<br><br>Time O(n)  |  Space O(n) stack";
                break;
            default:
                text = "Multiplies 1 x 2 x ... x n in a loop.<br><br>"
                        + "Time O(n)  |  Space O(1)";
        }
        quickInfo.setText("<html><div style='width:190px'><b>" + name + "</b><br>"
                + text + "</div></html>");
        quickInfo.setFont(Theme.SMALL_FONT);
        quickInfo.setForeground(Theme.TEXT);
    }

    // Tries to parse an integer from the input field.
    private Integer parseN() {
        try {
            return Integer.parseInt(input.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Evaluates the chosen recursion problem, builds the growth chart, and starts the animation if enabled.
    private void run(boolean play) {
        Integer parsed = parseN();
        if (parsed == null) {
            return;
        }
        int n = parsed;
        String name = selected();

        RecursionAlgorithms.Result result;
        double millis;
        try {
            long t0 = System.nanoTime();
            result = RecursionAlgorithms.run(name, n);
            millis = (System.nanoTime() - t0) / 1_000_000.0;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String shown = result.value.length() > 28
                ? result.value.substring(0, 20) + "... (" + result.value.length()
                    + " digits)"
                : result.value;
        resultBar.setText(String.format(
                "<html><b>Result</b>&nbsp; %s &nbsp;&nbsp;&nbsp;&nbsp; "
                + "<b>Operations</b>&nbsp; %,d &nbsp;&nbsp;&nbsp;&nbsp; "
                + "<b>Time</b>&nbsp; %.3f ms</html>", shown, result.operations, millis));

        buildGrowth(n);

        if (RecursionTrace.canAnimate(name, n)) {
            trace = RecursionTrace.trace(name, n);
            stack.clear("");
            zeroGauges();
            controller.load(trace.events.size());
            controller.setBaseInterval(
                    Math.max(60, Math.min(400, 12000.0 / trace.events.size())));
            if (play) {
                controller.play();
            }
        } else {
            trace = null;
            controller.load(0);
            int limit = name.equals("Fibonacci - Naive")
                    ? RecursionTrace.NAIVE_LIMIT : RecursionTrace.OTHER_LIMIT;
            stack.clear("n = " + n + " is too large to animate (limit " + limit
                    + "). Result and graph are shown.");
            int depth = name.contains("Bottom-up") || name.contains("Iterative")
                    ? 1 : n + 1;
            progressGauge.setValue(1, "100%");
            opsGauge.setValue(1, compact(result.operations));
            depthGauge.setValue(1, String.valueOf(depth));
            timeGauge.setValue(Math.min(1, millis / 1000), String.format("%.1fms", millis));
        }
    }

    // Converts a long value to a compact string representation, using "k" for thousands and "M" for millions.
    private static String compact(long v) {
        if (v >= 1_000_000) return String.format("%.1fM", v / 1_000_000.0);
        if (v >= 10_000) return String.format("%.0fk", v / 1000.0);
        return String.valueOf(v);
    }

    // Operation counts for every n up to the input, for each variant.
    private void buildGrowth(int n) {
        String[] family = isFibonacci() ? FIB : FACT;
        Color[] colors = isFibonacci()
                ? new Color[]{Theme.BAR_COMPARING, Theme.BAR_UNSORTED, Theme.BAR_SORTED}
                : new Color[]{Theme.BAR_COMPARING, Theme.BAR_UNSORTED};
        String[] legend = isFibonacci()
                ? new String[]{"Naive", "Memoized", "Bottom-up"}
                : new String[]{"Recursive", "Iterative"};

        legendHolder.removeAll();
        legendHolder.add(new Legend(legend, colors), BorderLayout.CENTER);
        legendHolder.revalidate();
        legendHolder.repaint();

        growth.setSeries(colors);
        growth.setLogY(isFibonacci());
        growth.setAxisTitles("Input n", isFibonacci() ? "Operations (log scale)" : "Operations");

        int xMax = Math.min(n, isFibonacci() ? 30 : 50);
        for (int k = 0; k <= xMax; k++) {
            for (int s = 0; s < family.length; s++) {
                growth.addPoint(s, k, RecursionAlgorithms.run(family[s], k).operations);
            }
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

    private void step() {
        if (!controller.isLoaded()) {
            run(false);
        }
        if (controller.isLoaded()) {
            controller.stepOnce();
        }
    }

    // ------------------------------------------- AnimationController.Listener

    @Override
    public void onStep(int index) {
        RecursionTrace.Event e = trace.events.get(index);
        stack.apply(e);
        progressGauge.setValue((index + 1.0) / trace.events.size(),
                Math.round((index + 1.0) / trace.events.size() * 100) + "%");
        opsGauge.setValue(trace.operations == 0 ? 0 : (double) e.ops / trace.operations,
                String.valueOf(e.ops));
        depthGauge.setValue(trace.maxDepth == 0 ? 0 : (double) e.depth / trace.maxDepth,
                String.valueOf(e.depth));
        updateTime();
    }

    @Override
    public void onTick() {
        stack.setBlip(controller.blip());
        updateTime();
    }

    @Override
    public void onStateChanged() {
        playback.setPlaying(!controller.isPaused());
    }

    private void updateTime() {
        double seconds = controller.getElapsedSeconds();
        timeGauge.setValue((seconds % 60) / 60, String.format("%.2fs", seconds));
    }
}
