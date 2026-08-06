package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Anime;
import model.AnimeList;

// JPanel that represents the tab for anime base
public class AnimeBasePanel extends JPanel {

    private AnimeList activeWatchList;
    private List<Anime> stockLibrary;
    private Runnable refreshCallback;

    // REQUIRES: activeWatchList != null
    // MODIFIES: this
    // EFFECTS: constructs panel, populates stock database, and initializes the UI components
    public AnimeBasePanel(AnimeList activeWatchList, Runnable refreshCallback) {
        this.activeWatchList = activeWatchList;
        this.stockLibrary = new ArrayList<>();
        this.refreshCallback = refreshCallback;
        populateStockDatabase();
        initializePanel();
    }

    // MODIFIES: this
    // EFFECTS: populates stockLibrary with default stock anime items
    private void populateStockDatabase() {
        stockLibrary.add(new Anime("Fullmetal Alchemist: Brotherhood",
                Arrays.asList("Action", "Adventure", "Fantasy"), 64, 1, "Not Watched", "Stock catalog item", 1));
        stockLibrary.add(new Anime("Attack on Titan",
                Arrays.asList("Action", "Drama", "Mystery"), 87, 4, "Not Watched", "Stock catalog item", 2));
        stockLibrary.add(new Anime("Demon Slayer",
                Arrays.asList("Action", "Supernatural"), 55, 3, "Not Watched", "Stock catalog item", 2));
        stockLibrary.add(new Anime("Spirited Away",
                Arrays.asList("Animation", "Adventure", "Supernatural"), 1, 1, "Not Watched", "Movie", 3));
        stockLibrary.add(new Anime("Jujutsu Kaisen",
                Arrays.asList("Action", "Fantasy"), 47, 2, "Not Watched", "Stock catalog item", 1));
        stockLibrary.add(new Anime("Steins Gate",
                Arrays.asList("Sci-Fi", "Thriller"), 24, 1, "Not Watched", "Stock catalog item", 2));
    }

    // MODIFIES: this
    // EFFECTS: initializes layout with header, scrollable grid of stock anime cards, and custom scrollbar styling
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16));
        header.setBackground(Theme.PANEL_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DIM));
        header.add(Theme.makeTitleLabel("Anime Base"));
        add(header, BorderLayout.NORTH);

        // Card Grid
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        for (Anime anime : stockLibrary) {
            gridPanel.add(createStockCard(anime));
        }

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
    private JPanel createStockCard(Anime anime) {
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

        String imagePath = "/resources/images/" + anime.getName().toLowerCase().replace(" ", "_") + ".jpg";
        java.net.URL imgURL = getClass().getResource(imagePath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaledImg = icon.getImage().getScaledInstance(144, 224, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(144, 224));
            card.add(imageLabel, BorderLayout.NORTH);
        } else {
            coverPlaceholder.setPreferredSize(new Dimension(144, 224));
            coverPlaceholder.setOpaque(false);
            card.add(coverPlaceholder, BorderLayout.NORTH);
        }

        // Info section
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(anime.getName());
        nameLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
        nameLabel.setForeground(Color.WHITE);

        JLabel genreLabel = new JLabel(String.join("  •  ", anime.getGenre()));
        genreLabel.setFont(Theme.FONT_SMALL.deriveFont(9f));
        genreLabel.setForeground(Theme.TEXT_DIM);

        JLabel episodeLabel = Theme.makeBodyLabel(anime.getLength() 
                + " eps  ·  " + anime.getSeasons() + " season(s)", Theme.TEXT_DIM);
        episodeLabel.setFont(Theme.FONT_SMALL.deriveFont(9f));

        infoPanel.add(nameLabel);
        infoPanel.add(genreLabel);
        infoPanel.add(episodeLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Add button
        JButton addBtn = Theme.makeRoundedButton("+ Add to Watchlist");
        addBtn.setFont(Theme.FONT_SMALL.deriveFont(10f));
        addBtn.addActionListener(e -> {
            Anime copy = new Anime(anime.getName(), new ArrayList<>(anime.getGenre()),
                    anime.getLength(), anime.getSeasons(),
                    "Plan to Watch", "Added from Stock Base", anime.getPriority());
            activeWatchList.addAnime(copy);
            if (refreshCallback != null) {
                refreshCallback.run();
            } 
            JOptionPane.showMessageDialog(this,
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
}