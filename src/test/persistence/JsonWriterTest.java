package persistence;

import model.Anime;
import model.AnimeList;
import persistence.JsonReader;
import persistence.JsonWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    private AnimeList testList;
    private Anime frieren;
    private Anime naruto;
    private JsonWriter writer;
    private JsonReader reader;

    @BeforeEach
    public void runBefore() {
        testList = new AnimeList();

        List<String> frierenGenres = new ArrayList<>();
        frierenGenres.add("Fantasy");
        frierenGenres.add("Adventure");
        frieren = new Anime("Frieren", frierenGenres,28, 1, "Not Watched", "Masterpiece", 1);

        List<String> narutoGenres = new ArrayList<>();
        narutoGenres.add("Action");
        narutoGenres.add("Adventure");
        naruto = new Anime("Naruto", narutoGenres,100, 5, "Watching", "Classic shonen", 2);

        writer = new JsonWriter("./data/testWriter.json");
        reader = new JsonReader("./data/testWriter.json");
    }

    @Test
    public void testWriterAnimeList() throws IOException {
        testList.addAnime(frieren);
        testList.addAnime(naruto);

        writer.open();
        writer.write(testList);
        writer.close();

        AnimeList returnedList = reader.read();

        assertEquals(2, returnedList.getAnimes().size());
        assertEquals("Frieren", returnedList.getAnimes().get(0).getName());
        assertEquals("Naruto", returnedList.getAnimes().get(1).getName());
    }

    @Test
    public void testWriterEmptyAnimeList() throws IOException {
        writer.open();
        writer.write(testList);
        writer.close();

        AnimeList returnedList = reader.read();

        assertEquals(0, returnedList.getAnimes().size());
    }

    @Test
    public void testWriterInvalidFile() {
        JsonWriter badWriter = new JsonWriter("./data/\0invalid:file.json");
        assertThrows(IOException.class, () -> badWriter.open());
    }
}