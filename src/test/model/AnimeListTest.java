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
        frieren = new Anime("Frieren", frierenGenres, 28, "Not Watched", "Masterpiece", 1, 2023);

        List<String> narutoGenres = new ArrayList<>();
        narutoGenres.add("Action");
        narutoGenres.add("Adventure");
        naruto = new Anime("Naruto", narutoGenres, 100, "Watching", "Classic shonen", 2, 2002);

        List<String> toradoraGenres = new ArrayList<>();
        toradoraGenres.add("Romance");
        toradoraGenres.add("Comedy");
        toradora = new Anime("Toradora", toradoraGenres, 25, "Completed", "Tsundere staple", 3, 2008);
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
        testList.addAnime(frieren);  // 28 eps
        testList.addAnime(naruto);   // 100 eps
        
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

    @Test
    public void testSortByStatusAllCombinations() {
        List<String> emptyGenres = new ArrayList<>();
        Anime watching = new Anime("A", emptyGenres, 10, "Watching", "", 1, 2024);
        Anime planToWatch = new Anime("B", emptyGenres, 10, "Plan to Watch", "", 2, 2024);
        Anime completed = new Anime("C", emptyGenres, 10, "Completed", "", 3, 2024);
        Anime notWatched = new Anime("D", emptyGenres, 10, "Not Watched", "", 4, 2024);

        // Permutation 1
        AnimeList list1 = new AnimeList();
        list1.addAnime(notWatched);
        list1.addAnime(planToWatch);
        list1.addAnime(completed);
        list1.addAnime(watching);

        List<Anime> sorted1 = list1.sortByStatus();
        assertEquals(watching, sorted1.get(0));
        assertEquals(planToWatch, sorted1.get(1));
        assertEquals(completed, sorted1.get(2));
        assertEquals(notWatched, sorted1.get(3));

        // Permutation 2
        AnimeList list2 = new AnimeList();
        list2.addAnime(completed);
        list2.addAnime(notWatched);
        list2.addAnime(watching);
        list2.addAnime(planToWatch);

        List<Anime> sorted2 = list2.sortByStatus();
        assertEquals(watching, sorted2.get(0));
        assertEquals(planToWatch, sorted2.get(1));
        assertEquals(completed, sorted2.get(2));
        assertEquals(notWatched, sorted2.get(3));
    }

    @Test
    public void testSortByRatingWithNegativeRatingBranch() {
        List<String> emptyGenres = new ArrayList<>();
        AnimeList list = new AnimeList();
        Anime unrated = new Anime("Unrated", emptyGenres, 10, "Plan to Watch", "", 1, 2024); // rating -1.0
        Anime invalidNegative = new Anime("Negative", emptyGenres, 10, "Plan to Watch", "", 2, 2024);
        invalidNegative.setRating(-2.0); // violates requires clause, but triggers the branch

        list.addAnime(unrated);
        list.addAnime(invalidNegative);

        List<Anime> sorted = list.sortByRating();
        assertEquals(2, sorted.size());
        assertEquals(invalidNegative, sorted.get(0));
        assertEquals(unrated, sorted.get(1));
    }

    @Test
    public void testCalculateFavoriteGenreWithEmptyGenres() {
        AnimeList list = new AnimeList();
        Anime emptyGenreAnime = new Anime("No Genre", new ArrayList<>(), 10, "Watching", "", 1, 2024);
        emptyGenreAnime.setCurrentEpisodeWatched(5);
        emptyGenreAnime.setRating(8.0);
        list.addAnime(emptyGenreAnime);
        assertNull(list.calculateFavoriteGenre());
    }

    @Test
    public void testCalculateFavoriteGenreAllUnrated() {
        List<String> genres = new ArrayList<>();
        genres.add("Adventure");
        AnimeList list = new AnimeList();
        Anime anime1 = new Anime("Anime 1", genres, 10, "Watching", "", 1, 2024);
        anime1.setCurrentEpisodeWatched(5);
        // rating is -1.0
        list.addAnime(anime1);
        assertNull(list.calculateFavoriteGenre());
    }

    @Test
    public void testGetTopRecommendationsMoreThanThree() {
        AnimeList list = new AnimeList();
        List<String> genres = new ArrayList<>();
        genres.add("Adventure");
        
        Anime a1 = new Anime("A1", genres, 10, "Plan to Watch", "", 5, 2024);
        Anime a2 = new Anime("A2", genres, 10, "Plan to Watch", "", 1, 2024);
        Anime a3 = new Anime("A3", genres, 10, "Plan to Watch", "", 4, 2024);
        Anime a4 = new Anime("A4", genres, 10, "Plan to Watch", "", 2, 2024);
        Anime a5 = new Anime("A5", genres, 10, "Plan to Watch", "", 3, 2024);
        
        list.addAnime(a1);
        list.addAnime(a2);
        list.addAnime(a3);
        list.addAnime(a4);
        list.addAnime(a5);
        
        List<Anime> recs = list.getTopRecommendations("Adventure");
        assertEquals(3, recs.size());
        assertEquals(a2, recs.get(0)); // priority 1
        assertEquals(a4, recs.get(1)); // priority 2
        assertEquals(a5, recs.get(2)); // priority 3
    }

    @Test
    public void testGetTopRecommendationsNoMatch() {
        testList.addAnime(frieren);  // Fantasy, Adventure
        testList.addAnime(naruto);   // Action, Adventure

        List<Anime> recs = testList.getTopRecommendations("Romance");
        assertEquals(0, recs.size());
    }

    @Test
    public void testCalculateTotalWatchTimeEmptyList() {
        assertEquals(0, testList.calculateTotalWatchTime());
    }

    // calculateFavoriteGenre where genreScores.containsKey(genre) == true
    // i.e., two anime share a genre so the second one skips the initializing put
    @Test
    public void testCalculateFavoriteGenreSharedGenre() {
        testList.addAnime(frieren);  // Fantasy, Adventure
        testList.addAnime(naruto);   // Action, Adventure

        frieren.setCurrentEpisodeWatched(10);
        frieren.setRating(9.0);

        naruto.setCurrentEpisodeWatched(10);
        naruto.setRating(8.0);

        // returns true and the initialising put(0.0) is skipped; scores accumulate
        String favorite = testList.calculateFavoriteGenre();
        assertEquals("Adventure", favorite);
    }
}
