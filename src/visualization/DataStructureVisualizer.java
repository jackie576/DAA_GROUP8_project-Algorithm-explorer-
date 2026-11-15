// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// these are the imports for the classes and packages used in this file.
import frontend.Theme;
import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.JComponent;
import javax.swing.Timer;

// Draws the current state of a data structure for animation playback.
public class DataStructureVisualizer extends JComponent {

    public enum Kind { NONE, STACK, QUEUE, CIRCULAR, SINGLY, DOUBLY, BRACKETS }

    private static final Color ERROR = new Color(214, 84, 84);
    private static final Color FRAME = new Color(110, 102, 92);

    private Kind kind = Kind.NONE;
    private int[] values = new int[0];
    private int front;
    private int size;
    private int highlight = -1;
    private long highlightStart;
    private String message = "";
    private final Timer blipTimer;

    private String expression = "";
    private int bracketIndex = -1;
    private String bracketStack = "";
    private String bracketStatus = "";
    private boolean bracketError;

    // class constructor that initializes the DataStructureVisualizer with default settings.
    public DataStructureVisualizer() {
        setOpaque(false);
        setPreferredSize(new Dimension(500, 240));
        blipTimer = new Timer(16, e -> {
            repaint();
            if (blip() == 0) {
                ((Timer) e.getSource()).stop();
            }
        });
    }

    // Clear the panel so the UI can show a generic message instead of a stale structure.
    public void clear(String text) {
        kind = Kind.NONE;
        values = new int[0];
        highlight = -1;
        message = text;
        repaint();
    }

    // Show a simple linear container such as a stack or queue and highlight the element
    // the last operation just touched so the student can see the action in context.
    public void showLinear(Kind kind, int[] values, int highlightIndex, String message) {
        this.kind = kind;
        this.values = values.clone();
        this.message = message;
        startBlip(highlightIndex);
    }

    // Show the current ring-buffer layout, including the front pointer, the active size,
    // and the slot that just changed so wrap-around behavior is easy to follow.
    public void showCircular(int[] slots, int front, int size,
                             int highlightSlot, String message) {
        this.kind = Kind.CIRCULAR;
        this.values = slots.clone();
        this.front = front;
        this.size = size;
        this.message = message;
        startBlip(highlightSlot);
    }

    // Show a singly or doubly linked list with the head and tail labeled, and highlight
    public void showBrackets(String expression, int index, String stack,
                             String status, boolean error) {
        this.kind = Kind.BRACKETS;
        this.expression = expression;
        this.bracketIndex = index;
        this.bracketStack = stack;
        this.bracketStatus = status;
        this.bracketError = error;
        this.message = "";
        this.highlight = -1;
        repaint();
    }

    // Show a singly or doubly linked list with the head and tail labeled, and highlight
    private void startBlip(int index) {
        highlight = index;
        highlightStart = System.nanoTime();
        if (index >= 0) {
            blipTimer.restart();
        }
        repaint();
    }

    // 0 to 1 and back to 0 over about half a second.
    private double blip() {
        if (highlight < 0) {
            return 0;
        }
        double t = (System.nanoTime() - highlightStart) / 1_000_000.0 / 550;
        return t >= 1 ? 0 : Math.sin(Math.PI * t);
    }

    // The paint routine chooses a drawing strategy based on the current data structure.
    // That keeps the panel readable while the learner watches a single step at a time.
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int areaH = h - 34;

        switch (kind) {
            case STACK: drawStack(g2, w, areaH); break;
            case QUEUE: drawQueue(g2, w, areaH); break;
            case CIRCULAR: drawCircular(g2, w, areaH); break;
            case SINGLY: drawList(g2, w, areaH, false); break;
            case DOUBLY: drawList(g2, w, areaH, true); break;
            case BRACKETS: drawBrackets(g2, w, areaH); break;
            default:
                text(g2, message, w / 2, areaH / 2, 14, Theme.MUTED, true);
        }

        if (kind != Kind.NONE && !message.isEmpty()) {
            text(g2, message, 14, h - 12, 13, Theme.TEXT, false);
        }
        g2.dispose();
    }

    // Draw a stack with the top at the bottom of the panel.
    private void drawStack(Graphics2D g2, int w, int areaH) {
        int n = values.length;
        int bucketW = Math.min(170, w / 2);
        int x = (w - bucketW) / 2;
        int top = 12;
        int bottom = areaH - 6;

        g2.setColor(FRAME);
        g2.fillRect(x - 4, top, 4, bottom - top + 4);
        g2.fillRect(x + bucketW, top, 4, bottom - top + 4);
        g2.fillRect(x - 4, bottom, bucketW + 8, 4);

        if (n == 0) {
            text(g2, "Stack is empty", w / 2, (top + bottom) / 2, 14, Theme.MUTED, true);
            return;
        }
        int bh = (int) Math.max(14, Math.min(40, (bottom - top - 6.0) / Math.max(n, 5)));
        for (int i = 0; i < n; i++) {
            int y = bottom - (i + 1) * bh - (i == highlight ? (int) (blip() * 12) : 0);
            element(g2, x + 4, y + 1, bucketW - 8, bh - 3, i == highlight,
                    String.valueOf(values[i]));
        }
        int topY = bottom - n * bh;
        arrowRight(g2, x - 70, topY + bh / 2, x - 10, topY + bh / 2);
        text(g2, "TOP", x - 70, topY + bh / 2 - 8, 12, Theme.TEXT, false);
        text(g2, "BOTTOM", x + bucketW + 14, bottom - 2, 11, Theme.MUTED, false);
    }

    // Draw a queue with the front on the left and the rear on the right, labeling both ends.
    private void drawQueue(Graphics2D g2, int w, int areaH) {
        int n = values.length;
        int cy = areaH / 2;
        int bw = (int) Math.max(30, Math.min(70, (w - 190.0) / Math.max(n, 6)));
        int bh = 46;

        text(g2, "dequeue", 14, cy - 54, 11, Theme.MUTED, false);
        arrowLeft(g2, 80, cy - 44, 14, cy - 44);
        text(g2, "enqueue", w - 64, cy - 54, 11, Theme.MUTED, false);
        arrowRight(g2, w - 80, cy - 44, w - 14, cy - 44);

        if (n == 0) {
            text(g2, "Queue is empty", w / 2, cy, 14, Theme.MUTED, true);
            return;
        }
        int startX = (w - n * bw) / 2;
        for (int i = 0; i < n; i++) {
            int lift = i == highlight ? (int) (blip() * 12) : 0;
            element(g2, startX + i * bw + 2, cy - bh / 2 - lift, bw - 4, bh,
                    i == highlight, String.valueOf(values[i]));
        }
        text(g2, "FRONT", startX + bw / 2, cy + bh / 2 + 24, 12, Theme.TEXT, true);
        text(g2, "REAR", startX + (n - 1) * bw + bw / 2, cy + bh / 2 + 24, 12, Theme.TEXT, true);
        g2.setColor(Theme.TEXT);
        g2.drawLine(startX + bw / 2, cy + bh / 2 + 4, startX + bw / 2, cy + bh / 2 + 10);
        g2.drawLine(startX + (n - 1) * bw + bw / 2, cy + bh / 2 + 4,
                startX + (n - 1) * bw + bw / 2, cy + bh / 2 + 10);
    }

    // Draw the current state of a circular queue, including the front pointer, the active size,
    private void drawCircular(Graphics2D g2, int w, int areaH) {
        int cap = values.length;
        int cw = (int) Math.min(64, (w - 60.0) / cap);
        int ch = 46;
        int startX = (w - cap * cw) / 2;
        int cy = areaH / 2 + 10;
        int rear = size == 0 ? -1 : (front + size - 1) % cap;

        for (int k = 0; k < cap; k++) {
            int x = startX + k * cw;
            boolean used = ((k - front + cap) % cap) < size;
            int lift = k == highlight ? (int) (blip() * 12) : 0;

            if (used) {
                element(g2, x + 2, cy - ch / 2 - lift, cw - 4, ch,
                        k == highlight, String.valueOf(values[k]));
            } else {
                g2.setColor(Theme.SURFACE);
                g2.fillRect(x + 2, cy - ch / 2, cw - 4, ch);
                g2.setColor(Theme.BORDER);
                g2.drawRect(x + 2, cy - ch / 2, cw - 4, ch);
            }
            text(g2, String.valueOf(k), x + cw / 2, cy + ch / 2 + 16, 11,
                    Theme.MUTED, true);

            if (size > 0 && (k == front || k == rear)) {
                String tag = k == front && k == rear ? "FRONT / REAR"
                        : (k == front ? "FRONT" : "REAR");
                text(g2, tag, x + cw / 2, cy + ch / 2 + 34, 11, Theme.TEXT, true);
            }
        }

        // wrap-around arrow from the last slot back to slot 0
        int xr = startX + (cap - 1) * cw + cw / 2;
        int xl = startX + cw / 2;
        int top = cy - ch / 2 - 8;
        g2.setColor(Theme.MUTED);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
        g2.draw(new java.awt.geom.QuadCurve2D.Double(xr, top, (xl + xr) / 2.0,
                top - 44, xl, top));
        g2.setStroke(new BasicStroke(1f));
        Path2D head = new Path2D.Double();
        head.moveTo(xl, top + 2);
        head.lineTo(xl - 4, top - 6);
        head.lineTo(xl + 4, top - 6);
        head.closePath();
        g2.fill(head);
        text(g2, "wraps around to slot 0", w / 2, top - 26, 11, Theme.MUTED, true);
        text(g2, "size " + size + " / " + cap, 14, 20, 12, Theme.TEXT, false);
    }

    // Draw the current state of a doubly linked list
    private void drawList(Graphics2D g2, int w, int areaH, boolean doubly) {
        int n = values.length;
        int dataW = 44;
        int ptrW = 18;
        int nodeW = doubly ? dataW + 2 * ptrW : dataW + ptrW;
        int gap = 36;
        int nodeH = 36;
        int perRow = Math.max(1, (w - 70) / (nodeW + gap));
        int rows = Math.max(1, (n + perRow - 1) / perRow);
        int rowH = 80;
        int startY = Math.max(28, (areaH - rows * rowH) / 2 + 22);

        if (n == 0) {
            text(g2, "List is empty (HEAD = NULL)", w / 2, areaH / 2, 14, Theme.MUTED, true);
            return;
        }
        int cols = Math.min(n, perRow);
        int startX = Math.max(20, (w - (cols * (nodeW + gap) + 20)) / 2);

        for (int i = 0; i < n; i++) {
            int row = i / perRow;
            int col = i % perRow;
            int x = startX + col * (nodeW + gap);
            int y = startY + row * rowH;
            int lift = i == highlight ? (int) (blip() * 12) : 0;

            int dx = x + (doubly ? ptrW : 0);
            element(g2, dx, y - lift, dataW, nodeH, i == highlight,
                    String.valueOf(values[i]));
            if (doubly) {
                pointerCell(g2, x, y - lift, ptrW, nodeH);
            }
            pointerCell(g2, dx + dataW, y - lift, ptrW, nodeH);

            if (i == 0) {
                text(g2, "HEAD", x + nodeW / 2, y - 14, 12, Theme.TEXT, true);
            }
            if (i == n - 1 && doubly) {
                text(g2, "TAIL", x + nodeW / 2, y - 14, 12, Theme.TEXT, true);
            }

            int midY = y + nodeH / 2;
            boolean lastInRow = col == perRow - 1;
            if (i < n - 1 && !lastInRow) {
                int ax = x + nodeW;
                arrowRight(g2, ax, midY - (doubly ? 5 : 0), ax + gap, midY - (doubly ? 5 : 0));
                if (doubly) {
                    arrowLeft(g2, ax + gap, midY + 5, ax, midY + 5);
                }
            } else if (i < n - 1) {
                int nx = startX + nodeW / 2;
                int ny = startY + (row + 1) * rowH;
                g2.setColor(Theme.TEXT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(x + nodeW / 2, y + nodeH, nx, ny - 4);
                g2.setStroke(new BasicStroke(1f));
            } else {
                int ax = x + nodeW;
                arrowRight(g2, ax, midY, ax + gap - 4, midY);
                text(g2, "NULL", ax + gap, midY + 4, 12, Theme.MUTED, false);
            }
        }
    }

    // Draw the current state of a doubly linked list
    private void drawBrackets(Graphics2D g2, int w, int areaH) {
        int n = expression.length();
        int cell = (int) Math.max(14, Math.min(34, (w - 40.0) / Math.max(n, 1)));
        int startX = (w - n * cell) / 2;
        int y = 24;

        for (int i = 0; i < n; i++) {
            Color fill = Theme.SURFACE;
            Color ink = Theme.TEXT;
            if (i < bracketIndex) {
                fill = Theme.ACCENT_LIGHT;
            }
            if (i == bracketIndex) {
                fill = bracketError ? ERROR : Theme.BAR_COMPARING;
                ink = Color.WHITE;
            }
            g2.setColor(fill);
            g2.fillRect(startX + i * cell, y, cell - 2, 38);
            g2.setColor(Theme.BORDER);
            g2.drawRect(startX + i * cell, y, cell - 2, 38);
            g2.setColor(ink);
            g2.setFont(new Font("Consolas", Font.BOLD, Math.max(11, cell / 2 + 4)));
            String ch = String.valueOf(expression.charAt(i));
            g2.drawString(ch, startX + i * cell + (cell - 2 - g2.getFontMetrics().stringWidth(ch)) / 2, y + 25);
        }

        text(g2, "Stack of opening brackets", 20, y + 80, 13, Theme.TEXT, false);
        int sw = 40;
        int sy = y + 92;
        g2.setColor(FRAME);
        g2.fillRect(20, sy + 44, Math.max(sw * Math.max(bracketStack.length(), 4), 160), 4);
        for (int i = 0; i < bracketStack.length(); i++) {
            g2.setColor(Theme.BAR_UNSORTED);
            g2.fillRect(22 + i * sw, sy, sw - 4, 40);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Consolas", Font.BOLD, 20));
            String ch = String.valueOf(bracketStack.charAt(i));
            g2.drawString(ch, 22 + i * sw + (sw - 4 - g2.getFontMetrics().stringWidth(ch)) / 2, sy + 27);
        }
        if (!bracketStack.isEmpty()) {
            text(g2, "<- TOP", 22 + bracketStack.length() * sw + 4, sy + 25, 12, Theme.TEXT, false);
        }
        text(g2, bracketStatus, w / 2, areaH - 6, 16,
                bracketError ? ERROR : Theme.TEXT, true);
    }

    // Draw an element of the array
    private void element(Graphics2D g2, int x, int y, int w, int h,
                         boolean active, String label) {
        g2.setColor(active && blip() > 0 ? Theme.BAR_COMPARING : Theme.BAR_UNSORTED);
        g2.fillRect(x, y, w, h);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(11, Math.min(16, h / 2 + 2))));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, x + (w - fm.stringWidth(label)) / 2,
                y + (h + fm.getAscent() - fm.getDescent()) / 2);
    }

    // Draw a cell of the array
    private void pointerCell(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(Theme.SURFACE);
        g2.fillRect(x, y, w, h);
        g2.setColor(Theme.BAR_UNSORTED);
        g2.drawRect(x, y, w, h);
        g2.fillOval(x + w / 2 - 2, y + h / 2 - 2, 5, 5);
    }

    // Draw the given text at the specified location with the specified size and color, optionally centering it horizontally.
    private void text(Graphics2D g2, String s, int x, int y, int size,
                      Color color, boolean centered) {
        g2.setColor(color);
        g2.setFont(new Font("Segoe UI", size >= 14 ? Font.BOLD : Font.PLAIN, size));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(s, centered ? x - fm.stringWidth(s) / 2 : x, y);
    }

    // Draw a right-pointing arrow
    private void arrowRight(Graphics2D g2, int x1, int y1, int x2, int y2) {
        arrow(g2, x1, y1, x2, y2, 1);
    }

    // Draw a left-pointing arrow
    private void arrowLeft(Graphics2D g2, int x1, int y1, int x2, int y2) {
        arrow(g2, x1, y1, x2, y2, -1);
    }

    // Draw a right- or left-pointing arrow
    private void arrow(Graphics2D g2, int x1, int y1, int x2, int y2, int dir) {
        g2.setColor(Theme.TEXT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x1, y1, x2, y2);
        Path2D head = new Path2D.Double();
        head.moveTo(x2, y2);
        head.lineTo(x2 - 7 * dir, y2 - 4);
        head.lineTo(x2 - 7 * dir, y2 + 4);
        head.closePath();
        g2.fill(head);
        g2.setStroke(new BasicStroke(1f));
    }
}
