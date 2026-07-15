package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnimeTest {
    private Anime testAnime;
    private List<String> defaultGenres;

    @BeforeEach
    public void runBefore() {
        defaultGenres = new ArrayList<>();
        defaultGenres.add("Adventure");
        defaultGenres.add("Fantasy");
        
        testAnime = new Anime("Frieren", defaultGenres, 28, 1, "Watched", "Masterpiece", 1);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Frieren", testAnime.getName());
        assertEquals(defaultGenres, testAnime.getGenre());
        assertEquals(28, testAnime.getLength());
        assertEquals(1, testAnime.getSeasons());
        assertEquals("Watched", testAnime.getStatus());
        assertEquals("Masterpiece", testAnime.getNote());
        assertEquals(1, testAnime.getPriority());
        assertEquals(0, testAnime.getCurrentEpisodeWatched());
        assertEquals(-1.0, testAnime.getRating(), 0.001);
    }

    @Test
    public void testSetSeasons() {
        testAnime.setSeasons(3);
        assertEquals(3, testAnime.getSeasons());
    }

    @Test
    public void testSetRatingValid() {
        testAnime.setRating(8.5);
        assertEquals(8.5, testAnime.getRating(), 0.001); //Include a delta for floating point error
        
        testAnime.setRating(0.0);
        assertEquals(0.0, testAnime.getRating(), 0.001);
        
        testAnime.setRating(10.0);
        assertEquals(10.0, testAnime.getRating(), 0.001);
    }

    @Test
    public void testSetNote() {
        testAnime.setNote("I loved this");
        assertEquals("I loved this", testAnime.getNote()); 
    }

    @Test
    public void testSetStatus() {
        testAnime.setStatus("Watching");
        assertEquals("Watching", testAnime.getStatus());
        
        testAnime.setStatus("Finished");
        assertEquals("Finished", testAnime.getStatus());

        testAnime.setStatus("Not Watched");
        assertEquals("Not Watched", testAnime.getStatus());
    }

    @Test
    public void testSetCurrentEpisodeWatched() {
        testAnime.setCurrentEpisodeWatched(15);
        assertEquals(15, testAnime.getCurrentEpisodeWatched());

        // Test auto flip status
        testAnime.setCurrentEpisodeWatched(28);
        assertEquals(28, testAnime.getCurrentEpisodeWatched());
        assertEquals("Completed", testAnime.getStatus());

        testAnime.setCurrentEpisodeWatched(0);
        assertEquals(0, testAnime.getCurrentEpisodeWatched());
        assertEquals("Not Watched", testAnime.getStatus());
    }

    @Test
    public void testSetPriority() {
        testAnime.setPriority(3);
        assertEquals(3, testAnime.getPriority());
        testAnime.setPriority(1);
        assertEquals(1, testAnime.getPriority());
    }
}

