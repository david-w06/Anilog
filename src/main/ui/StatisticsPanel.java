package ui;

import javax.swing.*;
import java.awt.*;
import model.AnimeList;

public class StatisticsPanel extends JPanel {

    private AnimeList animeList;

    private JLabel totalEntriesValue;
    private JLabel watchTimeValue;
    private JLabel favGenreValue;
    private JLabel completedValue;

    // REQUIRES: animeList != null
    // MODIFIES: this
    // EFFECTS: constructs panel object bound to the provided anime list and initializes UI components
    public StatisticsPanel(AnimeList animeList) {
        this.animeList = animeList;
        initializePanel();
    }

    // MODIFIES: this
    // EFFECTS: builds the page header, 2x2 stat card grid, and initializes metric values
    @SuppressWarnings("methodlength")
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16));
        header.setBackground(Theme.PANEL_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DIM));
        header.add(Theme.makeTitleLabel("Statistics"));
        add(header, BorderLayout.NORTH);

        // Grid
        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.setBackground(Theme.BG_DARK);

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        totalEntriesValue = new JLabel("0");
        watchTimeValue    = new JLabel("0");
        favGenreValue     = new JLabel("N/A");
        completedValue    = new JLabel("0");

        grid.add(makeStatCard("Total Entries", totalEntriesValue, "#"));
        grid.add(makeStatCard("Watch Hours",   watchTimeValue,    "~"));
        grid.add(makeStatCard("Fav. Genre",    favGenreValue,     "*"));
        grid.add(makeStatCard("Completed",    completedValue,    "v"));

        gridWrapper.add(grid);
        add(gridWrapper, BorderLayout.CENTER);

        updateStatistics();
    }

    // REQUIRES: animeList != null
    // MODIFIES: this
    // EFFECTS: queries animeList for updated metrics and refreshes all metric label display texts
    public void updateStatistics() {
        int total           = animeList.getAnimes().size();
        int watchTimeHours  = animeList.calculateTotalWatchTime();
        String favGenre     = animeList.calculateFavoriteGenre();
        int completed       = animeList.filterByStatus("Completed").size();

        totalEntriesValue.setText(String.valueOf(total));
        watchTimeValue.setText("~" + watchTimeHours + "h");
        favGenreValue.setText(favGenre != null ? favGenre : "N/A");
        completedValue.setText(String.valueOf(completed));
    }

    // REQUIRES: title != null, valueLabel != null, icon != null
    // MODIFIES: this
    // EFFECTS: constructs a rounded panel containing the card title, icon, value display, and accent border
    @SuppressWarnings("methodlength")
    private JPanel makeStatCard(String title, JLabel valueLabel, String icon) {
        JPanel card = Theme.makeRoundedPanel(Theme.PANEL_BG, 18);
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Icon + title row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel titleLabel = Theme.makeBodyLabel(title, Theme.TEXT_DIM);
        titleLabel.setFont(Theme.FONT_SMALL.deriveFont(10f));
        titleRow.add(iconLabel);
        titleRow.add(titleLabel);

        // Value label
        valueLabel.setFont(Theme.FONT_TITLE.deriveFont(22f));
        valueLabel.setForeground(Theme.ACCENT_PINK);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Pink bottom accent line
        JPanel accentLine = new JPanel();
        accentLine.setPreferredSize(new Dimension(0, 2));
        accentLine.setBackground(Theme.ACCENT_PINK);
        accentLine.setOpaque(true);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(accentLine, BorderLayout.SOUTH);

        return card;
    }
}