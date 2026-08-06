package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

import model.AnimeList;
import persistence.JsonWriter;

// Main background JFrame that holds all panels
public class MainWindow extends JFrame {
    
    private static final String JSON_STORE = "./data/animeList.json";
    private AnimeList animeList;
    
    private JPanel navigationPanel;
    private WatchListPanel watchListPanel;
    private StatisticsPanel statisticsPanel;
    private RecommendationPanel recommendationPanel;
    private AnimeBasePanel animeBasePanel;
    private SettingsPanel settingsPanel;

    CardLayout cardLayout = new CardLayout();
    JPanel contentContainer = new JPanel(cardLayout);

    private Point initialClick;

    public MainWindow(AnimeList animeList) {
        this.animeList = animeList;
        initializeWindow();
    }

    // EFFECTS: initialize main JFrame and adding all panels to it
    @SuppressWarnings("methodlength")
    private void initializeWindow() {
        setUndecorated(true); // no title bar
        setTitle("AniLog");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Overrides Swing's default behavior EXIT_ON_CLOSE 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExitPrompt();
            }
        });

        //set layout
        setLayout(new BorderLayout());

        // setup CardLayout container
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);

        // instantiate each distinct panel subclass with central refresh trigger
        watchListPanel = new WatchListPanel(animeList, this::refreshAllPanels);
        statisticsPanel = new StatisticsPanel(animeList);
        recommendationPanel = new RecommendationPanel(animeList);
        animeBasePanel = new AnimeBasePanel(animeList, this::refreshAllPanels);
        settingsPanel = new SettingsPanel(animeList, this::refreshAllPanels);

        // add panel instances to the container with string keys
        contentContainer.add(watchListPanel, "WATCHLIST");
        contentContainer.add(recommendationPanel, "RECOMMENDATIONS");
        contentContainer.add(statisticsPanel, "STATISTICS");
        contentContainer.add(animeBasePanel, "ANIME_BASE");
        contentContainer.add(settingsPanel, "SETTINGS");

        // pass a tab-switching callback to the navigation bar, updating tabs on switch
        navigationPanel = new NavigationPanel(tabKey -> {
            cardLayout.show(contentContainer, tabKey);
            refreshAllPanels();
        });

        add(navigationPanel, BorderLayout.WEST);
        add(contentContainer, BorderLayout.CENTER);

        // make window draggable via navigation panel
        makeDraggable(navigationPanel);

        //keep at the end
        setVisible(true);

        //intro animation
        startSplashAnimation();
    }

    // EFFECTS: allows component to be dragged
    private void makeDraggable(Component component) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        component.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (initialClick != null) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    int dx = e.getX() - initialClick.x;
                    int dy = e.getY() - initialClick.y;

                    int newX = thisX + dx;
                    int newY = thisY + dy;
                    setLocation(newX, newY);
                }
            }
        });
    }

    // EFFECTS: Refreshes all tab panels displaying anime list data
    public void refreshAllPanels() {
        if (watchListPanel != null) {
            watchListPanel.refreshList();
        }
        if (statisticsPanel != null) {
            statisticsPanel.updateStatistics();
        }
        if (recommendationPanel != null) {
            recommendationPanel.generateRecommendations();
        }
    }

    // EFFECTS: using a popup window to prompt user to save on exit
    private void handleExitPrompt() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Would you like to save your watchlist changes before exiting?",
                "Save on Exit",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            saveWatchList();
            dispose();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            dispose();
            System.exit(0);
        }
    }
    
    // EFFECTS: invoke saving on the data layer
    private void saveWatchList() {
        try {
            File file = new File(JSON_STORE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            JsonWriter writer = new JsonWriter(JSON_STORE);
            writer.open();
            writer.write(animeList);
            writer.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to write to file: " 
                        + JSON_STORE + "\n" + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // EFFECTS: creating a top layer plane to disply startup animation
    private void startSplashAnimation() {
        SplashOverlayPanel splashPanel = new SplashOverlayPanel(20);
        splashPanel.setBounds(0, 0, getWidth(), getHeight());
        
        // set as glass pane so it overlays on top of all existing UI panels
        setGlassPane(splashPanel);
        splashPanel.setVisible(true);

        // Hide overlay when animation finishes
        splashPanel.playAnimation(() -> {
            splashPanel.setVisible(false); 
        });
    }
}
