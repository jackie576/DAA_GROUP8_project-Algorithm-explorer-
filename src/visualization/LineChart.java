// Draws a multi-series chart with automatic scaling and axis formatting.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// this is the class that draws a multi-series chart with automatic scaling and axis formatting.
import frontend.Theme;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

// This class draws a multi-series chart with automatic scaling and axis formatting.
public class LineChart extends JComponent {

    private static class Series {
        final Color color;
        final List<double[]> points = new ArrayList<>();

        Series(Color color) {
            this.color = color;
        }
    }

    private final List<Series> series = new ArrayList<>();
    private boolean logY;
    private String xTitle = "";
    private String yTitle = "";

    public LineChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 150));
    }

    // Defines the series (one colour each) and removes all points.
    public void setSeries(Color... colors) {
        series.clear();
        for (Color c : colors) {
            series.add(new Series(c));
        }
        repaint();
    }

    public void setLogY(boolean logY) {
        this.logY = logY;
        repaint();
    }

    public void setAxisTitles(String x, String y) {
        this.xTitle = x;
        this.yTitle = y;
        repaint();
    }

    public void clear() {
        for (Series s : series) {
            s.points.clear();
        }
        repaint();
    }

    public void addPoint(int seriesIndex, double x, double y) {
        series.get(seriesIndex).points.add(new double[]{x, y});
        repaint();
    }

    // Formats a number into a string with appropriate suffixes (k for thousands, M for millions) and decimal precision based on its magnitude.
    static String format(double v) {
        double a = Math.abs(v);
        if (a >= 1_000_000) return String.format("%.1fM", v / 1_000_000);
        if (a >= 10_000) return String.format("%.0fk", v / 1000);
        if (a >= 1000) return String.format("%.1fk", v / 1000);
        if (a >= 100) return String.format("%.0f", v);
        if (a >= 10) return String.format("%.1f", v);
        if (a == Math.rint(a)) return String.format("%.0f", v);
        return String.format("%.2f", v);
    }

    // Like niceCeil but only 1, 2, 5, 10 steps so ticks are whole numbers.
    private static double niceCeilWhole(double v) {
        if (v <= 1) {
            return 1;
        }
        double exp = Math.pow(10, Math.floor(Math.log10(v)));
        for (double m : new double[]{1, 2, 5, 10}) {
            if (m * exp >= v) {
                return m * exp;
            }
        }
        return 10 * exp;
    }

    private static double niceCeil(double v) {
        if (v <= 0) {
            return 1;
        }
        double exp = Math.pow(10, Math.floor(Math.log10(v)));
        for (double m : new double[]{1, 2, 2.5, 5, 10}) {
            if (m * exp >= v) {
                return m * exp;
            }
        }
        return 10 * exp;
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

        double xMax = 1;
        double yMax = 0;
        double yMinPositive = Double.MAX_VALUE;
        for (Series s : series) {
            for (double[] p : s.points) {
                xMax = Math.max(xMax, p[0]);
                yMax = Math.max(yMax, p[1]);
                if (p[1] > 0) {
                    yMinPositive = Math.min(yMinPositive, p[1]);
                }
            }
        }
        xMax = niceCeilWhole(xMax);
        int xTicks = xMax < 5 ? (int) xMax : 5;

        int left = 50;
        int right = 14;
        int top = yTitle.isEmpty() ? 10 : 22;
        int bottom = xTitle.isEmpty() ? 26 : 40;
        int w = getWidth() - left - right;
        int h = getHeight() - top - bottom;
        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        // Y scale
        double lo;
        double hi;
        if (logY) {
            double minExp = yMinPositive == Double.MAX_VALUE
                    ? 0 : Math.floor(Math.log10(yMinPositive));
            double maxExp = yMax <= 1 ? 1 : Math.ceil(Math.log10(yMax));
            lo = Math.min(minExp, maxExp - 1);
            hi = maxExp;
        } else {
            lo = 0;
            hi = niceCeil(yMax);
        }

        // grid + Y labels
        int yTicks = logY ? (int) Math.min(6, hi - lo) : 4;
        yTicks = Math.max(1, yTicks);
        for (int i = 0; i <= yTicks; i++) {
            int y = top + h - h * i / yTicks;
            g2.setColor(Theme.GRID);
            g2.drawLine(left, y, left + w, y);
            double value = logY ? Math.pow(10, lo + (hi - lo) * i / yTicks)
                    : hi * i / yTicks;
            g2.setColor(Theme.MUTED);
            String label = format(value);
            g2.drawString(label, left - 6 - fm.stringWidth(label), y + 4);
        }
        for (int i = 0; i <= xTicks; i++) {
            int x = left + w * i / xTicks;
            g2.setColor(Theme.MUTED);
            String label = format(xMax * i / xTicks);
            g2.drawString(label, x - fm.stringWidth(label) / 2, top + h + 16);
        }
        g2.setColor(Theme.BORDER);
        g2.drawLine(left, top + h, left + w, top + h);
        g2.drawLine(left, top, left, top + h);

        g2.setColor(Theme.MUTED);
        if (!xTitle.isEmpty()) {
            g2.drawString(xTitle, left + (w - fm.stringWidth(xTitle)) / 2,
                    top + h + 34);
        }
        if (!yTitle.isEmpty()) {
            g2.drawString(yTitle, left - 40, top - 8);
        }

        // series
        for (Series s : series) {
            g2.setColor(s.color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            int px = -1;
            int py = -1;
            for (double[] p : s.points) {
                double yv = logY ? Math.log10(Math.max(p[1], Math.pow(10, lo)))
                        : p[1];
                int x = left + (int) (p[0] / xMax * w);
                int y = top + h - (int) ((yv - lo) / (hi - lo) * h);
                if (px >= 0) {
                    g2.drawLine(px, py, x, y);
                }
                px = x;
                py = y;
            }
            g2.setStroke(new BasicStroke(1f));
            for (double[] p : s.points) {
                double yv = logY ? Math.log10(Math.max(p[1], Math.pow(10, lo)))
                        : p[1];
                int x = left + (int) (p[0] / xMax * w);
                int y = top + h - (int) ((yv - lo) / (hi - lo) * h);
                g2.fillOval(x - 3, y - 3, 6, 6);
            }
        }
        g2.dispose();
    }
}
