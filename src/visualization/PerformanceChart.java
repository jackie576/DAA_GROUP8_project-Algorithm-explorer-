// Charts how the number of comparisons or writes changes across input sizes.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// these imports are needed for the classes used in this file.
import frontend.Theme;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

// Draws a chart showing how the number of comparisons or writes changes across input sizes.
public class PerformanceChart extends JComponent {

    public static final Color COMPARISONS = Theme.BAR_UNSORTED;
    public static final Color MOVES = Theme.BAR_COMPARING;

    private final List<int[]> points = new ArrayList<>();
    private int xMax = 1;
    private int yMax = 1;

    public PerformanceChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 150));
    }

    public void setScale(int xMax, int yMax) {
        this.xMax = Math.max(1, xMax);
        this.yMax = Math.max(1, yMax);
        repaint();
    }

    public void clear() {
        points.clear();
        repaint();
    }

    public void addPoint(int step, int comparisons, int moves) {
        points.add(new int[]{step, comparisons, moves});
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        int left = 44;
        int right = 14;
        int top = 10;
        int bottom = 26;
        int w = getWidth() - left - right;
        int h = getHeight() - top - bottom;
        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        for (int i = 0; i <= 4; i++) {
            int y = top + h - h * i / 4;
            g2.setColor(Theme.GRID);
            g2.drawLine(left, y, left + w, y);
            g2.setColor(Theme.MUTED);
            String label = String.valueOf(Math.round((double) yMax * i / 4));
            g2.drawString(label, left - 6 - fm.stringWidth(label), y + 4);
        }
        for (int i = 0; i <= 4; i++) {
            int x = left + w * i / 4;
            g2.setColor(Theme.MUTED);
            String label = String.valueOf(Math.round((double) xMax * i / 4));
            g2.drawString(label, x - fm.stringWidth(label) / 2, top + h + 18);
        }
        g2.setColor(Theme.BORDER);
        g2.drawLine(left, top + h, left + w, top + h);
        g2.drawLine(left, top, left, top + h);

        drawSeries(g2, 1, COMPARISONS, left, top, w, h);
        drawSeries(g2, 2, MOVES, left, top, w, h);
        g2.dispose();
    }

    private void drawSeries(Graphics2D g2, int column, Color color,
                            int left, int top, int w, int h) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        int prevX = left;
        int prevY = top + h;
        for (int[] p : points) {
            int x = left + (int) ((double) p[0] / xMax * w);
            int y = top + h - (int) ((double) p[column] / yMax * h);
            g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
        if (!points.isEmpty()) {
            g2.fillOval(prevX - 4, prevY - 4, 8, 8);
        }
    }
}
