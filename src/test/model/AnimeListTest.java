package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import static org.junit.jupiter.api.Assertions.*;

public class AnimeListTest{
    private AnimeList testList;
    private Anime frieren;
    private Anime naruto;
    private Anime toradora;
    
    @BeforeEach
    public void runBefore() {
        testList = new AnimeList();

        List<String> frierenGenres = new ArrayList<>();
        frierenGenres.add("Fantasy");
        frierenGenres.add("Adventure");
        frieren = new Anime("Frieren", frierenGenres, 28, 1, "Not Watched", "Masterpiece", 1);

        List<String> narutoGenres = new ArrayList<>();
        narutoGenres.add("Action");
        narutoGenres.add("Adventure");
        naruto = new Anime("Naruto", narutoGenres, 100, 5, "Watching", "Classic shonen", 2);

        List<String> toradoraGenres = new ArrayList<>();
        toradoraGenres.add("Romance");
        toradoraGenres.add("Comedy");
        toradora = new Anime("Toradora", toradoraGenres, 25, 1, "Completed", "Tsundere staple", 3);
    }

    @Test
    public void testConstructor() {
        assertNotNull(testList.getAnimes());
        assertEquals(0, testList.getAnimes().size());
    }

    @Test
    public void testAddAnime(){
        testList.addAnime(frieren);
        assertEquals(1, testList.getAnimes().size());
        assertTrue(testList.getAnimes().contains(frieren));

        testList.addAnime(naruto);
        assertEquals(2, testList.getAnimes().size());
        assertTrue(testList.getAnimes().contains(naruto));
    }

    @Test
    public void testRemoveAnime(){
        testList.addAnime(frieren);
        testList.addAnime(naruto);

        testList.removeAnime(frieren);
        assertEquals(1, testList.getAnimes().size());
        assertFalse(testList.getAnimes().contains(frieren));
        assertTrue(testList.getAnimes().contains(naruto));

        testList.removeAnime(toradora);
        assertEquals(1, testList.getAnimes().size());
    }

    // ================= Test Filtering Operations =================

    @Test
    public void testFilterByGenre() {
        testList.addAnime(frieren);
        testList.addAnime(naruto);
        
        List<Anime> results = testList.filterByGenre("Fantasy");
        assertEquals(1, results.size());

        results = testList.filterByGenre("Romance");
        assertEquals(0, results.size());
    }

    @Test
    public void testFilterByGenreMultipleMatches() {
        testList.addAnime(frieren);  // Adventure, Fantasy
        testList.addAnime(naruto);   // Adventure, Action
        testList.addAnime(toradora); // Romance, Comedy

        List<Anime> results = testList.filterByGenre("Adventure");
        assertEquals(2, results.size());
        assertTrue(results.contains(frieren));
        assertTrue(results.contains(naruto));
        assertFalse(results.contains(toradora));
    }

    @Test
    public void testFilterByStatus() {
        testList.addAnime(frieren);
        testList.addAnime(naruto); 
        testList.addAnime(toradora); 

        List<Anime> notWatched = testList.filterByStatus("Not Watched");
        assertEquals(1, notWatched.size());
        assertTrue(notWatched.contains(frieren));

        List<Anime> watching = testList.filterByStatus("Watching");
        assertEquals(1, watching.size());
        assertTrue(watching.contains(naruto));
    }

    @Test
    public void testFilterBySeasonCount() {
        testList.addAnime(frieren);
        testList.addAnime(naruto);
        testList.addAnime(toradora); 

        List<Anime> oneSeason = testList.filterBySeasonCount(1);
        assertEquals(2, oneSeason.size());
        assertTrue(oneSeason.contains(frieren));
        assertTrue(oneSeason.contains(toradora));

        List<Anime> fiveSeasons = testList.filterBySeasonCount(5);
        assertEquals(1, fiveSeasons.size());
        assertTrue(fiveSeasons.contains(naruto));

        List<Anime> threeSeasons = testList.filterBySeasonCount(3);
        assertEquals(0, threeSeasons.size());
    }

    // ================= Test Sorting Operations =================
    
    @Test
    public void testSortByName() {
        testList.addAnime(toradora);
        testList.addAnime(frieren);
        testList.addAnime(naruto);

        List<Anime> sorted = testList.sortByName();
        assertEquals(3, sorted.size());
        assertEquals(frieren, sorted.get(0));
        assertEquals(naruto, sorted.get(1));
        assertEquals(toradora, sorted.get(2)); 
    }

    @Test
    public void testSortByRating() {
        testList.addAnime(frieren);
        testList.addAnime(naruto);
        testList.addAnime(toradora);

        frieren.setRating(9.5);
        naruto.setRating(6.0);
        // Toradora remains unrated (-1.0)

        List<Anime> sorted = testList.sortByRating();
        assertEquals(3, sorted.size());
        assertEquals(frieren, sorted.get(0));
        assertEquals(naruto, sorted.get(1));
        assertEquals(toradora, sorted.get(2));
    }

    @Test
    public void testSortByStatus(){
        testList.addAnime(frieren);
        testList.addAnime(naruto);
        testList.addAnime(toradora);
        
        List<Anime> sorted = testList.sortByStatus();
        assertEquals(naruto, sorted.get(0));
        assertEquals(toradora, sorted.get(1));
        assertEquals(frieren, sorted.get(2));

    }

    // ================= Test Analytical Methods =================

    @Test
    public void testCalculateTotalWatchTime() {
        testList.addAnime(frieren);  // 28 eps, 1 season
        testList.addAnime(naruto);   // 100 eps per season, 5 seasons
        
        // Assume each episode is 24 mins or 0.4 hrs, may become adjustable later.
        
        frieren.setCurrentEpisodeWatched(10); // 4 hours
        naruto.setCurrentEpisodeWatched(50);  // 20 hours

        assertEquals(24, testList.calculateTotalWatchTime());
    }

    @Test
    public void testGetTopRecommendations() {
        testList.addAnime(toradora);
        testList.addAnime(naruto);
        testList.addAnime(frieren);

        frieren.setStatus("Plan to Watch");
        naruto.setStatus("Plan to Watch");
        toradora.setStatus("Plan to Watch");

        List<Anime> recs = testList.getTopRecommendations("Adventure");
        
        assertEquals(2, recs.size());
        assertEquals(frieren, recs.get(0));
        assertEquals(naruto, recs.get(1));  
    }


    @Test
    public void testCalculateFavoriteGenre() {
       // Null if nothing in list
        assertNull(testList.calculateFavoriteGenre());
       
        testList.addAnime(frieren);
        testList.addAnime(naruto);
        testList.addAnime(toradora);

        frieren.setCurrentEpisodeWatched(28);
        frieren.setRating(9.5);

        naruto.setCurrentEpisodeWatched(100);
        naruto.setRating(8.0);

        toradora.setCurrentEpisodeWatched(5);
        toradora.setRating(7.0);

        assertEquals("Adventure", testList.calculateFavoriteGenre());
    }

    @Test
    public void testCalculateFavoriteGenreTieAndRatingsWeight() {
        testList.addAnime(frieren);
        testList.addAnime(toradora);

        frieren.setCurrentEpisodeWatched(10);
        frieren.setRating(9.0);

        toradora.setCurrentEpisodeWatched(10);
        toradora.setRating(5.0);

        String favorite = testList.calculateFavoriteGenre();
        assertTrue(favorite.equals("Fantasy") || favorite.equals("Adventure"));
        assertNotEquals("Romance", favorite);
        assertNotEquals("Comedy", favorite);
    }

    @Test
    public void testToJson() {
        
        testList.addAnime(frieren);
        testList.addAnime(naruto);

        JSONObject json = testList.toJson();

        JSONArray jsonAnimeArray = json.getJSONArray("animes"); 
        assertEquals(2, jsonAnimeArray.length());

        JSONObject jsonFirstAnime = jsonAnimeArray.getJSONObject(0);
        assertEquals("Frieren", jsonFirstAnime.getString("name"));

        JSONObject jsonSecondAnime = jsonAnimeArray.getJSONObject(1);
        assertEquals("Naruto", jsonSecondAnime.getString("name"));
    }
}