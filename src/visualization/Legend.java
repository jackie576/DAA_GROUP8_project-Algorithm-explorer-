// Renders a small legend that labels the colors used in charts and visualizations.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package visualization;

// this is the class that renders a small legend that labels the colors used in charts and visualizations.
import frontend.Theme;
import java.awt.*;
import javax.swing.JComponent;

// This class renders a small legend that labels the colors used in charts and visualizations.
public class Legend extends JComponent {

    private final String[] names;
    private final Color[] colors;

    public Legend(String[] names, Color[] colors) {
        this.names = names;
        this.colors = colors;
        setPreferredSize(new Dimension(names.length * 100, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(Theme.SMALL_FONT);
        FontMetrics fm = g2.getFontMetrics();

        int total = 0;
        for (String n : names) {
            total += 18 + fm.stringWidth(n) + 14;
        }
        int x = getWidth() - total;
        int mid = getHeight() / 2;
        for (int i = 0; i < names.length; i++) {
            g2.setColor(colors[i]);
            g2.fillRect(x, mid - 5, 10, 10);
            g2.setColor(Theme.TEXT);
            g2.drawString(names[i], x + 16, mid + 4);
            x += 18 + fm.stringWidth(names[i]) + 14;
        }
        g2.dispose();
    }
}
