// Draws a compact gauge view for performance or structure metrics.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import java.awt.geom.Arc2D;
import javax.swing.JComponent;

// this is the class that draws a compact gauge view for performance or structure metrics.
public class GaugePanel extends JComponent {

    // class attributes.    
    private final Color color;
    private String label;
    private String text = "0";
    private double fraction;

    // class constructor that initializes the gauge panel with a label and color.
    public GaugePanel(String label, Color color) {
        this.label = label;
        this.color = color;
        setOpaque(false);
        setPreferredSize(new Dimension(150, 150));
    }

    // class that sets the label of the gauge panel and repaints it.
    public void setLabel(String label) {
        this.label = label;
        repaint();
    }

    // class that sets the value and text of the gauge panel and repaints it.
    public void setValue(double fraction, String text) {
        this.fraction = Math.max(0, Math.min(1, fraction));
        this.text = text;
        repaint();
    }

    // class that paints the gauge panel with the current value, label, and progress arc.
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 4;
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0;
        double outer = size / 2.0;

        g2.setColor(Theme.SURFACE);
        g2.fillOval((int) (cx - outer), (int) (cy - outer), size, size);
        g2.setColor(Theme.BORDER);
        g2.drawOval((int) (cx - outer), (int) (cy - outer), size, size);

        // tick marks
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(190, 198, 204));
        for (int k = 0; k < 60; k++) {
            double angle = Math.toRadians(k * 6);
            double r1 = outer - 4;
            double r2 = outer - (k % 5 == 0 ? 11 : 8);
            g2.drawLine((int) (cx + Math.cos(angle) * r1),
                    (int) (cy + Math.sin(angle) * r1),
                    (int) (cx + Math.cos(angle) * r2),
                    (int) (cy + Math.sin(angle) * r2));
        }

        // ring track and progress arc
        double ring = outer - 22;
        g2.setStroke(new BasicStroke(9f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));
        g2.setColor(new Color(228, 234, 238));
        g2.draw(new Arc2D.Double(cx - ring, cy - ring, ring * 2, ring * 2,
                0, 360, Arc2D.OPEN));
        g2.setColor(color);
        g2.draw(new Arc2D.Double(cx - ring, cy - ring, ring * 2, ring * 2,
                90, -360 * fraction, Arc2D.OPEN));

        // centre text
        g2.setColor(Theme.TEXT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (int) (cx - fm.stringWidth(text) / 2.0),
                (int) (cy + 2));

        g2.setColor(Theme.MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fm = g2.getFontMetrics();
        g2.drawString(label, (int) (cx - fm.stringWidth(label) / 2.0),
                (int) (cy + 20));
        g2.dispose();
    }
}
