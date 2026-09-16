package persistence;

import java.util.List;

import model.Anime;

public class DataBaseTest {

    public static void main(String[] args) {

        AnimeRepository repository = new AnimeRepository();

        List<Anime> animeList = repository.getAllAnime();

        System.out.println("Anime in database:");

        for (Anime anime : animeList) {
            System.out.println(anime.getName());
        }
    }
}