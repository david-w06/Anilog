package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Anime;
import model.AnimeList;

// JPanel that represents the tab for recommendation
public class RecommendationPanel extends JPanel {

    private AnimeList animeList;
    private JPanel recommendationsContainer;

    // REQUIRES: animeList != null
    // MODIFIES: this
    // EFFECTS: constructs a RecommendationPanel object with the given anime list and initializes UI components
    public RecommendationPanel(AnimeList animeList) {
        this.animeList = animeList;
        initializePanel();
    }

    // MODIFIES: this
    // EFFECTS: configures layout and builds header and scrollable recommendation list containers
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        add(createHeader(), BorderLayout.NORTH);

        recommendationsContainer = new JPanel();
        recommendationsContainer.setLayout(new BoxLayout(recommendationsContainer, BoxLayout.Y_AXIS));
        recommendationsContainer.setBackground(Theme.BG_DARK);
        recommendationsContainer.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(createScrollPane(), BorderLayout.CENTER);

        generateRecommendations();
    }

    // MODIFIES: this
    // EFFECTS: constructs and returns top header panel containing section title and refresh button
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PANEL_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DIM),
                    BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel titleLabel = Theme.makeTitleLabel("Recommendations");
        JButton refreshBtn = Theme.makeRoundedButton(">> Refresh Recs");
        refreshBtn.addActionListener(e -> generateRecommendations());

        header.add(titleLabel, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        return header;
    }

    // MODIFIES: this
    // EFFECTS: constructs and returns JScrollPane wrapping recommendationsContainer with custom dark scrollbar
    private JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(recommendationsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Theme.BG_DARK);
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        return scrollPane;
    }

    // MODIFIES: this, recommendationsContainer
    // EFFECTS: clears current content and populates recommendationsContainer with top anime matches based on
    //          favorite genre and priority ranking.
    public void generateRecommendations() {
        recommendationsContainer.removeAll();
        String favGenre = animeList.calculateFavoriteGenre();

        List<Anime> topRecs = animeList.getTopRecommendations(favGenre);

        if (favGenre != null) {
            addGenreBadge(favGenre);
        } else {
            addEmptyState("Add and rate anime in your Watchlist to refine personalized recommendations!");
            recommendationsContainer.add(Box.createVerticalStrut(14));
        }

        if (!topRecs.isEmpty()) {
            addRecommendationList(topRecs, favGenre);
        } else {
            addEmptyState("No unwatched anime in your current watchlist. Explore recommendations below!");
            recommendationsContainer.add(Box.createVerticalStrut(14));
        }

        addDiscoverySection();

        revalidate();
        repaint();
    }

    private void addDiscoverySection() {
        JLabel discoveryTitle = Theme.makeBodyLabel("Explore Popular Catalog Items", Color.WHITE);
        discoveryTitle.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 14f));
        recommendationsContainer.add(discoveryTitle);
        recommendationsContainer.add(Box.createVerticalStrut(10));

        List<Anime> catalog = getCatalogItems();
        for (Anime item : catalog) {
            if (!isInWatchList(item.getName())) {
                recommendationsContainer.add(makeCatalogCard(item));
                recommendationsContainer.add(Box.createVerticalStrut(10));
            }
        }
    }

    private boolean isInWatchList(String name) {
        for (Anime a : animeList.getAnimes()) {
            if (a.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private List<Anime> getCatalogItems() {
        List<Anime> catalog = new ArrayList<>();
        catalog.add(new Anime("Fullmetal Alchemist: Brotherhood", Arrays.asList("Action", "Adventure", "Fantasy"), 64, "Plan to Watch", "Catalog", 1, 2009));
        catalog.add(new Anime("Attack on Titan", Arrays.asList("Action", "Drama", "Fantasy"), 87, "Plan to Watch", "Catalog", 2, 2013));
        catalog.add(new Anime("Demon Slayer", Arrays.asList("Action", "Supernatural"), 55,  "Plan to Watch", "Catalog", 2, 2019));
        catalog.add(new Anime("Steins Gate", Arrays.asList("Sci-Fi", "Thriller"), 24,  "Plan to Watch", "Catalog", 2, 2011));
        return catalog;
    }

    // REQUIRES: favGenre != null
    // MODIFIES: recommendationsContainer
    // EFFECTS: adds genre indicator badge row to recommendationsContainer.
    private void addGenreBadge(String favGenre) {
        JLabel genreHint = Theme.makeBodyLabel("Top Genre Preference: ", Theme.TEXT_DIM);
        JLabel genreBadge = new JLabel(" " + favGenre + " ");
        genreBadge.setFont(Theme.FONT_SMALL.deriveFont(10f));
        genreBadge.setForeground(Theme.ACCENT_PINK);
        genreBadge.setOpaque(true);
        genreBadge.setBackground(new Color(255, 42, 122, 25));
        genreBadge.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_PINK_DIM, 1));

        JPanel hintRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        hintRow.setOpaque(false);
        hintRow.add(genreHint);
        hintRow.add(genreBadge);
        hintRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        recommendationsContainer.add(hintRow);
        recommendationsContainer.add(Box.createVerticalStrut(14));
    }

    // REQUIRES: topRecs != null
    // MODIFIES: recommendationsContainer
    // EFFECTS: generates and appends recommendation card panels to recommendationsContainer for each anime in topRecs.
    private void addRecommendationList(List<Anime> topRecs, String favGenre) {
        for (Anime anime : topRecs) {
            recommendationsContainer.add(makeRecCard(anime, favGenre));
            recommendationsContainer.add(Box.createVerticalStrut(12));
        }
    }

    // REQUIRES: anime != null
    // EFFECTS: constructs and returns a rounded panel containing anime priority badge, detail info, and quick actions
    private JPanel makeRecCard(Anime anime, String matchedGenre) {
        JPanel card = Theme.makeRoundedPanel(Theme.PANEL_BG, 14);
        card.setLayout(new BorderLayout(12, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        card.add(makePriorityBadge(anime.getPriority()), BorderLayout.WEST);
        card.add(makeDetailPanel(anime, matchedGenre), BorderLayout.CENTER);

        JButton startWatchBtn = Theme.makeRoundedButton("Start Watching");
        startWatchBtn.setFont(Theme.FONT_SMALL.deriveFont(11f));
        startWatchBtn.addActionListener(e -> {
            anime.setStatus("Watching");
            if (anime.getCurrentEpisodeWatched() == 0) {
                anime.setCurrentEpisodeWatched(1);
            }
            animeList.updateAnime(anime);
            generateRecommendations();
        });

        JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        rightCol.setOpaque(false);
        rightCol.add(startWatchBtn);
        card.add(rightCol, BorderLayout.EAST);

        return card;
    }

    private JPanel makeCatalogCard(Anime anime) {
        JPanel card = Theme.makeRoundedPanel(Theme.PANEL_BG, 14);
        card.setLayout(new BorderLayout(12, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel detailCol = new JPanel(new GridLayout(2, 1, 0, 3));
        detailCol.setOpaque(false);

        JLabel nameLabel = new JLabel(anime.getName());
        nameLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
        nameLabel.setForeground(Color.WHITE);

        JLabel genreLabel = Theme.makeBodyLabel("Genres: " + String.join(", ", anime.getGenre()) + "  •  " + anime.getLength() + " eps", Theme.TEXT_DIM);
        genreLabel.setFont(Theme.FONT_SMALL);

        detailCol.add(nameLabel);
        detailCol.add(genreLabel);

        JButton addBtn = Theme.makeRoundedButton("+ Add to Watchlist");
        addBtn.setFont(Theme.FONT_SMALL.deriveFont(11f));
        addBtn.addActionListener(e -> {
            animeList.addAnime(new Anime(anime.getName(), anime.getGenre(), anime.getEpisodeCount(), "Plan to Watch", anime.getNote(), anime.getPriority(), anime.getYear()));
            generateRecommendations();
        });

        JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        rightCol.setOpaque(false);
        rightCol.add(addBtn);

        card.add(detailCol, BorderLayout.CENTER);
        card.add(rightCol, BorderLayout.EAST);
        return card;
    }

    // EFFECTS: constructs and returns left column panel containing styled priority badge.
    private JPanel makePriorityBadge(int priority) {
        JLabel priorityBadge = new JLabel(" P" + priority + " ");
        priorityBadge.setFont(Theme.FONT_SMALL.deriveFont(10f));
        priorityBadge.setForeground(Color.WHITE);
        priorityBadge.setOpaque(true);
        priorityBadge.setBackground(Theme.ACCENT_PINK);
        priorityBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JPanel leftCol = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        leftCol.setOpaque(false);
        leftCol.add(priorityBadge);
        return leftCol;
    }

    // REQUIRES: anime != null
    // EFFECTS: constructs and returns center column panel containing anime title, matched genre, season, and episode
    private JPanel makeDetailPanel(Anime anime, String matchedGenre) {
        JPanel centerCol = new JPanel(new GridLayout(2, 1, 0, 4));
        centerCol.setOpaque(false);

        JLabel nameLabel = new JLabel(anime.getName());
        nameLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
        nameLabel.setForeground(Color.WHITE);

        String matchedStr = (matchedGenre != null && anime.getGenre().contains(matchedGenre)) ? "Matched: " + matchedGenre + "  •  " : "";
        JLabel detailLabel = Theme.makeBodyLabel(
                matchedStr + " seasons  •  " 
                + anime.getLength() + " eps  •  Status: " + anime.getStatus(), Theme.TEXT_DIM);
        detailLabel.setFont(Theme.FONT_SMALL);

        centerCol.add(nameLabel);
        centerCol.add(detailLabel);
        return centerCol;
    }

    // REQUIRES: message != null
    // MODIFIES: recommendationsContainer
    // EFFECTS: creates and adds an empty state message label to recommendationsContainer.
    private void addEmptyState(String message) {
        JLabel label = Theme.makeBodyLabel(message, Theme.TEXT_DIM);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(Theme.FONT_SMALL);
        recommendationsContainer.add(label);
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