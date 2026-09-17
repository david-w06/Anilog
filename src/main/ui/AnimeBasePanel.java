package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Anime;
import model.AnimeList;
import service.AniListService;

// JPanel that represents the tab for anime base
public class AnimeBasePanel extends JPanel {

    private AnimeList activeWatchList;
    private AniListService aniListService;
    private List<Anime> searchResults;
    private Runnable refreshCallback;
    private JPanel gridPanel;

    // REQUIRES: activeWatchList != null
    // MODIFIES: this
    // EFFECTS: constructs panel, populates stock database, and initializes the UI components
    public AnimeBasePanel(AnimeList activeWatchList, Runnable refreshCallback) {
        this.activeWatchList = activeWatchList;
        this.aniListService = new AniListService();
        this.searchResults = new ArrayList<>();
        this.refreshCallback = refreshCallback;

        initializePanel();
    }

    // MODIFIES: this
    // EFFECTS: initializes layout with header, scrollable grid of stock anime cards, and custom scrollbar styling
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(Theme.PANEL_BG);
        header.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Theme.BORDER_DIM));

        header.add(Theme.makeTitleLabel("Anime Base"));

        JTextField searchField = new JTextField(25);
        searchField.setFont(Theme.FONT_BODY);
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(Theme.BG_DARK);

        JButton searchButton = Theme.makeRoundedButton("Search");

        header.add(searchField);
        header.add(searchButton);

        searchButton.addActionListener(e -> {
            String search = searchField.getText().trim();

            if (search.isEmpty()) {
                return;
            }

            performSearch(search);
        });

        searchField.addActionListener(e -> {
            String search = searchField.getText().trim();

            if (!search.isEmpty()) {
                performSearch(search);
            }
        });

        add(header, BorderLayout.NORTH);

        // Card Grid
        gridPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Theme.BG_DARK);
        gridWrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Theme.BG_DARK);
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        add(scrollPane, BorderLayout.CENTER);
    }

    // REQUIRES: anime != null
    // EFFECTS: creates a styled card panel displaying cover art, info details, and an add-to-watchlist action button
    @SuppressWarnings("methodlength")
    private JPanel createAnimeCard(Anime anime) {
        JPanel card = Theme.makeRoundedPanel(Theme.PANEL_BG, 16);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Cover art placeholder
        JPanel coverPlaceholder = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 40, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 42, 122, 60));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int s = 24;
                int[] xs = {cx, cx + s, cx, cx - s};
                int[] ys = {cy - s, cy, cy + s, cy};
                g2.fillPolygon(xs, ys, 4);
                g2.setColor(Theme.ACCENT_PINK);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawPolygon(xs, ys, 4);
                g2.dispose();
            }
        };

        String imageUrl = anime.getCoverImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                URL url = new URL(imageUrl);

                ImageIcon icon = new ImageIcon(url);

                Image scaledImg = icon.getImage().getScaledInstance(
                        144,
                        224,
                        Image.SCALE_SMOOTH
                );

                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setPreferredSize(new Dimension(144, 224));

                card.add(imageLabel, BorderLayout.NORTH);

            } catch (Exception e) {
                coverPlaceholder.setPreferredSize(new Dimension(144, 224));
                coverPlaceholder.setOpaque(false);
                card.add(coverPlaceholder, BorderLayout.NORTH);
            }
        } else {
            coverPlaceholder.setPreferredSize(new Dimension(144, 224));
            coverPlaceholder.setOpaque(false);
            card.add(coverPlaceholder, BorderLayout.NORTH);
        }
        // Info section
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 3));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(anime.getName());
        nameLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
        nameLabel.setForeground(Color.WHITE);

        String yearText = anime.getYear() > 0
                ? String.valueOf(anime.getYear())
                : "Unknown year";

        String episodeText = anime.getLength() > 0
                ? anime.getLength() + " eps"
                : "Unknown episodes";

        JLabel detailsLabel = Theme.makeBodyLabel(
                yearText + "  •  " + episodeText,
                Theme.TEXT_DIM);
        detailsLabel.setFont(Theme.FONT_SMALL.deriveFont(9f));

        String genreText = anime.getGenre().isEmpty()
                ? "No genres"
                : String.join("  •  ", anime.getGenre());

        JLabel genreLabel = Theme.makeBodyLabel(
                genreText,
                Theme.TEXT_DIM);
        genreLabel.setFont(Theme.FONT_SMALL.deriveFont(9f));

        String tagText;

        if (anime.getTags().isEmpty()) {
            tagText = "No tags";
        } else {
            int numberOfTagsToShow = Math.min(3, anime.getTags().size());
            tagText = String.join(
                    "  •  ",
                    anime.getTags().subList(0, numberOfTagsToShow));
        }

        JLabel tagLabel = Theme.makeBodyLabel(
                tagText,
                Theme.TEXT_DIM);
        tagLabel.setFont(Theme.FONT_SMALL.deriveFont(8f));

        infoPanel.add(nameLabel);
        infoPanel.add(detailsLabel);
        infoPanel.add(genreLabel);
        infoPanel.add(tagLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Add button
        JButton addBtn = Theme.makeRoundedButton("+ Add to Watchlist");
        addBtn.setFont(Theme.FONT_SMALL.deriveFont(10f));
        addBtn.addActionListener(e -> {
            Anime copy = new Anime(
                anime.getName(),
                new ArrayList<>(anime.getGenre()),
                anime.getLength(),
                "Plan to Watch",
                "Added from AniList",
                anime.getPriority(),
                anime.getYear()
        );

        copy.setTags(new ArrayList<>(anime.getTags()));
        copy.setCoverImage(anime.getCoverImage());
        copy.setAniListId(anime.getAniListId());

        activeWatchList.addAnime(copy);
            if (refreshCallback != null) {
                refreshCallback.run();
            } 
            Theme.showStyledMessage(this,
                        "'" + anime.getName() + "' added to your WatchList!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
        });
        card.add(addBtn, BorderLayout.SOUTH);

        return card;
    }

    // REQUIRES: bar != null
    // MODIFIES: bar
    // EFFECTS: applies custom UI layout and accent colors to bar
    private void styleScrollBar(JScrollBar bar) {
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = Theme.ACCENT_PINK;
                trackColor = Theme.BG_DARK;
            }

            @Override
            protected JButton createDecreaseButton(int o) {
                return invisible();
            }

            @Override
            protected JButton createIncreaseButton(int o) {
                return invisible();
            }

            private JButton invisible() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
        bar.setBackground(Theme.BG_DARK);
        bar.setPreferredSize(new Dimension(6, 0));
    }

    private void performSearch(String search) {
        try {
            List<Anime> results = aniListService.searchAnime(search);

            searchResults = results;

            gridPanel.removeAll();

            for (Anime anime : searchResults) {
                gridPanel.add(createAnimeCard(anime));
            }

            gridPanel.revalidate();
            gridPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();

            Theme.showStyledMessage(
                    this,
                    "Unable to search AniList.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showAnimeDetails(Anime anime) {
        String genres = String.join(", ", anime.getGenre());

        String tags;

        if (anime.getTags().isEmpty()) {
            tags = "None";
        } else {
            tags = String.join(", ", anime.getTags());
        }

        String message =
                "Title: " + anime.getName()
                + "\nYear: " + anime.getYear()
                + "\nEpisodes: " + anime.getLength()
                + "\n\nGenres:\n" + genres
                + "\n\nTags:\n" + tags;

        Theme.showStyledMessage(
                this,
                message,
                "Anime Details",
                JOptionPane.INFORMATION_MESSAGE);
    }
}