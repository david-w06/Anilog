package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Anime;
import model.Recommendation;

public class RecommendationService {

    // EFFECTS: constructs a recommendation service
    public RecommendationService() {
    }

    // EFFECTS: returns recommendations for the user based on
    //          their anime preferences and the given candidates
    public List<Recommendation> getRecommendations(
            List<Anime> userAnime,
            List<Anime> candidates) {

        if (userAnime == null || candidates == null) {
            return new ArrayList<>();
        }

        Map<String, Double> genrePreferences =
                buildGenrePreferences(userAnime);

        Map<String, Double> tagPreferences =
                buildTagPreferences(userAnime);

        List<Recommendation> recommendations = new ArrayList<>();

        for (Anime candidate : candidates) {

            if (isInWatchList(candidate, userAnime)) {
                continue;
            }

            Recommendation recommendation =
                    createRecommendation(
                            candidate,
                            genrePreferences,
                            tagPreferences);

            recommendations.add(recommendation);
        }

        recommendations.sort(
                Comparator.comparingDouble(
                        Recommendation::getScore).reversed());

        normalizeScores(recommendations);

        return recommendations;
    }

    // EFFECTS: creates a recommendation for the given anime
    private Recommendation createRecommendation(
            Anime anime,
            Map<String, Double> genrePreferences,
            Map<String, Double> tagPreferences) {

        double score = calculateScore(
                anime,
                genrePreferences,
                tagPreferences);

        List<String> matchedGenres =
                findMatchedGenres(anime, genrePreferences);

        List<String> matchedTags =
                findMatchedTags(anime, tagPreferences);

        return new Recommendation(
                anime,
                score,
                matchedGenres,
                matchedTags);
    }

    // EFFECTS: returns a map containing the user's genre preferences
    private Map<String, Double> buildGenrePreferences(
            List<Anime> animeList) {

        Map<String, Double> preferences = new HashMap<>();

        for (Anime anime : animeList) {

            double weight = calculatePreferenceWeight(anime);

            if (weight == 0) {
                continue;
            }

            for (String genre : anime.getGenre()) {
                double currentWeight =
                        preferences.getOrDefault(genre, 0.0);

                preferences.put(
                        genre,
                        currentWeight + weight);
            }
        }

        return preferences;
    }

    // EFFECTS: returns a map containing the user's tag preferences
    private Map<String, Double> buildTagPreferences(
            List<Anime> animeList) {

        Map<String, Double> preferences = new HashMap<>();

        for (Anime anime : animeList) {

            double weight = calculatePreferenceWeight(anime);

            if (weight == 0) {
                continue;
            }

            for (String tag : anime.getTags()) {
                double currentWeight =
                        preferences.getOrDefault(tag, 0.0);

                preferences.put(
                        tag,
                        currentWeight + weight);
            }
        }

        return preferences;
    }

    // EFFECTS: calculates how strongly an anime should influence
    //          the user's preference profile
    private double calculatePreferenceWeight(Anime anime) {

        if (anime.getRating() < 0) {
            return 0;
        }

        double statusMultiplier;

        if (anime.getStatus().equals("Completed")) {
            statusMultiplier = 1.0;
        } else if (anime.getStatus().equals("Watching")) {
            statusMultiplier = 0.8;
        } else if (anime.getStatus().equals("Plan to Watch")) {
            statusMultiplier = 0.3;
        } else {
            statusMultiplier = 0.0;
        }

        return (anime.getRating() - 5.0) * statusMultiplier;
    }

    // EFFECTS: calculates how well an anime matches the user's preferences
    private double calculateScore(
            Anime anime,
            Map<String, Double> genrePreferences,
            Map<String, Double> tagPreferences) {

        double genreScore = 0.0;
        double tagScore = 0.0;

        for (String genre : anime.getGenre()) {
            genreScore +=
                    genrePreferences.getOrDefault(genre, 0.0);
        }

        for (String tag : anime.getTags()) {
            tagScore +=
                    tagPreferences.getOrDefault(tag, 0.0);
        }

        return genreScore + (2.0 * tagScore);
    }

    // EFFECTS: returns the genres of the anime that match
    //          positive user genre preferences
    private List<String> findMatchedGenres(
            Anime anime,
            Map<String, Double> genrePreferences) {

        List<String> matches = new ArrayList<>();

        for (String genre : anime.getGenre()) {

            double preference =
                    genrePreferences.getOrDefault(genre, 0.0);

            if (preference > 0) {
                matches.add(genre);
            }
        }

        return matches;
    }

    // EFFECTS: returns the tags of the anime that match
    //          positive user tag preferences
    private List<String> findMatchedTags(
            Anime anime,
            Map<String, Double> tagPreferences) {

        List<String> matches = new ArrayList<>();

        for (String tag : anime.getTags()) {

            double preference =
                    tagPreferences.getOrDefault(tag, 0.0);

            if (preference > 0) {
                matches.add(tag);
            }
        }

        return matches;
    }

    // EFFECTS: returns true if the anime is already in the user's watchlist
    private boolean isInWatchList(
            Anime candidate,
            List<Anime> userAnime) {

        for (Anime anime : userAnime) {

            if (candidate.getAniListId() != 0
                    && anime.getAniListId() == candidate.getAniListId()) {
                return true;
            }

            if (anime.getName().equalsIgnoreCase(
                    candidate.getName())) {

                return true;
            }
        }

        return false;
    }

    private void normalizeScores(
        List<Recommendation> recommendations) {

        if (recommendations.isEmpty()) {
            return;
        }

        double minimum =
                recommendations.get(
                        recommendations.size() - 1).getScore();

        double maximum =
                recommendations.get(0).getScore();

        if (maximum == minimum) {
            for (Recommendation recommendation
                    : recommendations) {

                recommendation.setNormalizedScore(100.0);
            }

            return;
        }

        for (Recommendation recommendation
                : recommendations) {

            double normalized =
                    (recommendation.getScore() - minimum)
                    / (maximum - minimum)
                    * 100.0;

            recommendation.setNormalizedScore(normalized);
        }
    }
}