package 학사관리프로젝트;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTable;

/** Fits older screens with fixed coordinates to the current menu window size. */
final class ResponsiveContent extends JPanel {
    private final Container screenContent;
    private final int designWidth;
    private final int designHeight;
    private final Map<Component, Rectangle> originalBounds = new IdentityHashMap<>();
    private final Map<Component, Font> originalFonts = new IdentityHashMap<>();
    private final Map<JTable, Integer> originalRowHeights = new IdentityHashMap<>();
    private int lastWidth = -1;
    private int lastHeight = -1;

    ResponsiveContent(Container screenContent, int designWidth, int designHeight) {
        super(new BorderLayout());
        this.screenContent = screenContent;
        this.designWidth = Math.max(1, designWidth);
        this.designHeight = Math.max(1, designHeight);
        rememberComponents(screenContent);
        add(screenContent, BorderLayout.CENTER);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (getWidth() == lastWidth && getHeight() == lastHeight) return;
        lastWidth = getWidth();
        lastHeight = getHeight();
        double widthScale = getWidth() / (double) designWidth;
        double heightScale = getHeight() / (double) designHeight;
        scaleFixedPositions(screenContent, widthScale, heightScale);
        float fontScale = (float) Math.min(widthScale, heightScale);
        for (Map.Entry<Component, Font> entry : originalFonts.entrySet()) {
            Font original = entry.getValue();
            entry.getKey().setFont(original.deriveFont(Math.max(1f, original.getSize2D() * fontScale)));
        }
        for (Map.Entry<JTable, Integer> entry : originalRowHeights.entrySet()) {
            entry.getKey().setRowHeight(Math.max(1, (int) Math.round(entry.getValue() * heightScale)));
        }
    }

    private void rememberComponents(Component component) {
        Font font = component.getFont();
        if (font != null) originalFonts.put(component, font);
        if (component instanceof JTable) {
            JTable table = (JTable) component;
            originalRowHeights.put(table, table.getRowHeight());
        }
        if (!(component instanceof Container)) return;
        Container container = (Container) component;
        for (Component child : container.getComponents()) {
            if (container.getLayout() == null) originalBounds.put(child, child.getBounds());
            rememberComponents(child);
        }
    }

    private void scaleFixedPositions(Container container, double widthScale, double heightScale) {
        for (Component child : container.getComponents()) {
            if (container.getLayout() == null) {
                Rectangle original = originalBounds.get(child);
                if (original != null) {
                    child.setBounds(
                            (int) Math.round(original.x * widthScale),
                            (int) Math.round(original.y * heightScale),
                            (int) Math.round(original.width * widthScale),
                            (int) Math.round(original.height * heightScale));
                }
            }
            if (child instanceof Container) {
                scaleFixedPositions((Container) child, widthScale, heightScale);
            }
        }
    }
}
