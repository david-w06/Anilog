package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Anime;
import model.AnimeList;

// JPanel that represents the tab for the watchlist tab
public class WatchListPanel extends JPanel {

    private AnimeList animeList;
    private Runnable refreshCallback;
    private DefaultListModel<Anime> listModel;
    private JList<Anime> animeJList;

    private JComboBox<String> filterBox;
    private JComboBox<String> sortBox;

    // REQUIRES: animeList != null
    // EFFECTS: constructs a WatchListPanel with the given animeList and no refresh callback
    public WatchListPanel(AnimeList animeList) {
        this(animeList, null);
    }

    // REQUIRES: animeList != null
    // MODIFIES: this
    // EFFECTS: constructs a WatchListPanel with the given animeList and refreshCallback, then initializes the UI
    public WatchListPanel(AnimeList animeList, Runnable refreshCallback) {
        this.animeList = animeList;
        this.refreshCallback = refreshCallback;
        initializePanel();
    }

    // MODIFIES: this
    // EFFECTS: initializes layout, constructs top control bar, list panel, scroll bar styling, and bottom action bar
    @SuppressWarnings("methodlength")
    private void initializePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        // top control bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        topBar.setBackground(Theme.PANEL_BG);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DIM));

        JLabel pageTitle = Theme.makeTitleLabel("Watchlist");
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 20));
        topBar.add(pageTitle);

        JLabel filterLabel = Theme.makeBodyLabel("Filter:", Theme.TEXT_DIM);
        
        filterBox = makeStyledComboBox(new String[]{"All Statuses", "Not Watched", 
            "Watching", "Plan to Watch", "Completed"});
        filterBox.addActionListener(e -> refreshList());

        JLabel sortLabel = Theme.makeBodyLabel("Sort:", Theme.TEXT_DIM);
        sortBox = makeStyledComboBox(new String[]{"Default Order", "Sort by Name", "Sort by Rating", "Sort by Status"});
        sortBox.addActionListener(e -> refreshList());

        topBar.add(filterLabel);
        topBar.add(filterBox);
        topBar.add(Box.createHorizontalStrut(8));
        topBar.add(sortLabel);
        topBar.add(sortBox);

        add(topBar, BorderLayout.NORTH);

        // list view
        listModel = new DefaultListModel<>();
        animeJList = new JList<>(listModel);
        animeJList.setBackground(Theme.BG_DARK);
        animeJList.setSelectionBackground(Theme.ACCENT_PINK);
        animeJList.setSelectionForeground(Color.WHITE);
        animeJList.setCellRenderer(new AnimeListCellRenderer());
        animeJList.setFixedCellHeight(72);

        JScrollPane scrollPane = new JScrollPane(animeJList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Theme.BG_DARK);
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        add(scrollPane, BorderLayout.CENTER);

        // action bar bottom
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottomBar.setBackground(Theme.PANEL_BG);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_DIM));

        JButton addButton    = Theme.makeRoundedButton("+ Add Anime");
        JButton modifyButton = Theme.makeRoundedButton("~ Modify", true);
        JButton deleteButton = Theme.makeRoundedButton("x Delete", true);

        addButton.addActionListener(e -> openAddAnimeDialog());
        modifyButton.addActionListener(e -> openModifyAnimeDialog());
        deleteButton.addActionListener(e -> deleteSelectedAnime());

        bottomBar.add(deleteButton);
        bottomBar.add(modifyButton);
        bottomBar.add(addButton);

        add(bottomBar, BorderLayout.SOUTH);

        refreshList();
    }

    // REQUIRES: items != null
    // EFFECTS: creates a custom styled JComboBox populated with items
    @SuppressWarnings("methodlength")
    private JComboBox<String> makeStyledComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setOpaque(true);
        box.setBackground(Theme.PANEL_BG);
        box.setForeground(Theme.ACCENT_PINK);
        box.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 11f));
        box.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_PINK, 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 6)
        ));
        box.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(
                    Graphics g, Rectangle bounds, boolean hasFocus) {
                // Paint the current value background with our dark theme.
                g.setColor(Theme.PANEL_BG);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            public void paintCurrentValue(
                    Graphics g, Rectangle bounds, boolean hasFocus) {

                ListCellRenderer<Object> renderer = (ListCellRenderer<Object>) comboBox.getRenderer();

                Component c = renderer.getListCellRendererComponent(
                        new JList<>(),
                        comboBox.getSelectedItem(),
                        -1,
                        false,       // don't treat closed box as selected
                        false
                );

                c.setBackground(Theme.PANEL_BG);
                c.setForeground(Theme.TEXT_LIGHT);

                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setOpaque(true);
                }

                SwingUtilities.paintComponent(
                        g,
                        c,
                        comboBox,
                        bounds.x,
                        bounds.y,
                        bounds.width,
                        bounds.height
                );
            }

            
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Theme.ACCENT_PINK);
                            int arrowWidth = 8;
                            int arrowHeight = 5;

                            int x = (getWidth() - arrowWidth) / 2;
                            int y = (getHeight() - arrowHeight) / 2;

                            int[] posX = {x, x + arrowWidth, x + arrowWidth / 2};

                            int[] posY = {y, y, y + arrowHeight};

                            g2.fillPolygon(posX, posY, 3);
                                g2.dispose();
                            }
                };
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(16, 16));
                return btn;
            }
        });
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBackground(isSelected ? Theme.ACCENT_PINK : Theme.PANEL_BG);
                lbl.setForeground(isSelected ? Color.WHITE : Theme.TEXT_LIGHT);
                lbl.setFont(Theme.FONT_BODY.deriveFont(11f));
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return lbl; 
            }
        });
        return box;
    }

   // REQUIRES: bar != null
    // MODIFIES: bar
    // EFFECTS: applies custom UI layout and accent colors to bar
    @SuppressWarnings("methodlength")
    private void styleScrollBar(JScrollBar bar) {
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = Theme.ACCENT_PINK;
                trackColor = Theme.BG_DARK;
            }

            @Override
            protected JButton createDecreaseButton(int o) {
                return invisibleButton();
            }

            @Override
            protected JButton createIncreaseButton(int o) {
                return invisibleButton();
            }

            private JButton invisibleButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });
        bar.setBackground(Theme.BG_DARK);
        bar.setPreferredSize(new Dimension(6, 0));
    }

    // MODIFIES: listModel
    // EFFECTS: clears listModel and repopulates it based on filter selections
    public void refreshList() {
        listModel.clear();

        String selectedFilter = (String) filterBox.getSelectedItem();
        List<Anime> displayList;
        if ("All Statuses".equals(selectedFilter)) {
            displayList = animeList.getAnimes();
        } else {
            displayList = animeList.filterByStatus(selectedFilter);
        }

        String selectedSort = (String) sortBox.getSelectedItem();
        if ("Sort by Name".equals(selectedSort)) {
            displayList = sortSublistByName(displayList);
        } else if ("Sort by Rating".equals(selectedSort)) {
            displayList = sortSublistByRating(displayList);
        } else if ("Sort by Status".equals(selectedSort)) {
            displayList = sortSublistByStatus(displayList);
        }

        for (Anime anime : displayList) {
            listModel.addElement(anime);
        }
    }

    // MODIFIES: animeList, listModel
    // EFFECTS: displays a dialog for adding an anime
    // EFFECTS: displays a dialog for adding a new anime and runs refreshCallback if non-null
    @SuppressWarnings("methodlength")
    private void openAddAnimeDialog() {
        JTextField nameField    = new JTextField();
        JTextField genreField   = new JTextField();
        JTextField epsField = new JTextField("12");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "Not Watched",
                "Watching",
                "Completed",
                "Plan to Watch"
        });
        JTextField noteField     = new JTextField();
        JTextField priorityField = new JTextField("1");
        JTextField yearField     = new JTextField("2024");

        Theme.styleTextField(nameField);
        Theme.styleTextField(genreField);
        Theme.styleTextField(epsField);
        Theme.styleTextField(noteField);
        Theme.styleTextField(priorityField);
        Theme.styleTextField(yearField);
        Theme.styleComboBox(statusBox);

        Object[] message = {
            Theme.makeBodyLabel("Anime Title:", Theme.TEXT_LIGHT), nameField,
            Theme.makeBodyLabel("Genres (comma separated):", Theme.TEXT_LIGHT), genreField,
            Theme.makeBodyLabel("Episodes Count:", Theme.TEXT_LIGHT), epsField,
            Theme.makeBodyLabel("Initial Status:", Theme.TEXT_LIGHT), statusBox,
            Theme.makeBodyLabel("Priority Rank (1 = High):", Theme.TEXT_LIGHT), priorityField,
            Theme.makeBodyLabel("Personal Notes:", Theme.TEXT_LIGHT), noteField,
            Theme.makeBodyLabel("Year Released:", Theme.TEXT_LIGHT), yearField
        };

        JOptionPane pane = new JOptionPane(
                message,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        JDialog dialog = pane.createDialog(this, "Add New Anime");

        // Dark dialog background
        dialog.getContentPane().setBackground(Theme.BG_DARK);

        // Find and style the buttons
        Theme.styleDialogButtons(dialog);

        // Set background
        Theme.styleDialogBackground(dialog);

        dialog.setResizable(false);
        dialog.setVisible(true);

        Object selectedValue = pane.getValue();

        if (!(selectedValue instanceof Integer)) {
            return;
        }

        int option = (Integer) selectedValue;

        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                List<String> genres =
                        Arrays.asList(genreField.getText().split("\\s*,\\s*"));
                int episodeCount = Integer.parseInt(epsField.getText().trim());
                String status = (String) statusBox.getSelectedItem();
                String note = noteField.getText().trim();
                int priority = Integer.parseInt(priorityField.getText().trim());
                int year = Integer.parseInt(yearField.getText().trim());

                Anime newAnime = new Anime(name, genres, episodeCount, status, note, priority, year);

                animeList.addAnime(newAnime);
                refreshList();

                if (refreshCallback != null) {
                    refreshCallback.run();
                }

            } catch (NumberFormatException ex) {
                Theme.showStyledMessage(
                        this,
                        "Invalid numerical inputs. Please check season/episode counts or priorities.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // MODIFIES: selected
    // EFFECTS: displays a dialog for editing selected anime and runs refreshCallback if non-null
    @SuppressWarnings("methodlength")
    private void openModifyAnimeDialog() {
        Anime selected = animeJList.getSelectedValue();
        if (selected == null) {
            Theme.showStyledMessage(this, "Please select an anime entry to modify.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField epWatchedField = new JTextField(String.valueOf(selected.getCurrentEpisodeWatched()));
        JTextField epsField = new JTextField(String.valueOf(selected.getLength()));

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Not Watched", "Watching", 
                "Completed", "Plan to Watch"});
        statusBox.setSelectedItem(selected.getStatus());
        JTextField ratingField   = new JTextField(selected.getRating() == -1.0 ? "" 
                : String.valueOf(selected.getRating()));
        JTextField noteField     = new JTextField(selected.getNote());
        JTextField priorityField = new JTextField(String.valueOf(selected.getPriority()));

        Object[] message = {
            Theme.makeBodyLabel("Update Watched Episodes (0 to " + selected.getLength() + "):", Theme.TEXT_LIGHT), epWatchedField,
            Theme.makeBodyLabel("Episodes Count:", Theme.TEXT_LIGHT), epsField,
            Theme.makeBodyLabel("Update Watch Status:", Theme.TEXT_LIGHT), statusBox,
            Theme.makeBodyLabel("Update Rating (0.0 to 10.0, leave empty for unrated):", Theme.TEXT_LIGHT), ratingField,
            Theme.makeBodyLabel("Priority Rank:", Theme.TEXT_LIGHT), priorityField,
            Theme.makeBodyLabel("Personal Notes:", Theme.TEXT_LIGHT), noteField
        };

        int option = Theme.showStyledConfirm(this, message, "Modify: " 
                + selected.getName(), JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String epBreakdownStr = epsField.getText().trim();
                if (!epBreakdownStr.isEmpty()) {
                    List<Integer> newCounts = new ArrayList<>();
                    for (String part : epBreakdownStr.split("\\s*,\\s*")) {
                        if (!part.isEmpty()) {
                            newCounts.add(Integer.parseInt(part));
                        }
                    }
                }

                int newEp = Integer.parseInt(epWatchedField.getText().trim());
                if (newEp < 0 || newEp > selected.getLength()) {
                    throw new IllegalArgumentException("Episodes must be between 0 and total length.");
                }
                selected.setCurrentEpisodeWatched(newEp);
                
                if (newEp == selected.getLength()) {
                    selected.setStatus("Completed");
                } else {
                    selected.setStatus((String) statusBox.getSelectedItem());
                }

                String ratingInput = ratingField.getText().trim();
                if (ratingInput.isEmpty()) {
                    selected.setRating(-1.0);
                } else {
                    double newRating = Double.parseDouble(ratingInput);
                    if (newRating < 0.0 || newRating > 10.0) {
                        throw new IllegalArgumentException("Rating must be between 0.0 and 10.0.");
                    }
                    selected.setRating(newRating);
                }

                selected.setNote(noteField.getText().trim());
                selected.setPriority(Integer.parseInt(priorityField.getText().trim()));
                animeList.updateAnime(selected);

                refreshList();
                if (refreshCallback != null) {
                    refreshCallback.run();
                } 
            } catch (Exception ex) {
                Theme.showStyledMessage(this, "Invalid input: " 
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // MODIFIES: animeList, listModel
    // EFFECTS: prompts for delete confirmation
    private void deleteSelectedAnime() {
        Anime selected = animeJList.getSelectedValue();
        if (selected != null) {
            int confirm = Theme.showStyledConfirm(this,
                    "Are you sure you want to delete '" + selected.getName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                animeList.removeAnime(selected);
                refreshList();
                if (refreshCallback != null) {
                    refreshCallback.run();
                } 
            }
        }
    }

    // REQUIRES: list != null
    // EFFECTS: returns a new list containing elements of list sorted by anime name
    private List<Anime> sortSublistByName(List<Anime> list) {
        AnimeList temp = new AnimeList();
        list.forEach(temp::addAnime);
        return temp.sortByName();
    }

    // REQUIRES: list != null
    // EFFECTS: returns a new list containing elements of list sorted by anime rating
    private List<Anime> sortSublistByRating(List<Anime> list) {
        AnimeList temp = new AnimeList();
        list.forEach(temp::addAnime);
        return temp.sortByRating();
    }

    // REQUIRES: list != null
    // EFFECTS: returns a new list containing elements of list sorted by anime status
    private List<Anime> sortSublistByStatus(List<Anime> list) {
        AnimeList temp = new AnimeList();
        list.forEach(temp::addAnime);
        return temp.sortByStatus();
    }

    // custom cell renderer
    private static class AnimeListCellRenderer extends JPanel implements ListCellRenderer<Anime> {
        private final JLabel nameLabel   = new JLabel();
        private final JLabel detailLabel = new JLabel();
        private final JLabel statusBadge = new JLabel();

        // MODIFIES: this
        // EFFECTS: constructs custom cell renderer layout, initializes fonts, status badge, and panel structure
        @SuppressWarnings("methodlength")
        public AnimeListCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_DIM),
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)
            ));

            nameLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
            detailLabel.setFont(Theme.FONT_SMALL);

            // status badge
            statusBadge.setFont(Theme.FONT_SMALL.deriveFont(10f));
            statusBadge.setOpaque(true);
            statusBadge.setBorder(BorderFactory.createEmptyBorder(3, 9, 3, 9));

            JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
            leftPanel.setOpaque(false);
            leftPanel.add(nameLabel);
            leftPanel.add(detailLabel);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightPanel.setOpaque(false);
            rightPanel.add(statusBadge);

            add(leftPanel, BorderLayout.CENTER);
            add(rightPanel, BorderLayout.EAST);
        }

        // REQUIRES: list != null, anime != null
        // MODIFIES: this
        // EFFECTS: configures cell labels and  colors based on selection state and returns this panel component
        @Override
        @SuppressWarnings("methodlength")
        public Component getListCellRendererComponent(JList<? extends Anime> list, Anime anime,
                int index, boolean isSelected, boolean cellHasFocus) {

            nameLabel.setText(anime.getName() + "  [" + anime.getLength() 
                    + " episodes" + "]");

            String ratingStr = (anime.getRating() == -1.0) ? "Unrated" : anime.getRating() + "/10";
            String genreStr  = String.join(", ", anime.getGenre());
            detailLabel.setText("Genres: " + genreStr + "   •   Progress: " + anime.getCurrentEpisodeWatched() 
                    + "/" + anime.getLength() + " eps   •   Score: " + ratingStr);

            statusBadge.setText(anime.getStatus() + "  P" + anime.getPriority());

            if (isSelected) {
                setBackground(new Color(255, 42, 122, 60));
                nameLabel.setForeground(Color.WHITE);
                detailLabel.setForeground(Theme.TEXT_LIGHT);
                statusBadge.setBackground(Theme.ACCENT_PINK);
                statusBadge.setForeground(Color.WHITE);
            } else {
                setBackground(index % 2 == 0 ? Theme.PANEL_BG : Theme.PANEL_BG2);
                nameLabel.setForeground(Color.WHITE);
                detailLabel.setForeground(Theme.TEXT_DIM);
                statusBadge.setBackground(new Color(255, 42, 122, 30));
                statusBadge.setForeground(Theme.ACCENT_PINK);
            }
            return this;
        }
    }
}