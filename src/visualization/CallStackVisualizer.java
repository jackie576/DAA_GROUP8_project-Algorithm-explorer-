// Draws the recursive call stack as a step-by-step trace for the user.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// these are the imports for the classes and packages used in this file.
import frontend.Theme;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import recursion.RecursionTrace;

//

// Draws the call stack: each active call is a box piled on the one that made
public class CallStackVisualizer extends JComponent {

    private static class Frame {
        final String label;
        final boolean cached;

        Frame(String label, boolean cached) {
            this.label = label;
            this.cached = cached;
        }
    }

    private final List<Frame> frames = new ArrayList<>();
    private String lastReturn = "";
    private String loopLine1 = "";
    private String loopLine2 = "";
    private String message = "Press Run to watch the calls pile up.";
    private double blip;

    // class constructor that initializes the CallStackVisualizer with default settings.
    public CallStackVisualizer() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 220));
    }

    // Clears the call stack visualizer, resetting all frames, messages, and blip state, and repaints the component.
    public void clear(String message) {
        frames.clear();
        lastReturn = "";
        loopLine1 = "";
        loopLine2 = "";
        blip = 0;
        this.message = message;
        repaint();
    }

    // Applies an event to the call stack visualizer, updating the frames, last return, loop lines, and blip state, and repaints the component.
    public void apply(RecursionTrace.Event e) {
        message = "";
        switch (e.type) {
            case CALL:
                frames.add(new Frame(e.label, e.cached));
                break;
            case RETURN:
                if (!frames.isEmpty()) {
                    frames.remove(frames.size() - 1);
                }
                lastReturn = e.label + " = " + e.detail;
                break;
            default:
                frames.clear();
                loopLine1 = e.label;
                loopLine2 = e.detail;
                lastReturn = e.detail;
        }
        repaint();
    }

    // Sets the blip value for the top frame in the call stack visualizer, which can be used to animate or highlight the top frame, and repaints the component.
    public void setBlip(double blip) {
        this.blip = blip;
        repaint();
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
        int x0 = 28;
        int colW = Math.min(300, (int) (w * 0.5));
        int bottom = h - 22;
        int top = 14;

        boolean loop = !loopLine1.isEmpty();
        if (frames.isEmpty() && !loop && lastReturn.isEmpty()) {
            g2.setFont(Theme.NORMAL_FONT);
            g2.setColor(Theme.MUTED);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(message, (w - fm.stringWidth(message)) / 2, h / 2);
            g2.dispose();
            return;
        }

        // ground line under the stack
        g2.setColor(new Color(150, 140, 126));
        g2.fillRect(x0 - 8, bottom, colW + 16, 4);

        if (loop) {
            int fh = 96;
            int y = bottom - fh - 8;
            g2.setColor(Theme.BAR_COMPARING);
            g2.fillRect(x0, y, colW, fh);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            center(g2, loopLine1, x0, colW, y + 38);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            center(g2, loopLine2, x0, colW, y + 66);
        } else {
            int n = frames.size();
            int fh = (int) Math.max(14, Math.min(40, (bottom - top - 6.0) / Math.max(n, 6)));
            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, fh / 2 + 1)));
            for (int i = 0; i < n; i++) {
                Frame f = frames.get(i);
                boolean isTop = i == n - 1;
                int y = bottom - 2 - (i + 1) * fh;
                int dx = isTop ? (int) Math.round(blip * 10) : 0;

                Color color = isTop ? Theme.BAR_COMPARING : Theme.BAR_UNSORTED;
                if (f.cached) {
                    color = Theme.BAR_SORTED;
                }
                g2.setColor(color);
                g2.fillRect(x0 + dx, y + 1, colW, fh - 3);
                g2.setColor(Color.WHITE);
                String text = f.cached ? f.label + "  (cached)" : f.label;
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text,
                        x0 + dx + (colW - fm.stringWidth(text)) / 2,
                        y + 1 + (fh - 3 + fm.getAscent() - fm.getDescent()) / 2);

                if (isTop) {
                    g2.setColor(Theme.MUTED);
                    g2.setFont(Theme.SMALL_FONT);
                    g2.drawString("top of stack", x0 + colW + 14 + dx,
                            y + fh / 2 + 4);
                    g2.setFont(new Font("Segoe UI", Font.BOLD,
                            Math.max(10, fh / 2 + 1)));
                }
            }
        }

        // last returned value
        int rx = x0 + colW + 110;
        if (!lastReturn.isEmpty() && rx + 120 < w) {
            g2.setColor(Theme.MUTED);
            g2.setFont(Theme.SMALL_FONT);
            g2.drawString(loop ? "LATEST VALUE" : "LAST RETURN", rx, top + 24);
            g2.setColor(Theme.TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            g2.drawString(lastReturn, rx, top + 54);
        }
        g2.dispose();
    }

    // Draws the given text centered horizontally within the specified width at the specified y-coordinate.
    private static void center(Graphics2D g2, String text, int x, int width, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y);
    }
}
