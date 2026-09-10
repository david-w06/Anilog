package persistence;

import model.Anime;
import model.AnimeList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

// represents a reader that reads anime list data from JSON data stored in a file.
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads AnimeList from file and returns it;
    // throws IOException if an error occurs reading data from file
    public AnimeList read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseAnimeList(jsonObject);
    }

    // EFFECTS: reads source file as a string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        // access file path using UTF_8 character encoding, reading each line (s) and adding it to our string builder
        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    //MODIFIES: list
    // EFFECTS: parses AnimeList from JSONObject and returns it
    private AnimeList parseAnimeList(JSONObject jsonObject) {
        AnimeList list = new AnimeList();
        addAnimes(list, jsonObject);
        return list;
    }

    // MODIFIES: list
    // EFFECTS: parses animes from JSONObject and adds them to list
    private void addAnimes(AnimeList list, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("animes");
        for (Object json : jsonArray) {
            JSONObject nextAnime = (JSONObject) json;
            addAnime(list, nextAnime);
        }
    }

    // MODIFIES: list
    // EFFECTS: parses individual Anime from JSONObject and adds it to list
    private void addAnime(AnimeList list, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        
        JSONArray jsonGenres = jsonObject.getJSONArray("genres");
        List<String> genres = new ArrayList<>();
        for (Object g : jsonGenres) {
            genres.add((String) g);
        }

        int length = jsonObject.getInt("length");
        int seasons = jsonObject.getInt("seasons");
        String status = jsonObject.getString("status");
        String note = jsonObject.getString("note");
        int priority = jsonObject.getInt("priority");
        int currentEpisodeWatched = jsonObject.getInt("currentEpisodeWatched");
        double rating = jsonObject.getDouble("rating");
        int year = jsonObject.has("year") ? jsonObject.getInt("year") : 2024;

        Anime anime;
        if (jsonObject.has("seasonEpisodeCounts")) {
            JSONArray jsonCounts = jsonObject.getJSONArray("seasonEpisodeCounts");
            List<Integer> counts = new ArrayList<>();
            for (Object c : jsonCounts) {
                counts.add((Integer) c);
            }
            anime = new Anime(name, genres, counts, status, note, priority, year);
        } else {
            anime = new Anime(name, genres, length, seasons, status, note, priority, year);
        }
        anime.setCurrentEpisodeWatched(currentEpisodeWatched);
        anime.setRating(rating);
        list.addAnime(anime);
    }
}