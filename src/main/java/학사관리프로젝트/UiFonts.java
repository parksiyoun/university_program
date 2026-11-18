package 학사관리프로젝트;

import java.awt.Font;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/** Loads the bundled Nanum Gothic faces and applies them to Swing defaults. */
public final class UiFonts {
    private static final Font REGULAR = load("/fonts/NanumGothic.ttf", Font.PLAIN);
    private static final Font BOLD = load("/fonts/NanumGothicBold.ttf", Font.BOLD);

    private UiFonts() { }

    private static Font load(String resource, int style) {
        try (InputStream input = UiFonts.class.getResourceAsStream(resource)) {
            if (input == null) throw new IOException("Font resource not found: " + resource);
            Font font = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(style, 14f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (IOException | FontFormatException exception) {
            throw new IllegalStateException("나눔고딕 폰트를 불러오지 못했습니다: " + resource, exception);
        }
    }

    public static Font font(int style, int size) {
        Font face = (style & Font.BOLD) != 0 ? BOLD : REGULAR;
        return face.deriveFont(style, (float) size);
    }

    public static void applyDefaults() {
        FontUIResource regular = new FontUIResource(font(Font.PLAIN, 14));
        FontUIResource bold = new FontUIResource(font(Font.BOLD, 14));
        String[] regularKeys = {
                "Button.font", "CheckBox.font", "CheckBoxMenuItem.font", "ColorChooser.font",
                "ComboBox.font", "EditorPane.font", "Label.font", "List.font", "Menu.font",
                "MenuBar.font", "MenuItem.font", "OptionPane.font", "OptionPane.messageFont",
                "OptionPane.buttonFont", "Panel.font", "PasswordField.font", "PopupMenu.font",
                "ProgressBar.font", "RadioButton.font", "RadioButtonMenuItem.font", "ScrollPane.font",
                "Spinner.font", "TabbedPane.font", "Table.font", "TableHeader.font", "TextArea.font",
                "TextField.font", "TextPane.font", "ToggleButton.font", "ToolBar.font", "ToolTip.font",
                "Tree.font", "Viewport.font"
        };
        for (String key : regularKeys) UIManager.put(key, regular);

        Color buttonBackground = new Color(234, 206, 202);
        Color buttonForeground = new Color(105, 100, 107);
        UIManager.put("Button.background", buttonBackground);
        UIManager.put("Button.foreground", buttonForeground);
        UIManager.put("Button.select", buttonBackground.darker());
        UIManager.put("ToggleButton.background", buttonBackground);
        UIManager.put("ToggleButton.foreground", buttonForeground);
        UIManager.put("OptionPane.buttonBackground", buttonBackground);
        UIManager.put("OptionPane.buttonForeground", buttonForeground);
        UIManager.put("TitledBorder.font", bold);
    }
}



