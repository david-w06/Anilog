package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

import model.AnimeList;
import model.Event;
import model.EventLog;
// import persistence.JsonWriter;

// Main background JFrame that holds all panels
public class MainWindow extends JFrame {
    
    // private static final String JSON_STORE = Theme.getDataFilePath("./data/animeList.json");
    private AnimeList animeList;
    
    private NavigationPanel navigationPanel;
    private WatchListPanel watchListPanel;
    private StatisticsPanel statisticsPanel;
    private RecommendationPanel recommendationPanel;
    private AnimeBasePanel animeBasePanel;
    private SettingsPanel settingsPanel;

    private AnimatedPanelContainer contentContainer;
    private String currentTab = "WATCHLIST";

    private Point initialClick;

    public MainWindow(AnimeList animeList) {
        this.animeList = animeList;
        initializeWindow();
    }

    // EFFECTS: initialize main JFrame with screen resolution bounds checking and adding all panels
    @SuppressWarnings("methodlength")
    private void initializeWindow() {
        setUndecorated(true); // no title bar
        setTitle("AniLog");

        // Scale initial size according to user's display bounds
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();

        int targetWidth = Math.min(1200, (int) (screenBounds.width * 0.85));
        int targetHeight = Math.min(750, (int) (screenBounds.height * 0.85));

        setSize(targetWidth, targetHeight);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (Theme.APP_ICON != null) {
            setIconImage(Theme.APP_ICON);
        }

        // Overrides Swing's default behavior EXIT_ON_CLOSE 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExitPrompt();
            }
        });

        // set layout
        setLayout(new BorderLayout());

        // instantiate each distinct panel subclass with central refresh trigger
        watchListPanel = new WatchListPanel(animeList, this::refreshAllPanels);
        statisticsPanel = new StatisticsPanel(animeList);
        recommendationPanel = new RecommendationPanel(animeList);
        animeBasePanel = new AnimeBasePanel(animeList, this::refreshAllPanels);
        settingsPanel = new SettingsPanel(animeList, this::refreshAllPanels);

        // setup animated container with Watchlist as the initial panel
        contentContainer = new AnimatedPanelContainer(watchListPanel);

        // add panel instances to the container with string keys
        contentContainer.add(watchListPanel, "WATCHLIST");
        contentContainer.add(recommendationPanel, "RECOMMENDATIONS");
        contentContainer.add(statisticsPanel, "STATISTICS");
        contentContainer.add(animeBasePanel, "ANIME_BASE");
        contentContainer.add(settingsPanel, "SETTINGS");

        // pass a tab-switching callback to the navigation bar, updating tabs on switch
        navigationPanel = new NavigationPanel(tabKey -> {
            JPanel nextPanel = getPanel(tabKey);
            int currentIndex = getTabIndex(currentTab);
            int nextIndex = getTabIndex(tabKey);
            boolean fromBottom = nextIndex > currentIndex;

            contentContainer.showPanel(nextPanel, fromBottom);
            currentTab = tabKey;
            refreshAllPanels();
        });

        add(navigationPanel, BorderLayout.WEST);
        add(contentContainer, BorderLayout.CENTER);

        // make window draggable via navigation panel
        makeDraggable(navigationPanel);

        // keep at the end
        setVisible(true);

        // intro animation
        startSplashAnimation();
    }

    private int getTabIndex(String tabKey) {
        switch (tabKey) {
            case "WATCHLIST":
                return 0;
            case "RECOMMENDATIONS":
                return 1;
            case "STATISTICS":
                return 2;
            case "ANIME_BASE":
                return 3;
            case "SETTINGS":
                return 4;
            default:
                return 0;
        }
    }

    private JPanel getPanel(String tabKey) {
        if (tabKey.equals("WATCHLIST")) {
            return watchListPanel;
        } else if (tabKey.equals("RECOMMENDATIONS")) {
            return recommendationPanel;
        } else if (tabKey.equals("STATISTICS")) {
            return statisticsPanel;
        } else if (tabKey.equals("ANIME_BASE")) {
            return animeBasePanel;
        } else if (tabKey.equals("SETTINGS")) {
            return settingsPanel;
        }
        return watchListPanel;
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
        int choice = Theme.showStyledConfirm(
                this,
                "Would you like to save your watchlist changes before exiting?",
                "Save on Exit",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            // print exit log
            printLog(EventLog.getInstance());
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            dispose();
            printLog(EventLog.getInstance());
            System.exit(0);
        }
    }
    
    // EFFECTS: invoke saving on the data layer
/*     private void saveWatchList() {
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
            Theme.showStyledMessage(this, "Unable to write to file: " 
                        + JSON_STORE + "\n" + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
*/

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

    // EFFECTS: prints all logged events.
    public void printLog(EventLog el) {
        for (Event next : el) {
            System.out.println(next.toString());
        }
    }
}
