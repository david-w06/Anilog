package persistence;

import model.Anime;
import model.AnimeList;
import persistence.JsonReader;
import persistence.JsonWriter;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {

    @Test
    public void testReaderAnimeList() throws IOException {
        JsonReader reader =
                new JsonReader("./data/testReaderGeneralAnimeList.json");

        AnimeList list = reader.read();

        assertEquals(2, list.getAnimes().size());

        Anime frieren = list.getAnimes().get(0);
        assertEquals("Frieren", frieren.getName());
        assertEquals(28, frieren.getLength());
        assertEquals(1, frieren.getSeasons());
        assertEquals("Completed", frieren.getStatus());
        assertEquals("Masterpiece", frieren.getNote());
        assertEquals(1, frieren.getPriority());
        assertEquals(28, frieren.getCurrentEpisodeWatched());
        assertEquals(9.5, frieren.getRating(), 0.001);

        Anime naruto = list.getAnimes().get(1);
        assertEquals("Naruto", naruto.getName());
        assertEquals(100, naruto.getLength());
        assertEquals(5, naruto.getSeasons());
        assertEquals("Watching", naruto.getStatus());
        assertEquals("Classic shonen", naruto.getNote());
        assertEquals(2, naruto.getPriority());
        assertEquals(50, naruto.getCurrentEpisodeWatched());
        assertEquals(8.0, naruto.getRating(), 0.001);
    }

    @Test
    public void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");

        assertThrows(IOException.class, () -> {
            reader.read();
        });
    }

    @Test
    public void testReaderEmptyAnimeList() throws IOException {
        JsonReader reader =
                new JsonReader("./data/testReaderEmptyAnimeList.json");

        AnimeList list = reader.read();

        assertEquals(0, list.getAnimes().size());
    }

}