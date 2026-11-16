package frontend;

import java.awt.*;
import javax.swing.JComponent;

/**
 * Static bar at the bottom of the window: GROUP 8 in large letters, then the
 * member names with tighter spacing. Text shrinks to fit narrow windows.
 */
public class GroupBanner extends JComponent {

    private static final String TITLE = "GROUP 8";
    private static final String[] NAMES = {
        "OTHIENO DAVID", "GHAI SAMUEL MACH", "KYEMBA MOHAMMED",
        "TAYEBWA PEDSON", "SEMWANGA ROBERT", "NAKIRE JACKLINE"
    };

    private static final Color INK = new Color(16, 24, 76);   // dark midnight blue
    private static final float TITLE_SIZE = 36f;
    private static final float NAME_SIZE = 20f;
    private static final int TITLE_GAP = 70;   // after GROUP 8
    private static final int NAME_GAP = 36;    // between names

    public GroupBanner() {
        setPreferredSize(new Dimension(100, 62));
        setOpaque(true);
        setBackground(Theme.SURFACE);
    }

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
