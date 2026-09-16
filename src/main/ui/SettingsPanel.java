package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import model.AnimeList;

// JPanel that represents the tab for settings
public class SettingsPanel extends JPanel {

    private AnimeList activeAnimeList;
    private Runnable refreshCallback;
    // REQUIRES: animeList != null
    // MODIFIES: this
    // EFFECTS: constructs panel object with the given anime list and refresh callback
    public SettingsPanel(AnimeList animeList, Runnable refreshCallback) {
        this.activeAnimeList = animeList;
        this.refreshCallback = refreshCallback;
        initializePanel();
    }

    // MODIFIES: this
    // EFFECTS: configures panel layout and background, adding header to NORTH and scrollable content panel to CENTER
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        add(createHeader(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Theme.BG_DARK);
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        add(scrollPane, BorderLayout.CENTER);
    }

    // EFFECTS: constructs and returns top header panel containing section title
    private JPanel createHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16));
        header.setBackground(Theme.PANEL_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DIM));
        header.add(Theme.makeTitleLabel("Settings"));
        return header;
    }

    // EFFECTS: constructs and returns content container panel holding section label, save/load action row,
    //          separator line, and application about card
    private JPanel createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        JLabel sectionLabel = Theme.makeBodyLabel("Data Management", Theme.TEXT_DIM);
        sectionLabel.setFont(Theme.FONT_SMALL.deriveFont(10f));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sectionLabel);
        content.add(Box.createVerticalStrut(14));

        content.add(createActionRow());
        content.add(Box.createVerticalStrut(32));

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_DIM);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sep);
        content.add(Box.createVerticalStrut(28));

        content.add(createInfoCard());
        content.add(Box.createVerticalGlue());

        return content;
    }

    // EFFECTS: constructs and returns side-by-side action row holding Save and Load action cards
    private JPanel createActionRow() {
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 18, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionRow.add(makeActionCard(
                "[Save]  Save Watchlist",
                    "Changes are saved automatically",
                    Theme.makeRoundedButton("Confirm Saved"),
                    e -> saveWatchList()
        ));
        actionRow.add(makeActionCard(
                "[Load]  Load Watchlist",
                "Reloads current data from the database",
                Theme.makeRoundedButton("Reload Database"),
                e -> loadWatchList()
        ));

        return actionRow;
    }

    // EFFECTS: constructs and returns rounded informational card displaying application title and description
    private JPanel createInfoCard() {
        JPanel infoCard = Theme.makeRoundedPanel(Theme.PANEL_BG, 14);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel aboutTitle = Theme.makeBodyLabel("Creator's Github", Theme.TEXT_LIGHT);
        aboutTitle.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 12f));
        aboutTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel aboutSub = Theme.makeBodyLabel("github.com/david-w06/Anilog", Theme.TEXT_DIM);
        aboutSub.setFont(Theme.FONT_SMALL.deriveFont(10f));
        aboutSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoCard.add(aboutTitle);
        infoCard.add(Box.createVerticalStrut(6));
        infoCard.add(aboutSub);

        return infoCard;
    }

    // REQUIRES: title != null, subtitle != null, button != null, action != null
    // MODIFIES: button
    // EFFECTS: constructs and returns a rounded panel containing title, subtitle, and configured action button
    private JPanel makeActionCard(String title, String subtitle, JButton button, ActionListener action) {
        JPanel card = Theme.makeRoundedPanel(Theme.PANEL_BG, 14);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel textBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        textBlock.setOpaque(false);

        JLabel titleLabel = Theme.makeBodyLabel(title, Color.WHITE);
        titleLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));

        JLabel subLabel = Theme.makeBodyLabel(subtitle, Theme.TEXT_DIM);
        subLabel.setFont(Theme.FONT_SMALL.deriveFont(10f));

        textBlock.add(titleLabel);
        textBlock.add(subLabel);

        button.addActionListener(action);

        card.add(textBlock, BorderLayout.CENTER);
        card.add(button, BorderLayout.SOUTH);

        return card;
    }

    // EFFECTS: confirms that the current watchlist is already persisted in the database
    private void saveWatchList() {
        Theme.showStyledMessage(this,
                "Your watchlist changes are saved automatically.",
                "Save Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    // MODIFIES: this.activeAnimeList
    // EFFECTS: reloads activeAnimeList from the database and refreshes the UI
    private void loadWatchList() {
        try {
            activeAnimeList.reloadFromRepository();
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            Theme.showStyledMessage(this,
                    "Loaded watchlist from the database.",
                    "Load Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            Theme.showStyledMessage(this,
                    "Unable to load watchlist from the database:\n" + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}