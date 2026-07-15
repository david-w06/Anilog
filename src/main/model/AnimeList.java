package model;

import java.util.ArrayList;
import java.util.List;

public class AnimeList {
    private List<Anime> animes;

    // EFFECTS: constructs an empty anime list
    public AnimeList() {
        this.animes = new ArrayList<>();
    }

    // ================= Core Operations =================

    // MODIFIES: this
    // EFFECTS: adds an anime to the watchlist
    public void addAnime(Anime anime) {
        // Stub
    }

    // MODIFIES: this
    // EFFECTS: removes the specified anime from the watchlist if present
    public void removeAnime(Anime anime) {
        // Stub
    }
    public List<Anime> getAnimes() {
        return null; // Stub
    }

    // ================= Filtering Operations =================

    // EFFECTS: returns a list containing only the anime that feature the specified targetGenre
    public List<Anime> filterByGenre(String targetGenre) {
        return null; // Stub
    }

    // REQUIRES: targetStatus is one of "Watching", "Plan to Watch", or "Completed"
    // EFFECTS: returns a list containing only the anime that match the targetStatus
    public List<Anime> filterByStatus(String targetStatus) {
        return null; // Stub
    }

    // REQUIRES: targetSeason > 0
    // EFFECTS: returns a list containing only the anime that have the matching number of seasons
    public List<Anime> filterBySeasonCount(int targetSeason) {
        return null; // Stub
    }

    // ================= Sorting Operations =================
    
    // EFFECTS: returns a new list of anime sorted alphabetically by name
    public List<Anime> sortByName() {
        return null; // Stub
    }

    // EFFECTS: returns a new list of anime sorted by rating from highest to lowest;
    //          unrated anime (rating == -1.0) are placed at the bottom
    public List<Anime> sortByRating() {
        return null; // Stub
    }

    // ================= Analytical Methods =================

    // EFFECTS: returns the total estimated time spent watching completed/active anime in hours
    public int calculateTotalWatchTime() {
        return 0; // Stub
    }

    // EFFECTS: analyzes watch time and ratings across genres and returns the string name of the top genre
    public String calculateFavoriteGenre() {
        return null; // Stub
    }

    // REQUIRES: priorityWeight > 0, genreWeight > 0
    // EFFECTS: runs a rule-based algorithm over "Plan to Watch" anime, 
    //          ranking them by priority and genre preferences, and returns the top 3 recommendations
    public List<Anime> getTopRecommendations(String favoriteGenre, int priorityWeight, int genreWeight) {
        return null; // Stub
    }
}