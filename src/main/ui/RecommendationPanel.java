package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.Anime;
import model.AnimeList;
import service.AniListService;
import service.RecommendationService;
import model.Recommendation;

// JPanel that represents the tab for recommendation
public class RecommendationPanel extends JPanel {

    private AnimeList animeList;
    private RecommendationService recommendationService;
    private AniListService aniListService;
    private JPanel recommendationsContainer;

    // REQUIRES: animeList != null
    // MODIFIES: this
    // EFFECTS: constructs a RecommendationPanel object with the given anime list and initializes UI components
    public RecommendationPanel(AnimeList animeList) {
        this.animeList = animeList;
        this.recommendationService = new RecommendationService();
        this.aniListService = new AniListService();
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

    public void generateRecommendations() {
        recommendationsContainer.removeAll();

        JLabel loadingLabel =
                new JLabel("Loading recommendations...");

        recommendationsContainer.add(loadingLabel);

        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();

        SwingWorker<List<Recommendation>, Void> worker =
                new SwingWorker<List<Recommendation>, Void>() {

            @Override
            protected List<Recommendation> doInBackground()
                    throws Exception {

                List<Anime> candidates =
                        aniListService.getPopularAnime();

                return recommendationService.getRecommendations(
                        animeList.getAnimes(),
                        candidates);
            }

            @Override
            protected void done() {
                try {
                    List<Recommendation> recommendations =
                            get();

                    recommendationsContainer.removeAll();

                    if (recommendations.isEmpty()) {
                        addEmptyState("No recommendations found.");
                    } else {
                        for (Recommendation recommendation
                                : recommendations) {

                            addRecommendationCard(
                                    recommendation);
                        }
                    }

                    recommendationsContainer.revalidate();
                    recommendationsContainer.repaint();

                } catch (Exception e) {
                    recommendationsContainer.removeAll();

                    JLabel errorLabel =
                            new JLabel(
                                    "Unable to load recommendations.");

                    recommendationsContainer.add(errorLabel);

                    recommendationsContainer.revalidate();
                    recommendationsContainer.repaint();

                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    // REQUIRES: anime != null
    // EFFECTS: constructs and returns a rounded panel containing anime priority badge, detail info, and quick actions
    private JPanel makeRecCard(Recommendation recommendation) {
        Anime anime = recommendation.getAnime();

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 0));

        // ================= Left: Cover =================

        JLabel coverLabel = new JLabel();

        // For now, use your existing cover-image loading code here.
        // We will clean this up in the next step if necessary.

        card.add(coverLabel, BorderLayout.WEST);


        // ================= Center: Information =================

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(anime.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        infoPanel.add(titleLabel);


        // Genres

        String genres = String.join(
                " • ",
                anime.getGenre());

        JLabel genreLabel = new JLabel(genres);

        infoPanel.add(genreLabel);


        // Matched genres and tags

        List<String> matchedGenres =
                recommendation.getMatchedGenres();

        List<String> matchedTags =
                recommendation.getMatchedTags();

        List<String> matches =
                new ArrayList<>();

        matches.addAll(matchedGenres);
        matches.addAll(matchedTags);

        if (!matches.isEmpty()) {
            JLabel matchLabel = new JLabel(
                    "Matches: " + String.join(" • ", matches));

            infoPanel.add(matchLabel);
        }


        // Episodes / year

        String details =
                anime.getLength() + " episodes";

        if (anime.getYear() > 0) {
            details += " • " + anime.getYear();
        }

        JLabel detailsLabel = new JLabel(details);

        infoPanel.add(detailsLabel);


        card.add(infoPanel, BorderLayout.CENTER);


        // ================= Right: Score + Add =================

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(
                new BoxLayout(actionPanel, BoxLayout.Y_AXIS));

        double score = recommendation.getNormalizedScore();

        JLabel scoreLabel = new JLabel(
            String.format("%.0f%%", score));

        actionPanel.add(scoreLabel);


        JButton addButton = new JButton("+ Add");

        addButton.addActionListener(e -> {
            animeList.addAnime(anime);
            generateRecommendations();
        });

        actionPanel.add(addButton);

        card.add(actionPanel, BorderLayout.EAST);

        return card;
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

    private void addRecommendationCard(Recommendation recommendation) {

        JPanel card = makeRecCard(recommendation);

        recommendationsContainer.add(card);
    }
}