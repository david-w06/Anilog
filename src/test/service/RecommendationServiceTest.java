package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Anime;
import model.Recommendation;

public class RecommendationServiceTest {

    private final RecommendationService service = new RecommendationService();

    @Test
    public void testWeightedGenreAndTagScore() {
        Anime watched = anime("Loved", List.of("Action"), List.of("Shounen"),
                "Completed", 9.0);
        Anime candidate = anime("Candidate", List.of("Action"), List.of("Shounen"),
                "Not Watched", -1.0);

        List<Recommendation> results = service.getRecommendations(
                List.of(watched), List.of(candidate));

        assertEquals(12.0, results.get(0).getScore(), 0.001);
        assertEquals(List.of("Action"), results.get(0).getMatchedGenres());
        assertEquals(List.of("Shounen"), results.get(0).getMatchedTags());
    }

    @Test
    public void testStatusMultipliersAndNegativePreferences() {
        Anime completed = anime("Completed", List.of("Action"), List.of(),
                "Completed", 9.0);
        Anime watching = anime("Watching", List.of("Comedy"), List.of(),
                "Watching", 1.0);
        Anime plan = anime("Plan", List.of("Drama"), List.of(),
                "Plan to Watch", 9.0);

        Anime action = anime("Action candidate", List.of("Action"), List.of(),
                "Not Watched", -1.0);
        Anime comedy = anime("Comedy candidate", List.of("Comedy"), List.of(),
                "Not Watched", -1.0);
        Anime drama = anime("Drama candidate", List.of("Drama"), List.of(),
                "Not Watched", -1.0);

        List<Recommendation> results = service.getRecommendations(
                Arrays.asList(completed, watching, plan),
                Arrays.asList(action, comedy, drama));

        assertEquals(4.0, results.get(0).getScore(), 0.001);
        assertEquals("Action candidate", results.get(0).getAnime().getName());
        assertEquals(1.2, results.get(1).getScore(), 0.001);
        assertEquals("Drama candidate", results.get(1).getAnime().getName());
        assertEquals(-3.2, results.get(2).getScore(), 0.001);
        assertEquals("Comedy candidate", results.get(2).getAnime().getName());
    }

    @Test
    public void testOnlyPositiveMatchesAreExplained() {
        Anime liked = anime("Liked", List.of("Action"), List.of("Hero"),
                "Completed", 8.0);
        Anime disliked = anime("Disliked", List.of("Action"), List.of("Villain"),
                "Completed", 2.0);
        Anime candidate = anime("Mixed", List.of("Action"),
                List.of("Hero", "Villain"), "Not Watched", -1.0);

        Recommendation result = service.getRecommendations(
                Arrays.asList(liked, disliked), List.of(candidate)).get(0);

        assertEquals(0.0, result.getScore(), 0.001);
        assertTrue(result.getMatchedGenres().isEmpty());
        assertEquals(List.of("Hero"), result.getMatchedTags());
    }

    @Test
    public void testExistingCandidatesAreRemovedByAniListId() {
        Anime inWatchlist = anime("Old title", List.of("Action"), List.of(),
                "Completed", 10.0);
        inWatchlist.setAniListId(123);

        Anime sameAniListEntry = anime("Renamed title", List.of("Action"), List.of(),
                "Not Watched", -1.0);
        sameAniListEntry.setAniListId(123);

        Anime differentEntry = anime("New title", List.of("Action"), List.of(),
                "Not Watched", -1.0);

        List<Recommendation> results = service.getRecommendations(
                List.of(inWatchlist), Arrays.asList(sameAniListEntry, differentEntry));

        assertEquals(1, results.size());
        assertEquals("New title", results.get(0).getAnime().getName());
    }

    @Test
    public void testNullInputsReturnEmptyResults() {
        assertTrue(service.getRecommendations(null, List.of()).isEmpty());
        assertTrue(service.getRecommendations(List.of(), null).isEmpty());
    }

    private Anime anime(String name, List<String> genres, List<String> tags,
            String status, double rating) {
        Anime anime = new Anime(name, new ArrayList<>(genres), 12,
                status, "", 1, 2026);
        anime.setTags(new ArrayList<>(tags));
        anime.setRating(rating);
        return anime;
    }
}
