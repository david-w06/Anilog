package ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import java.io.InputStream;

// Contains method for making themed GUI elemenet  
public class Theme {

    // Color Palette
    public static final Color BG_DARK         = Color.decode("#121212");
    public static final Color BG_SIDEBAR      = Color.decode("#0D0D0D");
    public static final Color ACCENT_PINK     = Color.decode("#FF2A7A");
    public static final Color ACCENT_PINK_DIM = Color.decode("#99183E");
    public static final Color TEXT_LIGHT      = Color.decode("#E0E0E0");
    public static final Color TEXT_DIM        = Color.decode("#888888");
    public static final Color PANEL_BG        = Color.decode("#1E1E1E");
    public static final Color PANEL_BG2       = Color.decode("#252525");
    public static final Color BORDER_DIM      = Color.decode("#2A2A2A");

    // Fonts
    public static final Font FONT_TITLE;
    public static final Font FONT_BODY;
    public static final Font FONT_SMALL;
    public static final Font FONT_MONO;

    static {
        Font pressStart = loadFont("/fonts/PressStart2P-Regular.ttf", 11f);
        Font orbitron   = loadFont("/fonts/Orbitron-Regular.ttf", 12f);

        FONT_TITLE = pressStart != null ? pressStart : new Font("Monospaced", Font.BOLD, 11);
        FONT_BODY  = orbitron   != null ? orbitron   : new Font("Monospaced", Font.PLAIN, 12);
        FONT_SMALL = FONT_BODY.deriveFont(10f);
        FONT_MONO  = new Font("Monospaced", Font.PLAIN, 12);
    }

    // REQUIRES: resourcePath != null, size > 0
    // EFFECTS: load and register a TrueType font from resourcePath
    private static Font loadFont(String resourcePath, float size) {
        try (InputStream is = Theme.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return null;
            } 
            Font f = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    // REQUIRES: text != null
    // EFFECTS: creates and returns a styled JButton with custom vector-painted rounded corners, 
    //          supporting filled pink style or transparent outline style with hover/press styles
    @SuppressWarnings("methodlength")
    public static JButton makeRoundedButton(String text, boolean outline) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 20;
                if (outline) {
                    g2.setColor(getModel().isPressed()
                            ? ACCENT_PINK_DIM
                            : getModel().isRollover()
                                    ? new Color(255, 42, 122, 40)
                                    : new Color(0, 0, 0, 0));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                    g2.setColor(ACCENT_PINK);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, arc, arc));
                } else {
                    g2.setColor(getModel().isPressed()
                            ? ACCENT_PINK_DIM
                            : getModel().isRollover()
                                    ? ACCENT_PINK.brighter()
                                    : ACCENT_PINK);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(FONT_BODY.deriveFont(11f));
        btn.setForeground(outline ? ACCENT_PINK : Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    // REQUIRES: text != null
    // EFFECTS: creates and returns a filled pink rounded JButton.
    public static JButton makeRoundedButton(String text) {
        return makeRoundedButton(text, false);
    }

    // REQUIRES: bg != null, arc >= 0
    // EFFECTS: returns a transparent JPanel that paints a filled rounded background
    public static JPanel makeRoundedPanel(Color bg, int arc) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }


    // REQUIRES: text != null
    // EFFECTS: returns a JLabel styled with the title font and accent pink text color.
    public static JLabel makeTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE.deriveFont(13f));
        lbl.setForeground(ACCENT_PINK);
        return lbl;
    }

    // REQUIRES: text != null, color != null
    // EFFECTS: returns a JLabel styled with the body font and specified text color
    public static JLabel makeBodyLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(color);
        return lbl;
    }
    
    // REQUIRES: field != null
    // EFFECTS: construct a themed text field for dialog
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_LIGHT);
        field.setBackground(PANEL_BG2);
        field.setCaretColor(ACCENT_PINK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DIM),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
        // REQUIRES: comboBox != null
    // EFFECTS: construct a themed combo box for dialog
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_BODY);
        comboBox.setForeground(TEXT_LIGHT);
        comboBox.setBackground(PANEL_BG2);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_DIM));
    }

    // REQUIRES: label != null
    // EFFECTS: construct a themed label for dialog
    public static void styleDialogLabel(JLabel label) {
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_LIGHT);
    }

    // REQUIRES: container != null
    // EFFECTS: construct themed buttons for dialog
    public static void styleDialogButtons(Container container) {
        for (Component component : container.getComponents()) {

            if (component instanceof JButton) {
                JButton button = (JButton) component;

                button.setFont(FONT_BODY.deriveFont(11f));
                button.setForeground(Color.WHITE);
                button.setBackground(ACCENT_PINK);

                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(true);
                button.setOpaque(true);

                button.setBorder(
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                );
            }

            if (component instanceof Container) {
                styleDialogButtons((Container) component);
            }
        }
    }

    // REQUIRES: container != null
    // EFFECTS: recursively set all JPanels in the target container to dark theme leaving out buttons
    public static void styleDialogBackground(Container container) {
        if (!(container instanceof JButton)) {
            container.setBackground(BG_DARK);
        }

        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                styleDialogBackground((Container) component);
            }
        }
    }
}