// Draws the Tower of Hanoi pegs and moving disks in real time.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// these are the imports for the classes and packages used in this file.
import frontend.Theme;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

//

// Game board for Tower of Hanoi: three pegs, coloured disks, click a peg to

// lift its top disk and click another peg to drop it. Moves are animated as
public class HanoiVisualizer extends JComponent {

    public interface Listener {
        void onSelected(int disk);          // 0 = nothing selected
        void onMoved(int disk, int from, int to);
        void onInvalid(String message);
        void onSolved();
    }

    private static final Color[] DISK_COLORS = {
        new Color(32, 199, 217), new Color(36, 140, 210),
        new Color(46, 160, 90), new Color(240, 140, 30),
        new Color(214, 84, 84), new Color(150, 110, 200),
        new Color(200, 150, 20), new Color(90, 100, 110)
    };
    private static final String[] PEG_NAMES = {"A", "B", "C"};
    private static final String[] PEG_NOTES = {"Start", "Spare", "Goal"};

    private final List<List<Integer>> pegs = new ArrayList<>();
    private int disks;
    private int selected = -1;
    private boolean inputEnabled = true;
    private Listener listener;

    private int movingDisk;
    private int movingFrom;
    private int movingTo;
    private double movingT;
    private Timer animation;

    public HanoiVisualizer() {
        setOpaque(false);
        setPreferredSize(new Dimension(560, 320));
        for (int i = 0; i < 3; i++) {
            pegs.add(new ArrayList<>());
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX());
            }
        });
    }

    // Sets the listener for events such as disk selection, movement, invalid moves, and puzzle completion.
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    // Enables or disables user input for selecting and moving disks, and updates the cursor accordingly.
    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
        setCursor(enabled ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());
    }

    // Resets the puzzle to its initial state.
    public void reset(int diskCount) {
        if (animation != null) {
            animation.stop();
        }
        movingDisk = 0;
        selected = -1;
        disks = diskCount;
        for (List<Integer> peg : pegs) {
            peg.clear();
        }
        for (int d = diskCount; d >= 1; d--) {
            pegs.get(0).add(d);
        }
        repaint();
    }

    // Moves the top disk of one peg to another if the rules allow it.
    public boolean move(int from, int to, boolean animate, int durationMs) {
        finishAnimation();

        List<Integer> source = pegs.get(from);
        List<Integer> target = pegs.get(to);
        if (from == to || source.isEmpty()) {
            return false;
        }
        int disk = source.get(source.size() - 1);
        if (!target.isEmpty() && target.get(target.size() - 1) < disk) {
            return false;
        }

        selected = -1;
        source.remove(source.size() - 1);
        if (listener != null) {
            listener.onMoved(disk, from, to);
        }

        if (!animate) {
            target.add(disk);
            repaint();
            checkSolved();
            return true;
        }

        movingDisk = disk;
        movingFrom = from;
        movingTo = to;
        movingT = 0;
        long start = System.nanoTime();
        animation = new Timer(16, e -> {
            movingT = Math.min(1,
                    (System.nanoTime() - start) / 1_000_000.0 / durationMs);
            repaint();
            if (movingT >= 1) {
                finishAnimation();
            }
        });
        animation.start();
        return true;
    }

    // Completes the animation of a disk moving from one peg to another, updating the state and checking if the puzzle is solved.
    private void finishAnimation() {
        if (movingDisk == 0) {
            return;
        }
        animation.stop();
        pegs.get(movingTo).add(movingDisk);
        movingDisk = 0;
        repaint();
        checkSolved();
    }

    // Checks if the puzzle is solved by verifying if all disks are on the last peg, and notifies the listener if it is.
    private void checkSolved() {
        if (disks > 0 && pegs.get(2).size() == disks && listener != null) {
            listener.onSolved();
        }
    }

    // Handles a mouse click on the visualizer, determining which peg was clicked and managing disk selection and movement accordingly.
    private void handleClick(int x) {
        if (!inputEnabled || movingDisk != 0 || listener == null) {
            return;
        }
        int peg = Math.max(0, Math.min(2, (int) (x / (getWidth() / 3.0))));
        List<Integer> clicked = pegs.get(peg);

        if (selected == -1) {
            if (!clicked.isEmpty()) {
                selected = peg;
                listener.onSelected(clicked.get(clicked.size() - 1));
            }
        } else if (selected == peg) {
            selected = -1;
            listener.onSelected(0);
        } else {
            int from = selected;
            if (!move(from, peg, true, 260)) {
                selected = -1;
                listener.onSelected(0);
                listener.onInvalid(
                        "A larger disk can't be placed on a smaller one.");
            }
        }
        repaint();
    }

    // Easing function for smooth animation of disk movement.
    private static double ease(double t) {
        return t * t * (3 - 2 * t);
    }

    // Linear interpolation between two values a and b based on parameter t.
    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int baseY = h - 50;
        int count = Math.max(disks, 1);
        // disk height leaves room above the pegs for a lifted disk
        int diskH = (int) Math.max(12, Math.min(34, (baseY - 40.0) / (count + 2.5)));
        int pegH = (count + 1) * diskH + 10;
        double slot = w / 3.0;
        int maxW = (int) (slot * 0.88);
        int minW = Math.min(56, maxW / 2);
        int liftY = baseY - pegH - diskH - 6;

        // base plate and pegs
        g2.setColor(new Color(150, 140, 126));
        g2.fillRect(12, baseY, w - 24, 14);
        for (int i = 0; i < 3; i++) {
            int cx = (int) (slot * (i + 0.5));
            g2.setColor(i == selected ? Theme.ACCENT : new Color(110, 102, 92));
            g2.fillRect(cx - 5, baseY - pegH, 10, pegH);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.setColor(Theme.TEXT);
            String name = PEG_NAMES[i];
            g2.drawString(name, cx - g2.getFontMetrics().stringWidth(name) / 2,
                    baseY + 32);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(Theme.MUTED);
            g2.drawString(PEG_NOTES[i],
                    cx + 14, baseY + 32);
        }

        // resting disks
        for (int p = 0; p < 3; p++) {
            List<Integer> peg = pegs.get(p);
            int cx = (int) (slot * (p + 0.5));
            for (int k = 0; k < peg.size(); k++) {
                int disk = peg.get(k);
                boolean lifted = p == selected && k == peg.size() - 1;
                int y = lifted ? liftY : baseY - (k + 1) * diskH;
                drawDisk(g2, disk, cx, y, diskH, minW, maxW);
            }
        }

        // disk in flight
        if (movingDisk != 0) {
            double x0 = slot * (movingFrom + 0.5);
            double x1 = slot * (movingTo + 0.5);
            double y0 = baseY - (pegs.get(movingFrom).size() + 1) * diskH;
            double y1 = baseY - (pegs.get(movingTo).size() + 1) * diskH;
            double x;
            double y;
            if (movingT < 0.3) {
                x = x0;
                y = lerp(y0, liftY, ease(movingT / 0.3));
            } else if (movingT < 0.7) {
                x = lerp(x0, x1, ease((movingT - 0.3) / 0.4));
                y = liftY;
            } else {
                x = x1;
                y = lerp(liftY, y1, ease((movingT - 0.7) / 0.3));
            }
            drawDisk(g2, movingDisk, (int) x, (int) y, diskH, minW, maxW);
        }
        g2.dispose();
    }

    // Draws a single disk with a specific size and color at the given coordinates.
    private void drawDisk(Graphics2D g2, int disk, int cx, int y,
                          int diskH, int minW, int maxW) {
        int width = disks <= 1 ? maxW
                : (int) (minW + (maxW - minW) * (disk - 1.0) / (disks - 1));
        g2.setColor(DISK_COLORS[(disk - 1) % DISK_COLORS.length]);
        g2.fillRoundRect(cx - width / 2, y, width, diskH - 2, 6, 6);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, diskH / 2)));
        FontMetrics fm = g2.getFontMetrics();
        String label = String.valueOf(disk);
        g2.drawString(label, cx - fm.stringWidth(label) / 2,
                y + (diskH - 2 + fm.getAscent() - fm.getDescent()) / 2);
    }
}
