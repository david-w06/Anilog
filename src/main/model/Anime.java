package model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Anime {
    private String name;
    private List<String> genre; 
    private int length;
    private int seasons;
    private List<Integer> seasonEpisodeCounts;
    private String status; // "Not Watched", "Watching", "Completed", or "Plan to Watch"
    private int year;
    private String note;
    private int priority; 
    private int currentEpisodeWatched;
    private double rating; // from 0.0 to 10.0

    // REQUIRES: length > 0, seasons > 0
    // MODIFIES: this
    // EFFECTS: constructs an anime entry with default status "Not Watched", 
    //          currentEpisodeWatched set to 0, and rating set to unrated (-1.0)
    public Anime(String name, List<String> genre, int length, int seasons, String status, String note, int priority, int year) {
        this.name = name;
        this.genre = genre;
        this.seasons = seasons <= 0 ? 1 : seasons;
        this.seasonEpisodeCounts = new ArrayList<>();
        distributeLengthToSeasons(length, this.seasons);
        this.status = status;
        this.note = note;
        this.priority = priority;
        this.currentEpisodeWatched = 0;
        this.rating = -1.0; // -1.0 acts as our unrated sentinel value
        this.year = year;
    }

    // REQUIRES: seasons > 0
    // MODIFIES: this
    // EFFECTS: constructs an anime entry with explicit season episode counts
    public Anime(String name, List<String> genre, List<Integer> seasonEpisodeCounts, String status, String note, int priority, int year) {
        this.name = name;
        this.genre = genre;
        setSeasonEpisodeCounts(seasonEpisodeCounts);
        this.status = status;
        this.note = note;
        this.priority = priority;
        this.currentEpisodeWatched = 0;
        this.rating = -1.0;
        this.year = year;
    }

    private void distributeLengthToSeasons(int totalLength, int numSeasons) {
        this.seasonEpisodeCounts = new ArrayList<>();
        if (numSeasons <= 0) {
            numSeasons = 1;
        }
        int base = totalLength / numSeasons;
        int rem = totalLength % numSeasons;
        for (int i = 0; i < numSeasons; i++) {
            this.seasonEpisodeCounts.add(base + (i < rem ? 1 : 0));
        }
        this.length = totalLength;
        this.seasons = numSeasons;
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

    public List<Integer> getSeasonEpisodeCounts() {
        return new ArrayList<>(seasonEpisodeCounts);
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

    public int getYear() {
        return year;
    }

// ================= Mutators =================

    // REQUIRES: season > 0
    // MODIFIES: this
    // EFFECTS: sets a season number for the anime, updating seasonEpisodeCounts accordingly
    public void setSeasons(int season) {
        if (season <= 0) {
            return;
        }
        if (this.seasons != season) {
            distributeLengthToSeasons(this.length, season);
            EventLog.getInstance().logEvent(
                new Event("Updated season count for " + name + " to " + season + ".")
            );
        }
    }

    // REQUIRES: counts != null && !counts.isEmpty()
    // MODIFIES: this
    // EFFECTS: sets episode count for each season individually and updates total length and seasons count
    public void setSeasonEpisodeCounts(List<Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            return;
        }
        this.seasonEpisodeCounts = new ArrayList<>();
        int sum = 0;
        for (int count : counts) {
            int validCount = Math.max(0, count);
            this.seasonEpisodeCounts.add(validCount);
            sum += validCount;
        }
        this.seasons = this.seasonEpisodeCounts.size();
        this.length = sum;
        EventLog.getInstance().logEvent(
            new Event("Updated season episode counts for " + name + ".")
        );
    }

    // REQUIRES: status is one of "Not Watched", "Watching", "Completed", or "Plan to Watch"
    // MODIFIES: this
    // EFFECTS: sets a new watch status for the anime
    public void setStatus(String status) {
        if (!this.status.equals(status)) {
            this.status = status;
            EventLog.getInstance().logEvent(
                new Event("Updated status for " + name + " to " + status + ".")
            );
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the personal notes for the anime
    public void setNote(String note) {
        if (!this.note.equals(note)) {
            this.note = note;
            EventLog.getInstance().logEvent(
                new Event("Updated note for " + name + ".")
            );
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the priority ranking for the anime
    public void setPriority(int priority) {
        if (this.priority != priority) {
            this.priority = priority;
            EventLog.getInstance().logEvent(
                new Event("Updated priority for " + name + " to " + priority + ".")
            );
        }
    }

    // REQUIRES: 0 <= episode <= length
    // MODIFIES: this
    // EFFECTS: updates the current episode progress tracker, if current == total episode, flip status to Completed.
    // Flips to Not Watched if current ep is 0, user can still change status on its own.
    public void setCurrentEpisodeWatched(int episode) {
        if (this.currentEpisodeWatched != episode) {
            this.currentEpisodeWatched = episode;
            if (this.currentEpisodeWatched == length) {
                status = "Completed"; 
            }
            if (this.currentEpisodeWatched == 0) {
                status = "Not Watched"; 
            }
            
            EventLog.getInstance().logEvent(
                new Event("Updated episode progress for " + name + " to episode " + episode + ".")
            );
        }
    }

    // REQUIRES: (rating >= 0.0 && rating <= 10.0) || rating == -1.0
    // MODIFIES: this
    // EFFECTS: updates the rating for the anime
    public void setRating(double rating) {
        if (this.rating != rating) {
            this.rating = rating;
            EventLog.getInstance().logEvent(
                new Event("Updated rating for " + name + " to " + rating + "/10.0.")
            );
        }
    }

    // for data persistence
    // EFFECTS: returns a JSONObject representing this anime, containing its attributes
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("genres", genre);
        json.put("length", length);
        json.put("seasons", seasons);
        json.put("seasonEpisodeCounts", seasonEpisodeCounts);
        json.put("status", status);
        json.put("note", note);
        json.put("priority", priority);
        json.put("currentEpisodeWatched", currentEpisodeWatched);
        json.put("rating", rating);
        json.put("year", year);
        return json;
    }
}