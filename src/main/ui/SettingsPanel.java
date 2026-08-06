package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import model.Anime;
import model.AnimeList;
import persistence.JsonReader;
import persistence.JsonWriter;

// JPanel that represents the tab for settings
public class SettingsPanel extends JPanel {

    private AnimeList activeAnimeList;
    private Runnable refreshCallback;
    private static final String JSON_STORE = "./data/animeList.json";

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
                    "Writes current data to disk",
                    Theme.makeRoundedButton("Save to File"),
                    e -> saveWatchList()
        ));
        actionRow.add(makeActionCard(
                "[Load]  Load Watchlist",
                "Replaces current data from disk",
                Theme.makeRoundedButton("Load from File"),
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

        JLabel aboutTitle = Theme.makeBodyLabel("AniLog — Anime Watchlist Tracker", Theme.TEXT_LIGHT);
        aboutTitle.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 12f));
        aboutTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel aboutSub = Theme.makeBodyLabel("Manage, rate, and discover your anime collection.", Theme.TEXT_DIM);
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

    // MODIFIES: disk file at JSON_STORE
    // EFFECTS: saves activeAnimeList data to JSON_STORE on disk and shows a confirmation message
    private void saveWatchList() {
        try {
            File file = new File(JSON_STORE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            } 

            JsonWriter writer = new JsonWriter(JSON_STORE);
            writer.open();
            writer.write(activeAnimeList);
            writer.close();

            JOptionPane.showMessageDialog(this,
                    "Saved watchlist to " + JSON_STORE, "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to write to file: " + JSON_STORE + "\n" + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this.activeAnimeList, disk file
    // EFFECTS: reads anime list from JSON_STORE, updates activeAnimeList with loaded items, runs refreshCallback
    //          if non-null, and shows a success dialog; displays error dialog if reading fails
    private void loadWatchList() {
        try {
            JsonReader reader = new JsonReader(JSON_STORE);
            AnimeList loadedList = reader.read();

            activeAnimeList.getAnimes().clear();
            for (Anime anime : loadedList.getAnimes()) {
                activeAnimeList.addAnime(anime);
            }

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            JOptionPane.showMessageDialog(this,
                        "Loaded watchlist from " + JSON_STORE, "Load Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                        "Unable to read from file: " + JSON_STORE, "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}