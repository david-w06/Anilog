package ui;

import model.AnimeList;

public class Main {
    public static void main(String[] args) throws Exception {
        // old start new AnimeConsoleUI();
        AnimeList animeList = new AnimeList();

        new MainWindow(animeList);
    }

}
