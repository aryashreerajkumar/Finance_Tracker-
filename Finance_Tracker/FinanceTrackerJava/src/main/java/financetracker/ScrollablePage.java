package financetracker;

import javax.swing.*;
import java.awt.*;

/** Vertical page content that reports its full height to a scroll pane. */
public class ScrollablePage extends JPanel implements Scrollable {
    public ScrollablePage() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 24; }
    @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return visibleRect.height; }
}
