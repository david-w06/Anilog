package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

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
        animes.add(anime);
    }

    // MODIFIES: this
    // EFFECTS: removes the specified anime from the watchlist if present
    public void removeAnime(Anime anime) {
        animes.remove(anime);
    }

    // MODIFIES: this
    // EFFECTS: returns the list of anime stored
    public List<Anime> getAnimes() {
        return animes;
    }

    // ================= Filtering Operations =================

    // EFFECTS: returns a list containing only the anime that feature the specified targetGenre
    public List<Anime> filterByGenre(String targetGenre) {
        List<Anime> filteredList = new ArrayList<>();
        
        for (Anime anime : animes) {
            if (anime.getGenre().contains(targetGenre)) {
                filteredList.add(anime);
            }
        } 
        return filteredList;
    }

    // REQUIRES: targetStatus is one of "Watching", "Plan to Watch", or "Completed"
    // EFFECTS: returns a list containing only the anime that match the targetStatus
    public List<Anime> filterByStatus(String targetStatus) {
        List<Anime> filteredList = new ArrayList<>();
        
        for (Anime anime : animes) {
            if (anime.getStatus().equals(targetStatus)) {
                filteredList.add(anime);
            }
        } 
        return filteredList;
    }

    // REQUIRES: targetSeason > 0
    // EFFECTS: returns a list containing only the anime that have the matching number of seasons
    public List<Anime> filterBySeasonCount(int targetSeason) {
        List<Anime> filteredList = new ArrayList<>();
       
        for (Anime anime : animes) {
            if (anime.getSeasons() == targetSeason) {
                filteredList.add(anime);
            }
        } 
        return filteredList;
    }

    // ================= Sorting Operations =================
    
    // EFFECTS: returns a new list of anime sorted alphabetically by name
    public List<Anime> sortByName() {
        List<Anime> sortedList = new ArrayList<>(animes);
        // selection sort algorithm
        // use a nested loop to find the smallest name among the indexes we haven't sorted
        for (int i = 0; i < sortedList.size(); i++) {
            int smallestIndex = i;

            for (int j = i + 1; j < sortedList.size(); j++) {
                if (sortedList.get(j).getName()
                        .compareTo(sortedList.get(smallestIndex).getName()) < 0) {
                    smallestIndex = j;
                }
            }

            // swap the current and early-most index with the smallest name
            Anime temp = sortedList.get(i);
            sortedList.set(i, sortedList.get(smallestIndex));
            sortedList.set(smallestIndex, temp);
        }
        return sortedList;
    }

    // EFFECTS: returns a new list of anime sorted by rating from highest to lowest;
    //          unrated anime (rating == -1.0) are placed at the bottom
    public List<Anime> sortByRating() {
        List<Anime> sortedList = new ArrayList<>(animes);
        // selection sort again
        for (int i = 0; i < sortedList.size(); i++) {
            int highestIndex = i;

            for (int j = i + 1; j < sortedList.size(); j++) {
                double currentRating = sortedList.get(j).getRating();
                double highestRating = sortedList.get(highestIndex).getRating();

                if (currentRating > highestRating) {
                    highestIndex = j;
                } else if (highestRating == -1.0 && currentRating != -1.0) {
                    highestIndex = j;
                }
            }
            //swap the highest among the remainings to the front
            Anime temp = sortedList.get(i);
            sortedList.set(i, sortedList.get(highestIndex));
            sortedList.set(highestIndex, temp);
        }

        return sortedList;
    }


    //EFFECTS: return a new list of anime sorted by their status in the order below
    public List<Anime> sortByStatus() { // Watching -> Plan to Watch -> Completed -> Not Watched
        List<Anime> sorted = new ArrayList<>(animes);

        // Same selection sort algorithm that compares based on the given order
        for (int i = 0; i < sorted.size(); i++) {
            int lowest = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                String current = sorted.get(j).getStatus();
                String best = sorted.get(lowest).getStatus();

                if ((current.equals("Watching") && !best.equals("Watching"))
                        || (current.equals("Plan to Watch") && best.equals("Completed"))
                        || (current.equals("Plan to Watch") && best.equals("Not Watched"))
                        || (current.equals("Completed") && best.equals("Not Watched"))) {
                    lowest = j;
                }
            }
            Anime temp = sorted.get(i);
            sorted.set(i, sorted.get(lowest));
            sorted.set(lowest, temp);
        }
        return sorted;
    }

    // ================= Analytical Methods =================

    // EFFECTS: returns the total estimated time spent watching completed/active anime in hours
    public int calculateTotalWatchTime() {
        int time = 0;
        for (Anime anime : animes) {
            time += anime.getCurrentEpisodeWatched() * 24 / 60;
        }
        return time;
    }

    // EFFECTS: analyzes watch time and ratings across genres and returns the string name of the top genre
    public String calculateFavoriteGenre() {
        if (animes.isEmpty()) {
            return null;
        }
        Map<String, Double> genreScores = new HashMap<>(); // using a hashmap to store all genres and their scores
        for (Anime anime : animes) {
            // score = time watched*rating
            double score = anime.getCurrentEpisodeWatched() * anime.getRating(); 
            for (String genre : anime.getGenre()) {
                if (!genreScores.containsKey(genre)) {
                    genreScores.put(genre, 0.0); // create a pair for the genre if it doesn't exist
                }
                genreScores.put(genre, genreScores.get(genre) + score); //add the anime's score contribution to genre
            }
        }
        // compare all pairs and extract the one with the highest genereScore
        String favorite = null;
        double highest = -1;
        for (String genre : genreScores.keySet()) {
            if (genreScores.get(genre) > highest) {
                highest = genreScores.get(genre);
                favorite = genre;
            }
        }
        return favorite;
    }

    // EFFECTS: runs a algorithm over "Plan to Watch" anime, 
    //          ranking them by priority and genre preferences, and returns the top 3 recommendations
    public List<Anime> getTopRecommendations(String favoriteGenre) {
        List<Anime> recommendations = new ArrayList<>();
        // Obtaining all entries with the target genre
        for (Anime anime : animes) {
            if (anime.getGenre().contains(favoriteGenre)) {
                recommendations.add(anime);
            }
        }
        // Run selection sort over recommendation to put the top 3 priority at the beginning
        for (int i = 0; i < recommendations.size(); i++) {
            int highestIndex = i;
            for (int j = i + 1; j < recommendations.size(); j++) {
                if (recommendations.get(j).getPriority()
                        < recommendations.get(highestIndex).getPriority()) {
                    highestIndex = j;
                }
            }
            Anime temp = recommendations.get(i);
            recommendations.set(i, recommendations.get(highestIndex));
            recommendations.set(highestIndex, temp);
        }
        // Return the first 3 recommendations or the entire list if list size < 3
        List<Anime> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, recommendations.size()); i++) {
            result.add(recommendations.get(i));
        }
        return result;
    }

    // for data persistence
    // EFFECTS: returns a JSONObject representing this anime list, containing a JSONArray of JSONObjects (animes)
    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        
        for (Anime anime : animes) {
            jsonArray.put(anime.toJson());
        }
        
        json.put("animes", jsonArray);
        return json;    
    }
}