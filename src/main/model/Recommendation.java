package model;

import java.util.List;

public class Recommendation {
    private Anime anime;
    private double score;
    private double normalizedScore;
    private List<String> matchedGenres;
    private List<String> matchedTags;

    public Recommendation(Anime anime,
                           double score,
                           List<String> matchedGenres,
                           List<String> matchedTags) {

        this.anime = anime;
        this.score = score;
        this.normalizedScore = 0.0;
        this.matchedGenres = matchedGenres;
        this.matchedTags = matchedTags;
    }

    public Anime getAnime() {
        return anime;
    }

    public double getScore() {
        return score;
    }

    public double getNormalizedScore() {
        return normalizedScore;
    }

    public void setNormalizedScore(double normalizedScore) {
        this.normalizedScore = normalizedScore;
    }

    public List<String> getMatchedGenres() {
        return matchedGenres;
    }

    public List<String> getMatchedTags() {
        return matchedTags;
    }
}