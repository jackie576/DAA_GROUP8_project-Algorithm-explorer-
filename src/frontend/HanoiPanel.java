// Provides the interactive Tower of Hanoi visual game and controls.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import recursion.TowerOfHanoi;
import visualization.AnimationController;
import visualization.HanoiVisualizer;

// this is the class that provides the interactive Tower of Hanoi visual game and controls.
public class HanoiPanel extends JPanel
        implements AnimationController.Listener, HanoiVisualizer.Listener {

    private static final String[] PEG = {"A", "B", "C"};
    private static final String PROMPT =
            "Click a peg to pick up its top disk, then click where to drop it.";

    private final JComboBox<String> algorithmBox;
    private final JComboBox<String> disksBox = Theme.combo(
            new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
    private final HanoiVisualizer board = new HanoiVisualizer();
    private final JLabel movesValue = statValue();
    private final JLabel minimumValue = statValue();
    private final JLabel disksValue = statValue();
    private final JLabel status = new JLabel(PROMPT);

    private final AnimationController controller;
    private final PlaybackControls playback;

    private List<int[]> plan = new ArrayList<>();
    private final Deque<int[]> history = new ArrayDeque<>();
    private int moves;
    private boolean solving;
    private boolean undoing;
    private boolean syncing;

    // class constructor that initializes the HanoiPanel with algorithm options and a callback for algorithm selection.
    public HanoiPanel(String[] algorithms, Consumer<String> onAlgorithmChosen) {

        controller = new AnimationController(this);
        playback = new PlaybackControls(
                this::togglePause, this::step, this::newGame,
                controller::setSpeed);

        algorithmBox = Theme.combo(algorithms);
        disksBox.setSelectedItem("3");
        board.setListener(this);
        board.setInputEnabled(true);

        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buildLeftColumn(), BorderLayout.WEST);
        add(buildRightColumn(), BorderLayout.CENTER);

        algorithmBox.addActionListener(e -> {
            String name = (String) algorithmBox.getSelectedItem();
            if (!syncing && !"Tower of Hanoi".equals(name)) {
                onAlgorithmChosen.accept(name);
            }
        });
        disksBox.addActionListener(e -> newGame());

        newGame();
    }

    // Called by the Recursion page when another page changed the choice.
    public void select(String name) {
        syncing = true;
        algorithmBox.setSelectedItem(name);
        syncing = false;
    }

    // ---- layout -----------

    private JPanel buildLeftColumn() {

        JPanel controls = Theme.createCard();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        controls.add(section("Algorithm"));
        left(algorithmBox, 38);
        controls.add(algorithmBox);

        controls.add(Box.createVerticalStrut(14));
        controls.add(section("Number of Disks"));
        left(disksBox, 38);
        controls.add(disksBox);

        controls.add(Box.createVerticalStrut(14));
        JButton newGame = Theme.createButton("New Game");
        JButton solve = Theme.createButton("Solve");
        JButton undo = Theme.button("Undo", false);
        JPanel row = new JPanel(new GridLayout(1, 2, 6, 0));
        row.setOpaque(false);
        row.add(newGame);
        row.add(solve);
        left(row, 42);
        controls.add(row);
        controls.add(Box.createVerticalStrut(8));
        left(undo, 40);
        controls.add(undo);

        controls.add(Box.createVerticalStrut(16));
        controls.add(section("Solver Controls"));
        left(playback, 80);
        controls.add(playback);

        JPanel info = Theme.createCard();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(section("Rules"));
        JLabel rules = new JLabel("<html><div style='width:190px'>"
                + "Move all disks from A to C.<br>"
                + "One disk at a time, and never a larger disk on a smaller one."
                + "<br><br><span style='color:#7c7469'>"
                + "Recursive idea: move n-1 disks aside, move the biggest, "
                + "then move the n-1 disks back on top.</span></div></html>");
        rules.setFont(Theme.SMALL_FONT);
        rules.setForeground(Theme.TEXT);
        rules.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(rules);

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

        newGame.addActionListener(e -> newGame());
        solve.addActionListener(e -> solve());
        undo.addActionListener(e -> undo());
        return column;
    }

    // Builds the right column of the panel, which contains the game board and statistics.
    private JPanel buildRightColumn() {

        JPanel boardCard = Theme.createCard();
        boardCard.setLayout(new BorderLayout(0, 8));
        boardCard.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundBorder(Theme.BORDER, Theme.RADIUS),
                BorderFactory.createEmptyBorder(12, 14, 10, 14)));
        boardCard.add(Theme.heading("Tower of Hanoi"), BorderLayout.NORTH);
        boardCard.add(board, BorderLayout.CENTER);

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(statBox("Moves", movesValue));
        stats.add(statBox("Minimum moves (2^n - 1)", minimumValue));
        stats.add(statBox("Disks", disksValue));

        JPanel statusCard = Theme.createCard();
        statusCard.setLayout(new BorderLayout());
        statusCard.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundBorder(Theme.BORDER, Theme.RADIUS),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        status.setFont(Theme.NORMAL_FONT);
        status.setForeground(Theme.TEXT);
        statusCard.add(status, BorderLayout.CENTER);

        JPanel column = new JPanel(new GridBagLayout());
        column.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;

        c.gridy = 0; c.weighty = 1; c.insets = new Insets(0, 0, 12, 0);
        boardCard.setMinimumSize(new Dimension(100, 260));
        column.add(boardCard, c);

        c.gridy = 1; c.weighty = 0;
        stats.setPreferredSize(new Dimension(100, 78));
        stats.setMinimumSize(new Dimension(100, 78));
        column.add(stats, c);

        c.gridy = 2; c.insets = new Insets(0, 0, 0, 0);
        statusCard.setPreferredSize(new Dimension(100, 50));
        statusCard.setMinimumSize(new Dimension(100, 50));
        column.add(statusCard, c);
        return column;
    }

    // Creates a JLabel for displaying a statistic value with a specific font and color.
    private static JLabel statValue() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(Theme.TEXT);
        return label;
    }

    // Creates a JPanel that displays a statistic caption and its corresponding value.
    private JPanel statBox(String caption, JLabel value) {
        JPanel box = Theme.createCard();
        box.setLayout(new BorderLayout(0, 2));
        box.setBorder(BorderFactory.createCompoundBorder(
                new Theme.RoundBorder(Theme.BORDER, Theme.RADIUS),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        JLabel title = new JLabel(caption);
        title.setFont(Theme.SMALL_FONT);
        title.setForeground(Theme.MUTED);
        box.add(title, BorderLayout.NORTH);
        box.add(value, BorderLayout.CENTER);
        return box;
    }

    // Creates a JLabel for a section heading with a specific font, alignment, and border.
    private JLabel section(String text) {
        JLabel label = Theme.heading(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    // Sets the alignment and maximum size of a component to ensure it fits within the layout.
    private void left(JComponent c, int height) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    // ---- actions ---------

    private int disks() {
        return Integer.parseInt((String) disksBox.getSelectedItem());
    }

    // Resets the game to its initial state, clearing the board and history, and updating the statistics.
    private void newGame() {
        controller.load(0);
        plan = new ArrayList<>();
        solving = false;
        moves = 0;
        history.clear();
        board.reset(disks());
        board.setInputEnabled(true);
        updateStats();
        status.setText(PROMPT);
    }

    // Starts the solver by generating the solution plan and disabling user input on the board.
    private void startSolver() {
        newGame();
        plan = TowerOfHanoi.solve(disks());
        solving = true;
        board.setInputEnabled(false);
        controller.load(plan.size());
    }

    // Initiates the solving process by starting the solver and playing the animation of the solution.
    private void solve() {
        startSolver();
        controller.play();
    }

    // Performs a single step of the solution, either by starting the solver or stepping through the plan.
    private void step() {
        if (!controller.isLoaded()) {
            startSolver();
        }
        controller.stepOnce();
    }

    // Toggles the pause state of the animation controller.
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

    // Undoes the last move made by the player, restoring the previous state of the board and updating the statistics.
    private void undo() {
        if (solving || history.isEmpty()) {
            return;
        }
        int[] last = history.pop();
        undoing = true;
        board.move(last[1], last[0], true, 250);
        undoing = false;
        status.setText("Last move undone.");
    }

    // Updates the displayed statistics for moves, minimum moves, and number of disks based on the current game state.
    private void updateStats() {
        movesValue.setText(String.valueOf(moves));
        minimumValue.setText(String.valueOf(TowerOfHanoi.minimumMoves(disks())));
        disksValue.setText(String.valueOf(disks()));
    }

    // ------------------------------------------------ board listener

    @Override
    public void onSelected(int disk) {
        status.setText(disk == 0 ? PROMPT
                : "Disk " + disk + " selected. Click a peg to drop it there.");
    }

    @Override
    public void onMoved(int disk, int from, int to) {
        if (undoing) {
            moves--;
        } else {
            moves++;
            history.push(new int[]{from, to});
        }
        updateStats();
        if (solving) {
            status.setText("Move disk " + disk + " from " + PEG[from]
                    + " to " + PEG[to] + ".");
        }
    }

    @Override
    public void onInvalid(String message) {
        status.setText(message);
    }

    @Override
    public void onSolved() {
        long min = TowerOfHanoi.minimumMoves(disks());
        status.setText("Solved in " + moves + " moves"
                + (moves == min ? " - the minimum possible!"
                        : ". The minimum is " + min + "."));
        solving = false;
        board.setInputEnabled(true);
    }

    // ------------------------------------- animation controller listener

    @Override
    public void onStep(int index) {
        int[] m = plan.get(index);
        int duration = (int) Math.min(450, controller.intervalMs() * 0.8);
        board.move(m[0], m[1], true, duration);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onStateChanged() {
        playback.setPlaying(!controller.isPaused());
    }
}
