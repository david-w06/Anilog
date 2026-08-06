package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
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
        JButton refreshBtn = Theme.makeRoundedButton(">> Generate");
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
    //          favorite genre, or displays an empty state message if no recommendations exist.
    public void generateRecommendations() {
        recommendationsContainer.removeAll();
        String favGenre = animeList.calculateFavoriteGenre();

        if (favGenre == null) {
            addEmptyState("Add watched anime with ratings to unlock recommendations!");
        } else {
            List<Anime> topRecs = animeList.getTopRecommendations(favGenre);
            if (topRecs.isEmpty()) {
                addEmptyState("No 'Plan to Watch' anime found matching your top genre: " + favGenre);
            } else {
                addGenreBadge(favGenre);
                addRecommendationList(topRecs, favGenre);
            }
        }

        revalidate();
        repaint();
    }

    // REQUIRES: favGenre != null
    // MODIFIES: recommendationsContainer
    // EFFECTS: adds genre indicator badge row to recommendationsContainer.
    private void addGenreBadge(String favGenre) {
        JLabel genreHint = Theme.makeBodyLabel("Based on your top genre: ", Theme.TEXT_DIM);
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

    // REQUIRES: topRecs != null, favGenre != null
    // MODIFIES: recommendationsContainer
    // EFFECTS: generates and appends recommendation card panels to recommendationsContainer for each anime in topRecs.
    private void addRecommendationList(List<Anime> topRecs, String favGenre) {
        for (Anime anime : topRecs) {
            recommendationsContainer.add(makeRecCard(anime, favGenre));
            recommendationsContainer.add(Box.createVerticalStrut(12));
        }
    }

    // REQUIRES: anime != null, matchedGenre != null
    // EFFECTS: constructs and returns a rounded panel containing anime priority badge and detail information.
    private JPanel makeRecCard(Anime anime, String matchedGenre) {
        JPanel card = Theme.makeRoundedPanel(Theme.PANEL_BG, 14);
        card.setLayout(new BorderLayout(12, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        card.add(makePriorityBadge(anime.getPriority()), BorderLayout.WEST);
        card.add(makeDetailPanel(anime, matchedGenre), BorderLayout.CENTER);

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

        JPanel leftCol = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leftCol.setOpaque(false);
        leftCol.add(priorityBadge);
        return leftCol;
    }

    // REQUIRES: anime != null, matchedGenre != null
    // EFFECTS: constructs and returns center column panel containing anime title, matched genre, season, and episode
    private JPanel makeDetailPanel(Anime anime, String matchedGenre) {
        JPanel centerCol = new JPanel(new GridLayout(2, 1, 0, 4));
        centerCol.setOpaque(false);

        JLabel nameLabel = new JLabel(anime.getName());
        nameLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
        nameLabel.setForeground(Color.WHITE);

        JLabel detailLabel = Theme.makeBodyLabel(
                "Matched: " + matchedGenre + "  •  " + anime.getSeasons() + " seasons  •  " 
                + anime.getLength() + " eps", Theme.TEXT_DIM);
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