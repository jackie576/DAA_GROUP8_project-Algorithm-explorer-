package frontend;

import datastructures.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import visualization.DataStructureVisualizer;
import visualization.DataStructureVisualizer.Kind;
import visualization.Legend;
import visualization.LineChart;

/** Data structures page: pile / row / ring / node drawings, gauges and graph. */
public class DataStructuresPanel extends JPanel {

    private CustomStack stack = new CustomStack();
    private CustomQueue queue = new CustomQueue();
    private CircularQueue circularQueue = new CircularQueue();
    private SinglyLinkedList singlyList = new SinglyLinkedList();
    private DoublyLinkedList doublyList = new DoublyLinkedList();
    private final BalancedBrackets brackets = new BalancedBrackets();

    private final JComboBox<String> structure = Theme.combo(new String[]{
        "Stack", "Linear Queue", "Circular Queue",
        "Singly Linked List", "Doubly Linked List", "Balanced Brackets"
    });
    private final JComboBox<String> operation = Theme.combo(new String[]{"Push"});
    private final JTextField input = Theme.input("");
    private final JLabel quickInfo = new JLabel();
    private final JTextArea steps = Theme.outputArea();

    private final DataStructureVisualizer visualizer = new DataStructureVisualizer();
    private final LineChart activity = new LineChart();

    private final GaugePanel sizeGauge = new GaugePanel("Size", Theme.ACCENT);
    private final GaugePanel opsGauge = new GaugePanel("Operations", Theme.BAR_UNSORTED);
    private final GaugePanel stepsGauge = new GaugePanel("Steps", Theme.BAR_COMPARING);
    private final GaugePanel timeGauge = new GaugePanel("Time", Theme.GAUGE_TIME);

    private int operationCount;
    private int maxSteps = 1;
    private double maxMicros = 1;
    private Timer bracketTimer;

    public DataStructuresPanel() {

        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buildLeftColumn(), BorderLayout.WEST);
        add(buildRightColumn(), BorderLayout.CENTER);

        structure.addActionListener(e -> {
            updateOperations();
            clearHistory();
            showCurrent("");
        });
        updateOperations();
        clearHistory();
        showCurrent("");
    }

    // ---------------------------------------------------------- layout

    private JPanel buildLeftColumn() {

        JPanel controls = Theme.createCard();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        controls.add(section("Structure"));
        left(structure, 38);
        controls.add(structure);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Operation"));
        left(operation, 38);
        controls.add(operation);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Value"));
        left(input, 42);
        controls.add(input);

        controls.add(Box.createVerticalStrut(14));
        JButton execute = Theme.createButton("Execute");
        left(execute, 42);
        controls.add(execute);
        controls.add(Box.createVerticalStrut(8));
        JButton reset = Theme.button("Reset Structure", false);
        left(reset, 40);
        controls.add(reset);

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

        execute.addActionListener(e -> execute());
        input.addActionListener(e -> execute());
        reset.addActionListener(e -> resetStructures());
        return column;
    }

    private JPanel buildRightColumn() {

        JPanel gauges = new JPanel(new GridLayout(1, 4, 12, 0));
        gauges.setOpaque(false);
        gauges.add(sizeGauge);
        gauges.add(opsGauge);
        gauges.add(stepsGauge);
        gauges.add(timeGauge);

        JPanel visualCard = Theme.titledCard("Structure View", visualizer, null);

        steps.setRows(5);
        JScrollPane scroll = new JScrollPane(steps);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        JPanel stepsCard = Theme.titledCard("Operation Steps", scroll, null);

        activity.setSeries(Theme.BAR_UNSORTED, Theme.BAR_COMPARING);
        activity.setAxisTitles("Operation number", "");
        JPanel activityCard = Theme.titledCard("Activity Graph", activity,
                new Legend(new String[]{"Size", "Steps"},
                        new Color[]{Theme.BAR_UNSORTED, Theme.BAR_COMPARING}));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 12, 0));
        bottom.setOpaque(false);
        bottom.add(stepsCard);
        bottom.add(activityCard);

        JPanel column = new JPanel(new GridBagLayout());
        column.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;

        gauges.setPreferredSize(new Dimension(100, 150));
        gauges.setMinimumSize(new Dimension(100, 150));
        visualCard.setMinimumSize(new Dimension(100, 160));
        bottom.setMinimumSize(new Dimension(100, 130));

        c.gridy = 0; c.weighty = 0; c.insets = new Insets(0, 0, 12, 0);
        column.add(gauges, c);
        c.gridy = 1; c.weighty = 3;
        column.add(visualCard, c);
        c.gridy = 2; c.weighty = 2; c.insets = new Insets(0, 0, 0, 0);
        column.add(bottom, c);
        return column;
    }

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

    // ----------------------------------------------------- structure state

    private String selected() {
        return (String) structure.getSelectedItem();
    }

    private Kind kindOf(String name) {
        switch (name) {
            case "Stack": return Kind.STACK;
            case "Linear Queue": return Kind.QUEUE;
            case "Circular Queue": return Kind.CIRCULAR;
            case "Singly Linked List": return Kind.SINGLY;
            default: return Kind.DOUBLY;
        }
    }

    private int[] snapshot(String name) {
        switch (name) {
            case "Stack": return stack.getSnapshot();
            case "Linear Queue": return queue.getSnapshot();
            case "Singly Linked List": return singlyList.getSnapshot();
            case "Doubly Linked List": return doublyList.getSnapshot();
            default: return circularQueue.getSlots();
        }
    }

    private int sizeOf(String name) {
        return name.equals("Circular Queue")
                ? circularQueue.getSize() : snapshot(name).length;
    }

    private void resetStructures() {
        stopBrackets();
        stack = new CustomStack();
        queue = new CustomQueue();
        circularQueue = new CircularQueue();
        singlyList = new SinglyLinkedList();
        doublyList = new DoublyLinkedList();
        steps.setText("");
        clearHistory();
        showCurrent("Structure reset.");
    }

    private void clearHistory() {
        stopBrackets();
        operationCount = 0;
        maxSteps = 1;
        maxMicros = 1;
        activity.clear();
        steps.setText("");
        sizeGauge.setValue(0, "0");
        opsGauge.setValue(0, "0");
        stepsGauge.setValue(0, "0");
        timeGauge.setValue(0, "0");
    }

    /** Draws the selected structure as it is right now (no highlight). */
    private void showCurrent(String message) {
        String name = selected();
        if (name.equals("Balanced Brackets")) {
            visualizer.clear("Enter an expression such as {[()]} and press Execute.");
        } else if (name.equals("Circular Queue")) {
            visualizer.showCircular(circularQueue.getSlots(), circularQueue.getFront(),
                    circularQueue.getSize(), -1, message);
        } else {
            visualizer.showLinear(kindOf(name), snapshot(name), -1, message);
        }
        sizeGauge.setValue(Math.min(1, sizeOf2(name) / 10.0), String.valueOf(sizeOf2(name)));
    }

    private int sizeOf2(String name) {
        return name.equals("Balanced Brackets") ? 0 : sizeOf(name);
    }

    private void updateOperations() {
        String selected = selected();
        String[] options;
        String info;

        switch (selected) {
            case "Stack":
                options = new String[]{"Push", "Pop", "Peek", "Display"};
                info = "LIFO: last in, first out. Push adds to the top, pop "
                        + "removes from the top.";
                break;
            case "Linear Queue":
                options = new String[]{"Enqueue", "Dequeue", "Peek", "Display"};
                info = "FIFO: first in, first out. Enqueue at the rear, "
                        + "dequeue from the front.";
                break;
            case "Circular Queue":
                options = new String[]{"Enqueue", "Dequeue", "Peek", "Display"};
                info = "A FIFO queue with 10 slots. The rear wraps around to "
                        + "reuse slots freed at the front.";
                break;
            case "Singly Linked List":
                options = new String[]{"Insert", "Delete", "Search", "Display"};
                info = "Each node points to the next one. Insert adds at the "
                        + "end; search walks from HEAD.";
                break;
            case "Doubly Linked List":
                options = new String[]{"Insert", "Delete", "Search",
                    "Display Forward", "Display Backward"};
                info = "Each node points both ways, so the list can be "
                        + "walked forward and backward.";
                break;
            default:
                options = new String[]{"Check Brackets"};
                info = "Uses a stack: push every opening bracket, pop when a "
                        + "matching closing bracket appears.";
        }

        operation.setModel(new DefaultComboBoxModel<>(options));
        input.setText(selected.equals("Balanced Brackets") ? "{[()]}" : "");
        input.setToolTipText(selected.equals("Balanced Brackets")
                ? "Enter an expression, e.g. {[()]}" : "Enter an integer");

        quickInfo.setText("<html><div style='width:190px'><b>" + selected
                + "</b><br>" + info + "</div></html>");
        quickInfo.setFont(Theme.SMALL_FONT);
        quickInfo.setForeground(Theme.TEXT);
    }

    // ------------------------------------------------------------ actions

    private void execute() {
        String name = selected();
        String op = (String) operation.getSelectedItem();
        String value = input.getText().trim();

        try {
            if (name == null || op == null) {
                throw new IllegalArgumentException("Please select a structure and operation.");
            }
            stopBrackets();
            int before = name.equals("Balanced Brackets") ? 0 : sizeOf(name);
            OperationReport report = perform(name, op, value);

            if (name.equals("Balanced Brackets")) {
                showBrackets(value, report);
                return;
            }

            int after = sizeOf(name);
            boolean added = after > before;
            String message = lastStep(report);
            if (name.equals("Circular Queue")) {
                visualizer.showCircular(circularQueue.getSlots(), circularQueue.getFront(),
                        circularQueue.getSize(),
                        added ? circularQueue.getRear() : -1, message);
            } else {
                visualizer.showLinear(kindOf(name), snapshot(name),
                        added ? after - 1 : -1, message);
            }
            record(after, report, name + "  |  " + op);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + e.getMessage(),
                    "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private OperationReport perform(String name, String op, String value) {
        switch (name) {
            case "Stack":
                switch (op) {
                    case "Push": return stack.push(parseValue(value));
                    case "Pop": return stack.pop();
                    case "Peek": return stack.peek();
                    default: return stack.display();
                }
            case "Linear Queue":
                switch (op) {
                    case "Enqueue": return queue.enqueue(parseValue(value));
                    case "Dequeue": return queue.dequeue();
                    case "Peek": return queue.peek();
                    default: return queue.display();
                }
            case "Circular Queue":
                switch (op) {
                    case "Enqueue": return circularQueue.enqueue(parseValue(value));
                    case "Dequeue": return circularQueue.dequeue();
                    case "Peek": return circularQueue.peek();
                    default: return circularQueue.display();
                }
            case "Singly Linked List":
                switch (op) {
                    case "Insert": return singlyList.insert(parseValue(value));
                    case "Delete": return singlyList.delete(parseValue(value));
                    case "Search": return singlyList.search(parseValue(value));
                    default: return singlyList.display();
                }
            case "Doubly Linked List":
                switch (op) {
                    case "Insert": return doublyList.insert(parseValue(value));
                    case "Delete": return doublyList.delete(parseValue(value));
                    case "Search": return doublyList.search(parseValue(value));
                    case "Display Forward": return doublyList.displayForward();
                    default: return doublyList.displayBackward();
                }
            case "Balanced Brackets":
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("Enter an expression to check.");
                }
                return brackets.check(value);
            default:
                throw new IllegalArgumentException("Unknown data structure.");
        }
    }

    private int parseValue(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Please enter an integer value.");
        }
        return Integer.parseInt(value);
    }

    private static String lastStep(OperationReport report) {
        String[] lines = report.getSteps().trim().split("\n");
        String last = lines[lines.length - 1];
        return last.replaceFirst("^Step \\d+: ", "");
    }

    /** Updates gauges, the graph and the steps text after an operation. */
    private void record(int size, OperationReport report, String title) {
        operationCount++;
        double micros = report.getExecutionTime() / 1000.0;
        maxSteps = Math.max(maxSteps, report.getOperationCount());
        maxMicros = Math.max(maxMicros, micros);

        sizeGauge.setValue(Math.min(1, size / 10.0), String.valueOf(size));
        opsGauge.setValue(Math.min(1, operationCount / 25.0), String.valueOf(operationCount));
        stepsGauge.setValue((double) report.getOperationCount() / maxSteps,
                String.valueOf(report.getOperationCount()));
        timeGauge.setValue(micros / maxMicros, String.format("%.1fµs", micros));

        activity.addPoint(0, operationCount, size);
        activity.addPoint(1, operationCount, report.getOperationCount());

        steps.setText(title + "\n\n" + report);
        steps.setCaretPosition(0);
    }

    // ------------------------------------------------- bracket animation

    private static class BracketStep {
        final int index;
        final String stack;
        final String status;
        final boolean error;

        BracketStep(int index, String stack, String status, boolean error) {
            this.index = index;
            this.stack = stack;
            this.status = status;
            this.error = error;
        }
    }

    private void showBrackets(String expr, OperationReport report) {
        List<BracketStep> list = new ArrayList<>();
        StringBuilder open = new StringBuilder();
        boolean failed = false;

        for (int i = 0; i < expr.length() && !failed; i++) {
            char c = expr.charAt(i);
            if ("({[".indexOf(c) >= 0) {
                open.append(c);
                list.add(new BracketStep(i, open.toString(), "Push '" + c + "'", false));
            } else if (")}]".indexOf(c) >= 0) {
                if (open.length() == 0) {
                    list.add(new BracketStep(i, "", "'" + c + "' has no opening bracket", true));
                    failed = true;
                } else {
                    char top = open.charAt(open.length() - 1);
                    boolean ok = "({[".indexOf(top) == ")}]".indexOf(c);
                    if (ok) {
                        open.setLength(open.length() - 1);
                        list.add(new BracketStep(i, open.toString(),
                                "Matched " + top + c + "  (pop)", false));
                    } else {
                        list.add(new BracketStep(i, open.toString(),
                                "Mismatch: " + top + " and " + c, true));
                        failed = true;
                    }
                }
            } else {
                list.add(new BracketStep(i, open.toString(), "Skip '" + c + "'", false));
            }
        }
        if (!failed) {
            list.add(open.length() == 0
                    ? new BracketStep(expr.length(), "", "Balanced", false)
                    : new BracketStep(expr.length(), open.toString(),
                            "Not balanced: unmatched opening brackets remain", true));
        }

        record(open.length(), report, "Balanced Brackets  |  " + expr);

        final int[] next = {0};
        bracketTimer = new Timer(450, e -> {
            BracketStep s = list.get(next[0]++);
            visualizer.showBrackets(expr, s.index, s.stack, s.status, s.error);
            if (next[0] >= list.size()) {
                stopBrackets();
            }
        });
        bracketTimer.setInitialDelay(0);
        bracketTimer.start();
    }

    private void stopBrackets() {
        if (bracketTimer != null) {
            bracketTimer.stop();
            bracketTimer = null;
        }
    }
}
