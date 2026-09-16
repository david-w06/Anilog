package model;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Anime {
    private int id;
    private String name;
    private List<String> genre; 
    private int length;
    private String status; // "Not Watched", "Watching", "Completed", or "Plan to Watch"
    private int year;
    private String note;
    private int priority; 
    private int currentEpisodeWatched;
    private double rating; // from 0.0 to 10.0
    private String coverImage;
    private List<String> tags;


    // MODIFIES: this
    // EFFECTS: constructs an anime entry with the total episode length
    public Anime(String name, List<String> genre, int length, String status,
            String note, int priority, int year) {
        this.name = name;
        this.genre = genre;
        this.length = length;
        this.status = status;
        this.note = note;
        this.priority = priority;
        this.currentEpisodeWatched = 0;
        this.rating = -1.0;
        this.year = year;
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

    // MODIFIES: this
    // EFFECTS: sets the total episode length
    public void setLength(int length) {
        this.length = length;
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
        json.put("status", status);
        json.put("note", note);
        json.put("priority", priority);
        json.put("currentEpisodeWatched", currentEpisodeWatched);
        json.put("rating", rating);
        json.put("year", year);
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}