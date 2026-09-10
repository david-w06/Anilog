package ui;

import model.AnimeList;

public class Main {
    public static void main(String[] args) throws Exception {
        AnimeList animeList = new AnimeList();
        new MainWindow(animeList);
    }

}
