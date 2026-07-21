package persistence;

import model.AnimeList;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// represents a writer that writes JSON representations of anime list data to a target file
public class JsonWriter {

    private String destination;
    private PrintWriter writer;

    // EFFECTS: constructs writer that will write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }
    
    // EFFECTS: constructs writer that will write to destination file
    public void write(AnimeList list){
        //stub
    }

    // MODIFIES: this
    // EFFECTS: opens the writer
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(destination);
    }

    // EFFECTS: closes writer
    public void close(){
        if (writer != null) {
            writer.close();
        }
    }

}