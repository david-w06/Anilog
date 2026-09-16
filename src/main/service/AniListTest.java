package service;

import java.util.List;

import model.Anime;


public class AniListTest {

    public static void main(String[] args) {
        AniListService service = new AniListService();

        List<Anime> results = service.searchAnime("Frieren");

        for (Anime anime : results) {
            System.out.println("ID: " + anime.getId());
            System.out.println("Title: " + anime.getName());
            System.out.println("Length: " + anime.getLength());
            System.out.println();
        }
    }
}