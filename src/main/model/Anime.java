package model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class Anime {
    private String name;
    private List<String> genre; 
    private int length;
    private int seasons;
    private String status; // "Not Watched", "Watching", "Completed", or "Plan to Watch"
    private String note;
    private int priority; 
    private int currentEpisodeWatched;
    private double rating; // from 0.0 to 10.0

    // REQUIRES: length > 0, seasons > 0
    // MODIFIES: this
    // EFFECTS: constructs an anime entry with default status "Not Watched", 
    //          currentEpisodeWatched set to 0, and rating set to unrated (-1.0)
    public Anime(String name, List<String> genre, int length, int seasons, String status, String note, int priority) {
        this.name = name;
        this.genre = genre;
        this.length = length;
        this.seasons = seasons;
        this.status = status;
        this.note = note;
        this.priority = priority;
        this.currentEpisodeWatched = 0;
        this.rating = -1.0; // -1.0 acts as our unrated sentinel value
    }

    // ================= Accessors =================

    public String getName() {
        return name;
    }

    public List<String> getGenre() {
        return genre;
    }

    public int getLength() {
        return length;
    }

    public int getSeasons() {
        return seasons;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public int getPriority() {
        return priority;
    }

    public int getCurrentEpisodeWatched() {
        return currentEpisodeWatched;
    }

    // EFFECTS: returns the user rating, or -1.0 if unrated
    public double getRating() {
        return rating;
    }

    // ================= Mutators =================

    // REQUIRES: season > 0
    // MODIFIES: this
    // EFFECTS: sets a season number for the anime
    public void setSeasons(int season) {
        this.seasons = season;
    }

    // REQUIRES: status is one of "Not Watched", "Watching", "Completed", or "Plan to Watch"
    // MODIFIES: this
    // EFFECTS: sets a new watch status for the anime
    public void setStatus(String status) {
        this.status = status;
    }

    // MODIFIES: this
    // EFFECTS: updates the personal notes for the anime
    public void setNote(String note) {
        this.note = note;
    }

    // MODIFIES: this
    // EFFECTS: updates the priority ranking for the anime
    public void setPriority(int priority) {
        this.priority = priority;
    }

    // REQUIRES: 0 <= episode <= length
    // MODIFIES: this
    // EFFECTS: updates the current episode progress tracker, if current == total episode, flip status to Completed.
    // Flips to Not Watched if current ep is 0, user can still change status on its own.
    public void setCurrentEpisodeWatched(int episode) {
        this.currentEpisodeWatched = episode;
        if (this.currentEpisodeWatched == length) {
            status = "Completed"; 
        }
        if (this.currentEpisodeWatched == 0) {
            status = "Not Watched"; 
        }
    }

    // REQUIRES: (rating >= 0.0 && rating <= 10.0) || rating == -1.0
    // MODIFIES: this
    // EFFECTS: updates the rating for the anime
    public void setRating(double rating) {
        this.rating = rating;
    }

    // EFFECTS: returns a JSONObject representing this anime, containing its attributes
    public JSONObject toJson(){
        return null; //stub
    }
}