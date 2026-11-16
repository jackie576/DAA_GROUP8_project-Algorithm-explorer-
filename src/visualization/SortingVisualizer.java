// Shows the current array as a set of bars and highlights active work during sort steps.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// these imports are needed for the classes used in this file.
import frontend.Theme;
import java.awt.*;
import javax.swing.JComponent;
import sorting.SortStep;

// This class renders the current array as a set of bars and highlights active work during sort steps.
public class SortingVisualizer extends JComponent {

    private int[] array = new int[0];
    private boolean[] sorted = new boolean[0];
    private int activeA = -1;
    private int activeB = -1;
    private double blip;

    public SortingVisualizer() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 220));
    }

    public void showArray(int[] values) {
        this.array = values.clone();
        this.sorted = new boolean[values.length];
        this.activeA = -1;
        this.activeB = -1;
        this.blip = 0;
        repaint();
    }

    public void showStep(SortStep step) {
        this.array = step.array;
        this.sorted = step.sorted;
        boolean active = step.type != SortStep.Type.DONE;
        this.activeA = active ? step.a : -1;
        this.activeB = active ? step.b : -1;
        repaint();
    }

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

        int n = array.length;
        int w = getWidth();
        int h = getHeight();
        int top = 34;
        int bottom = 28;
        int side = 16;
        int baseY = h - bottom;
        int areaH = baseY - top;

        // faint horizontal grid
        g2.setColor(Theme.GRID);
        for (int i = 0; i <= 4; i++) {
            int y = baseY - areaH * i / 4;
            g2.drawLine(side, y, w - side, y);
        }

        if (n == 0 || areaH <= 0) {
            g2.dispose();
            return;
        }

        int max = 1;
        for (int v : array) {
            max = Math.max(max, v);
        }

        double slot = (w - 2.0 * side) / n;
        int barW = (int) Math.max(4, slot * 0.68);
        int fontSize = n > 20 ? 10 : (n > 14 ? 12 : 14);
        g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < n; i++) {
            boolean active = i == activeA || i == activeB;
            int barH = Math.max(3, (int) ((double) array[i] / max * (areaH - 4)));
            int x = (int) (side + i * slot + (slot - barW) / 2);
            int lift = active ? (int) Math.round(blip * 12) : 0;
            int y = baseY - barH - lift;

            Color color = Theme.BAR_UNSORTED;
            if (sorted[i]) {
                color = Theme.BAR_SORTED;
            }
            if (active) {
                color = Theme.BAR_COMPARING;
            }

            g2.setColor(color);
            g2.fillRect(x, y, barW, barH + lift);

            g2.setColor(Theme.TEXT);
            String value = String.valueOf(array[i]);
            g2.drawString(value, x + (barW - fm.stringWidth(value)) / 2, y - 6);

            g2.setColor(Theme.MUTED);
            String index = String.valueOf(i);
            g2.drawString(index, x + (barW - fm.stringWidth(index)) / 2,
                    baseY + fm.getAscent() + 6);
        }

        g2.setColor(Theme.BORDER);
        g2.drawLine(side, baseY, w - side, baseY);
        g2.dispose();
    }
}
