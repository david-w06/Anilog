package persistence;

import model.AnimeList;
import java.io.IOException;

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
        return new AnimeList(); // stub
    }
}