package ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

// JPanel that represents the navigation bar
public class NavigationPanel extends JPanel {

    private Consumer<String> onTabSelected;
    private JButton activeButton = null;

    // REQUIRES: onTabSelected != null
    // MODIFIES: this
    // EFFECTS: constructs a NavigationPanel object with the given callback handler for tab selection
    //          and initializes all UI layout components.
    public NavigationPanel(Consumer<String> onTabSelected) {
        this.onTabSelected = onTabSelected;
        initializePanel();
    }

    @Override
    // REQUIRES: g != null
    // MODIFIES: g
    // EFFECTS: paints the panel background and draws a 1-pixel wide right-edge separator line.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Theme.BORDER_DIM);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g2.dispose();
    }

    // MODIFIES: this
    // EFFECTS: configures panel properties and delegates header, tab buttons, and footer creation.
    private void initializePanel() {
        setPreferredSize(new Dimension(140, 0));
        setBackground(Theme.BG_SIDEBAR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addHeader();
        JButton defaultBtn = addNavButtons();
        addFooterButtons();

        setActiveButton(defaultBtn);
    }

    // MODIFIES: this
    // EFFECTS: creates and adds app title label and decorative accent line to panel.
    private void addHeader() {
        JLabel appTitle = new JLabel("AniLog");
        appTitle.setFont(Theme.FONT_TITLE.deriveFont(14f));
        appTitle.setForeground(Theme.ACCENT_PINK);
        appTitle.setHorizontalAlignment(SwingConstants.CENTER);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitle.setBorder(BorderFactory.createEmptyBorder(24, 0, 16, 0));
        add(appTitle);

        JPanel accent = new JPanel();
        accent.setMaximumSize(new Dimension(80, 2));
        accent.setPreferredSize(new Dimension(80, 2));
        accent.setBackground(Theme.ACCENT_PINK);
        accent.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(accent);
        add(Box.createVerticalStrut(20));
    }

    // MODIFIES: this
    // EFFECTS: constructs and adds all main navigation tab buttons; returns default Watchlist button.
    private JButton addNavButtons() {
        JButton watchlistBtn = createNavButton("Watchlist", "WATCHLIST");
        add(watchlistBtn);
        add(Box.createVerticalStrut(6));
        add(createNavButton("Recommend", "RECOMMENDATIONS"));
        add(Box.createVerticalStrut(6));
        add(createNavButton("Statistics", "STATISTICS"));
        add(Box.createVerticalStrut(6));
        add(createNavButton("Anime Base", "ANIME_BASE"));
        add(Box.createVerticalStrut(6));
        add(createNavButton("Settings", "SETTINGS"));
        return watchlistBtn;
    }

    // MODIFIES: this
    // EFFECTS: creates and adds minimize and quit utility buttons to bottom of panel.
    private void addFooterButtons() {
        add(Box.createVerticalGlue());

        JButton minimizeBtn = createNavButton("Minimize", null);
        minimizeBtn.setForeground(Theme.TEXT_DIM);
        minimizeBtn.addActionListener(e -> handleMinimize());
        add(minimizeBtn);
        add(Box.createVerticalStrut(6));

        JButton quitBtn = createNavButton("Quit", null);
        quitBtn.setForeground(Theme.TEXT_DIM);
        quitBtn.addActionListener(e -> handleQuit());
        add(quitBtn);
        add(Box.createVerticalStrut(16));
    }

    // MODIFIES: this
    // EFFECTS: minimizes parent window frame if found.
    private void handleMinimize() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            ((Frame) window).setState(Frame.ICONIFIED);
        }
    }

    // MODIFIES: this
    // EFFECTS: dispatches window closing event to ancestor window, or exits system if window is null.
    private void handleQuit() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispatchEvent(new java.awt.event.WindowEvent(window, java.awt.event.WindowEvent.WINDOW_CLOSING));
        } else {
            System.exit(0);
        }
    }

    // REQUIRES: label != null
    // EFFECTS: returns a styled navigation button; attaches action listener for tab selection if tabKey is non-null.
    private JButton createNavButton(String label, String tabKey) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                paintButtonBackground(g, this);
                super.paintComponent(g);
            }
        };

        applyButtonStyle(btn);

        if (tabKey != null) {
            btn.addActionListener(e -> {
                setActiveButton(btn);
                onTabSelected.accept(tabKey);
            });
        }
        return btn;
    }

    // REQUIRES: g != null, btn != null
    // MODIFIES: g
    // EFFECTS: draws rounded highlighted background if button is active or hovered.
    private void paintButtonBackground(Graphics g, JButton btn) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (btn == activeButton) {
            g2.setColor(Theme.ACCENT_PINK);
            g2.fillRoundRect(6, 2, btn.getWidth() - 12, btn.getHeight() - 4, 14, 14);
        } else if (btn.getModel().isRollover()) {
            g2.setColor(new Color(255, 42, 122, 30));
            g2.fillRoundRect(6, 2, btn.getWidth() - 12, btn.getHeight() - 4, 14, 14);
        }
        g2.dispose();
    }

    // REQUIRES: btn != null
    // MODIFIES: btn
    // EFFECTS: applies standard typography, borders, dimensions, and cursor styles to button.
    private void applyButtonStyle(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(Theme.FONT_BODY.deriveFont(11f));
        btn.setForeground(Theme.TEXT_LIGHT);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(128, 40));
    }

    // MODIFIES: this, btn, this.activeButton
    // EFFECTS: updates activeButton reference, resetting former active button styling and highlighting new button.
    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setForeground(Theme.TEXT_LIGHT);
            activeButton.repaint();
        }
        activeButton = btn;
        if (activeButton != null) {
            activeButton.setForeground(Color.WHITE);
            activeButton.repaint();
        }
    }
}