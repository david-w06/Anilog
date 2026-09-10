package persistence;

import model.AnimeList;
import org.json.JSONObject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// represents a writer that writes JSON representations of anime list data to a target file
public class JsonWriter {

    private static final int TAB = 4;
    private String destination;
    private PrintWriter writer;

    // EFFECTS: constructs writer that will write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }
    
    // EFFECTS: constructs writer that will write to destination file
    public void write(AnimeList list) {
        JSONObject json = list.toJson();
        saveToFile(json.toString(TAB));

    }

    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }

    // MODIFIES: this
    // EFFECTS: opens the writer, creating parent directories if needed
    public void open() throws FileNotFoundException {
        java.io.File file = new java.io.File(destination);
        java.io.File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        writer = new PrintWriter(file);
    }

    // EFFECTS: closes writer
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

}