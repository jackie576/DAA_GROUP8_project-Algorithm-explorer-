// Displays benchmark results, chart summaries, and the analysis overview screen.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import analysis.BenchmarkResult;
import analysis.PerformanceAnalyzer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import sorting.*;
import visualization.Legend;
import visualization.LineChart;

// This class is a panel that shows benchmark results, chart summaries, and the analysis overview screen.
public class AnalysisPanel extends JPanel {

    private static final int MIN_SIZE = 10;
    private static final int MAX_SIZE = 20000;
    private static final Color[] COLORS = {
        Theme.BAR_COMPARING, new Color(150, 110, 200), Theme.BAR_SORTED,
        Theme.BAR_UNSORTED, new Color(214, 84, 84)
    };

    // sorts created as objects to access their names, descriptions, and complexity information for display in the UI.
    private final SortingAlgorithm[] info = {
        new BubbleSort(), new SelectionSort(), new InsertionSort(),
        new MergeSort(), new QuickSort()
    };

    // these are the UI components used in the analysis panel.
    private final JComboBox<String> algorithmBox = Theme.combo(new String[]{
        "Bubble Sort", "Selection Sort", "Insertion Sort",
        "Merge Sort", "Quick Sort", "Compare All"
    });

    // these are the UI components used in the analysis panel.
    private final JComboBox<String> arrangementBox = Theme.combo(new String[]{
        PerformanceAnalyzer.RANDOM, PerformanceAnalyzer.SORTED,
        PerformanceAnalyzer.REVERSE
    });

    // these are the UI components used in the analysis panel.
    private final JTextField sizeField = Theme.input("2000");
    private final JButton runButton = Theme.createButton("Run Analysis");
    private final JLabel quickInfo = new JLabel();
    private final LineChart timeChart = new LineChart();
    private final LineChart comparisonChart = new LineChart();
    private final JPanel legendStrip = new JPanel(new BorderLayout());
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Algorithm", "Input size", "Comparisons",
                "Swaps / Writes", "Time (ms)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final GaugePanel progressGauge = new GaugePanel("Progress", Theme.ACCENT);
    private final GaugePanel comparisonsGauge = new GaugePanel("Comparisons", Theme.BAR_UNSORTED);
    private final GaugePanel movesGauge = new GaugePanel("Swaps", Theme.BAR_COMPARING);
    private final GaugePanel timeGauge = new GaugePanel("Time", Theme.GAUGE_TIME);
    private volatile int runId;
    private double maxComparisons = 1;
    private double maxMoves = 1;
    private double maxMillis = 1;

    // constructor that initializes the analysis panel and its components.
    public AnalysisPanel() {

        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buildLeftColumn(), BorderLayout.WEST);
        add(buildRightColumn(), BorderLayout.CENTER);

        algorithmBox.addActionListener(e -> updateInfo());
        updateInfo();
        resetDisplay();
    }

    // builds the left column of the analysis panel.
    private JPanel buildLeftColumn() {

        JPanel controls = Theme.createCard();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        controls.add(section("Algorithm"));
        left(algorithmBox, 38);
        controls.add(algorithmBox);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Input Arrangement"));
        left(arrangementBox, 38);
        controls.add(arrangementBox);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Largest Input Size"));
        left(sizeField, 42);
        controls.add(sizeField);

        controls.add(Box.createVerticalStrut(14));
        left(runButton, 42);
        controls.add(runButton);
        controls.add(Box.createVerticalStrut(8));
        JButton clear = Theme.button("Clear Results", false);
        left(clear, 40);
        controls.add(clear);

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

        runButton.addActionListener(e -> run());
        sizeField.addActionListener(e -> run());
        clear.addActionListener(e -> resetDisplay());
        return column;
    }

    // builds the right column of the analysis panel.
    private JPanel buildRightColumn() {

        JPanel gauges = new JPanel(new GridLayout(1, 4, 12, 0));
        gauges.setOpaque(false);
        gauges.add(progressGauge);
        gauges.add(comparisonsGauge);
        gauges.add(movesGauge);
        gauges.add(timeGauge);

        timeChart.setAxisTitles("Input size", "ms");
        comparisonChart.setAxisTitles("Input size", "comparisons");
        legendStrip.setOpaque(false);

        JPanel timeCard = Theme.titledCard("Running Time", timeChart, null);
        JPanel comparisonCard = Theme.titledCard("Comparisons", comparisonChart, null);
        JPanel charts = new JPanel(new GridLayout(1, 2, 12, 0));
        charts.setOpaque(false);
        charts.add(timeCard);
        charts.add(comparisonCard);

        table.setRowHeight(24);
        table.setFont(Theme.SMALL_FONT);
        table.setForeground(Theme.TEXT);
        table.setBackground(Theme.SURFACE);
        table.setGridColor(Theme.BORDER);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Theme.ACCENT_LIGHT);
        table.setSelectionForeground(Theme.TEXT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(Theme.ACCENT_LIGHT);
        table.getTableHeader().setForeground(Theme.TEXT);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.SURFACE);
        JPanel tableCard = Theme.titledCard("Results", scroll, null);

        JPanel column = new JPanel(new GridBagLayout());
        column.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;

        gauges.setPreferredSize(new Dimension(100, 150));
        gauges.setMinimumSize(new Dimension(100, 150));
        charts.setMinimumSize(new Dimension(100, 160));
        tableCard.setMinimumSize(new Dimension(100, 120));

        c.gridy = 0; c.weighty = 0; c.insets = new Insets(0, 0, 12, 0);
        column.add(gauges, c);
        legendStrip.setPreferredSize(new Dimension(100, 20));
        legendStrip.setMinimumSize(new Dimension(100, 20));
        c.gridy = 1; c.weighty = 0; c.insets = new Insets(0, 0, 6, 0);
        column.add(legendStrip, c);
        c.gridy = 2; c.weighty = 3; c.insets = new Insets(0, 0, 12, 0);
        column.add(charts, c);
        c.gridy = 3; c.weighty = 2; c.insets = new Insets(0, 0, 0, 0);
        column.add(tableCard, c);
        return column;
    }

    // creates a section label with the given text for the analysis panel.
    private JLabel section(String text) {
        JLabel label = Theme.heading(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    // sets the alignment and maximum size of a component in the analysis panel.
    private void left(JComponent c, int height) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    // updates the quick info section of the analysis panel.
    private void updateInfo() {
        int i = algorithmBox.getSelectedIndex();
        String html;
        if (i < info.length) {
            html = "<b>" + info[i].name() + "</b><br>" + info[i].description()
                    + "<br><br><span style='color:#7c7469'>" + info[i].complexity()
                    + "</span>";
        } else {
            StringBuilder sb = new StringBuilder("<b>Compare All</b><br>"
                    + "Runs all five sorts on the same data at eight input sizes."
                    + "<br><br><span style='color:#7c7469'>");
            for (SortingAlgorithm a : info) {
                sb.append(a.name()).append(": ")
                        .append(a.complexity().split("\\|")[1].trim()).append("<br>");
            }
            html = sb.append("</span>").toString();
        }
        quickInfo.setText("<html><div style='width:190px'>" + html + "</div></html>");
        quickInfo.setFont(Theme.SMALL_FONT);
        quickInfo.setForeground(Theme.TEXT);
    }

    // resets the display of the analysis panel.
    private void resetDisplay() {
        runId++;
        runButton.setEnabled(true);
        tableModel.setRowCount(0);
        timeChart.setSeries();
        comparisonChart.setSeries();
        setLegends(new ArrayList<>());
        progressGauge.setValue(0, "0%");
        comparisonsGauge.setValue(0, "0");
        movesGauge.setValue(0, "0");
        timeGauge.setValue(0, "0ms");
        maxComparisons = 1;
        maxMoves = 1;
        maxMillis = 1;
    }

    // sets the legend strip of the analysis panel.
    private void setLegends(List<Integer> algorithms) {
        String[] names = new String[algorithms.size()];
        Color[] colors = new Color[algorithms.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = PerformanceAnalyzer.ALGORITHMS[algorithms.get(i)]
                    .replace(" Sort", "");
            colors[i] = COLORS[algorithms.get(i)];
        }
        legendStrip.removeAll();
        legendStrip.add(new Legend(names, colors), BorderLayout.CENTER);
        legendStrip.revalidate();
        legendStrip.repaint();
    }

    // runs the analysis based on the selected algorithm, input arrangement, and size.
    private void run() {
        if (!runButton.isEnabled()) {
            return;
        }
        int max;
        try {
            max = Integer.parseInt(sizeField.getText().trim());
            if (max < MIN_SIZE || max > MAX_SIZE) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Enter a whole number between " + MIN_SIZE + " and " + MAX_SIZE + ".",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> chosen = new ArrayList<>();
        int pick = algorithmBox.getSelectedIndex();
        if (pick < info.length) {
            chosen.add(pick);
        } else {
            for (int i = 0; i < info.length; i++) {
                chosen.add(i);
            }
        }

        resetDisplay();
        final int id = runId;
        runButton.setEnabled(false);

        Color[] colors = new Color[chosen.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = COLORS[chosen.get(i)];
        }
        timeChart.setSeries(colors);
        comparisonChart.setSeries(colors);
        setLegends(chosen);

        final String arrangement = (String) arrangementBox.getSelectedItem();
        final int[] sizes = PerformanceAnalyzer.sizes(max);
        final int total = sizes.length * chosen.size();

        Thread worker = new Thread(() -> {
            PerformanceAnalyzer.warmUp();
            int done = 0;
            for (int size : sizes) {
                int[] data = PerformanceAnalyzer.generate(arrangement, size, 42);
                for (int s = 0; s < chosen.size(); s++) {
                    if (id != runId) {
                        return;
                    }
                    int algo = chosen.get(s);
                    BenchmarkResult result = PerformanceAnalyzer.benchmark(
                            PerformanceAnalyzer.ALGORITHMS[algo], data);
                    final double progress = ++done / (double) total;
                    final int series = s;
                    final int algoIndex = algo;
                    SwingUtilities.invokeLater(() -> {
                        if (id == runId) {
                            publish(result, series, algoIndex, progress);
                        }
                    });
                }
            }
            SwingUtilities.invokeLater(() -> {
                if (id == runId) {
                    runButton.setEnabled(true);
                }
            });
        }, "benchmark");
        worker.setDaemon(true);
        worker.start();
    }

    // publishes the benchmark result to the UI components of the analysis panel.
    private void publish(BenchmarkResult r, int series, int algo, double progress) {
        timeChart.addPoint(series, r.size, r.millis);
        comparisonChart.addPoint(series, r.size, r.comparisons);

        tableModel.addRow(new Object[]{
            r.algorithm, String.format("%,d", r.size),
            String.format("%,d", r.comparisons), String.format("%,d", r.moves),
            String.format("%.3f", r.millis)});
        table.scrollRectToVisible(table.getCellRect(tableModel.getRowCount() - 1, 0, true));

        maxComparisons = Math.max(maxComparisons, r.comparisons);
        maxMoves = Math.max(maxMoves, r.moves);
        maxMillis = Math.max(maxMillis, r.millis);

        progressGauge.setValue(progress, Math.round(progress * 100) + "%");
        comparisonsGauge.setValue(r.comparisons / maxComparisons, compact(r.comparisons));
        movesGauge.setLabel(info[algo].moveLabel());
        movesGauge.setValue(r.moves / maxMoves, compact(r.moves));
        timeGauge.setValue(r.millis / maxMillis, String.format("%.1fms", r.millis));
    }

    // returns a compact string representation of a number.
    private static String compact(long v) {
        if (v >= 1_000_000) return String.format("%.1fM", v / 1_000_000.0);
        if (v >= 10_000) return String.format("%.0fk", v / 1000.0);
        return String.valueOf(v);
    }
}
