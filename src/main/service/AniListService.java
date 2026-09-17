package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

import model.Anime;

import org.json.JSONArray;

public class AniListService {

    private static final String API_URL = "https://graphql.anilist.co";

    private HttpClient client;

    public AniListService() {
        client = HttpClient.newHttpClient();
    }

   public List<Anime> searchAnime(String search) {
        String query = """
            query ($search: String!) {
                Page {
                    media(search: $search, type: ANIME) {
                        id
                        title {
                            romaji
                            english
                            native
                        }
                        genres
                        tags {
                            name
                            rank
                        }
                        episodes
                        startDate {
                            year
                        }
                        coverImage {
                            large
                        }
                    }
                }
            }
            """;

        JSONObject variables = new JSONObject();
        variables.put("search", search);

        JSONObject requestBody = new JSONObject();
        requestBody.put("query", query);
        requestBody.put("variables", variables);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject responseJson = new JSONObject(response.body());

            JSONObject data = responseJson.getJSONObject("data");
            JSONObject page = data.getJSONObject("Page");
            JSONArray media = page.getJSONArray("media");

            List<Anime> results = new ArrayList<>();

            for (int i = 0; i < media.length(); i++) {
                JSONObject animeJson = media.getJSONObject(i);

                Anime anime = parseAnime(animeJson);

                results.add(anime);
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // EFFECTS: retrieves a list of popular anime from AniList
    public List<Anime> getPopularAnime() {
        String query = """
            query {
                Page(perPage: 50) {
                    media(
                        type: ANIME,
                        sort: POPULARITY_DESC
                    ) {
                        id
                        title {
                            romaji
                            english
                            native
                        }
                        genres
                        tags {
                            name
                            rank
                        }
                        episodes
                        startDate {
                            year
                        }
                        coverImage {
                            large
                        }
                    }
                }
            }
                """;

        JSONObject requestBody = new JSONObject();
        requestBody.put("query", query);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            JSONObject responseJson =
                    new JSONObject(response.body());

                if (responseJson.has("errors")) {
                throw new IllegalStateException(
                    "AniList returned errors: " + responseJson.getJSONArray("errors"));
                }

            JSONObject data =
                    responseJson.getJSONObject("data");

            JSONObject page =
                    data.getJSONObject("Page");

            JSONArray media =
                    page.getJSONArray("media");

            List<Anime> results = new ArrayList<>();

            for (int i = 0; i < media.length(); i++) {

                JSONObject animeJson =
                        media.getJSONObject(i);

                Anime anime = parseAnime(animeJson);

                results.add(anime);
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // EFFECTS: converts an AniList media JSON object into an Anime object
    private Anime parseAnime(JSONObject animeJson) {

        int aniListId = animeJson.getInt("id");

        JSONObject title =
                animeJson.getJSONObject("title");

        String name =
                title.optString("english");

        if (name.isEmpty()) {
            name = title.optString("romaji");
        }

        JSONArray genresJson =
                animeJson.getJSONArray("genres");

        List<String> genres = new ArrayList<>();

        for (int i = 0; i < genresJson.length(); i++) {
            genres.add(genresJson.getString(i));
        }

        JSONArray tagsJson =
                animeJson.getJSONArray("tags");

        List<String> tags = new ArrayList<>();

        for (int i = 0; i < tagsJson.length(); i++) {
            JSONObject tag =
                    tagsJson.getJSONObject(i);

            tags.add(tag.getString("name"));
        }

        int length =
                animeJson.optInt("episodes", 0);

        JSONObject startDate =
                animeJson.optJSONObject("startDate");

        int year = startDate == null
                ? 0
                : startDate.optInt("year", 0);

        String coverImage = "";

        if (animeJson.has("coverImage")) {
            JSONObject cover =
                    animeJson.getJSONObject("coverImage");

            coverImage =
                    cover.optString("large");
        }

        Anime anime = new Anime(
                name,
                genres,
                length,
                "Not Watched",
                "",
                0,
                year
        );

        anime.setTags(tags);
        anime.setCoverImage(coverImage);
        anime.setAniListId(aniListId);

        return anime;
    }
}