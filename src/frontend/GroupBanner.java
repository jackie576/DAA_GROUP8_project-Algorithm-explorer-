// Creates the banner elements used to organize section headers in the UI.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import javax.swing.JComponent;

// Static bar at the bottom of the window: GROUP 8 in large letters, then the
public class GroupBanner extends JComponent {

    private static final String TITLE = "GROUP 8"; //group name and number
    private static final String[] NAMES = {  // names of group members
        "OTHIENO DAVID - M25B23/054", "GHAI SAMUEL MACH - M25B23/057", "KYEMBA MOHAMMED - M25B23/026",
        "TAYEBWA PEDSON - M25B23/024", "SEMWANGA ROBERT - M25B23/034", "NAKIRE JACKLINE - M25B23/046"
    };

    // class attributes for the banner's appearance.
    private static final Color INK = new Color(16, 24, 76);   // dark midnight blue
    private static final float TITLE_SIZE = 36f;
    private static final float NAME_SIZE = 20f;
    private static final int TITLE_GAP = 70;   // after GROUP 8
    private static final int NAME_GAP = 30;    // between names

    // class constructor that initializes the group banner with a preferred size and background color.
    public GroupBanner() {
        setPreferredSize(new Dimension(100, 62));
        setOpaque(true);
        setBackground(Theme.SURFACE);
    }

    // class that paints the group banner with the title and names, scaling text to fit if necessary.
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Theme.BORDER);
        g2.drawLine(0, 0, getWidth(), 0);

        // measure at full size, then scale everything down if it does not fit
        double scale = 1;
        double width = contentWidth(g2, 1);
        double available = getWidth() - 48;
        if (width > available) {
            scale = available / width;
        }

        Font titleFont = Theme.marqueeFont((float) (TITLE_SIZE * scale));
        Font nameFont = Theme.marqueeFont((float) (NAME_SIZE * scale));
        FontMetrics titleMetrics = g2.getFontMetrics(titleFont);
        FontMetrics nameMetrics = g2.getFontMetrics(nameFont);

        double x = (getWidth() - contentWidth(g2, scale)) / 2;
        double mid = getHeight() / 2.0;

        g2.setColor(INK);
        g2.setFont(titleFont);
        g2.drawString(TITLE, (float) x,
                (float) (mid + (titleMetrics.getAscent() - titleMetrics.getDescent()) / 2.0));
        x += titleMetrics.stringWidth(TITLE) + TITLE_GAP * scale;

        g2.setFont(nameFont);
        for (String name : NAMES) {
            g2.drawString(name, (float) x,
                    (float) (mid + (nameMetrics.getAscent() - nameMetrics.getDescent()) / 2.0));
            x += nameMetrics.stringWidth(name) + NAME_GAP * scale;
        }
        g2.dispose();
    }

    // class that calculates the total width of the banner content based on the current scale and font metrics.
    private double contentWidth(Graphics2D g2, double scale) {
        FontMetrics titleMetrics = g2.getFontMetrics(
                Theme.marqueeFont((float) (TITLE_SIZE * scale)));
        FontMetrics nameMetrics = g2.getFontMetrics(
                Theme.marqueeFont((float) (NAME_SIZE * scale)));
        double width = titleMetrics.stringWidth(TITLE) + TITLE_GAP * scale;
        for (int i = 0; i < NAMES.length; i++) {
            width += nameMetrics.stringWidth(NAMES[i]);
            if (i < NAMES.length - 1) {
                width += NAME_GAP * scale;
            }
        }
        return width;
    }
}
