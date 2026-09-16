package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import model.Anime;

public class AnimeRepository {

    private static final String DATABASE_URL = "jdbc:sqlite:data/anilog.db";

    public AnimeRepository() {
        createDataDirectory();
        createTable();
    }

    private void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create database directory", e);
        }
    }

    private Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try (PreparedStatement statement =
                connection.prepareStatement("PRAGMA foreign_keys = ON")) {
            statement.execute();
        }

        return connection;
    }

   private void createTable() {
        String createAnimeTable = """
                CREATE TABLE IF NOT EXISTS anime (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    length INTEGER,
                    status TEXT NOT NULL,
                    rating REAL,
                    priority INTEGER,
                    notes TEXT,
                    current_episode_watched INTEGER,
                    year INTEGER
                )
                """;

        String createGenreTable = """
                CREATE TABLE IF NOT EXISTS genres (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """;

        String createAnimeGenreTable = """
                CREATE TABLE IF NOT EXISTS anime_genres (
                    anime_id INTEGER NOT NULL,
                    genre_id INTEGER NOT NULL,

                    PRIMARY KEY (anime_id, genre_id),

                    FOREIGN KEY (anime_id) REFERENCES anime(id),
                    FOREIGN KEY (genre_id) REFERENCES genres(id)
                )
                """;

        try (Connection connection = connect()) {

            try (PreparedStatement statement =
                    connection.prepareStatement(createAnimeTable)) {
                statement.executeUpdate();
            }

            ensureColumn(connection, "current_episode_watched", "INTEGER");
            ensureColumn(connection, "length", "INTEGER");
            migrateEpisodesToLength(connection);
            ensureColumn(connection, "year", "INTEGER");

            try (PreparedStatement statement =
                    connection.prepareStatement(createGenreTable)) {
                statement.executeUpdate();
            }

            try (PreparedStatement statement =
                    connection.prepareStatement(createAnimeGenreTable)) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not create database tables", e);
        }
    }

    private void ensureColumn(Connection connection, String columnName, String definition)
            throws SQLException {
        String columnQuery = "PRAGMA table_info(anime)";
        try (PreparedStatement statement = connection.prepareStatement(columnQuery);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                if (columnName.equals(result.getString("name"))) {
                    return;
                }
            }
        }

        String addColumn = "ALTER TABLE anime ADD COLUMN " + columnName + " " + definition;
        try (PreparedStatement statement = connection.prepareStatement(addColumn)) {
            statement.executeUpdate();
        }
    }

    private void migrateEpisodesToLength(Connection connection) throws SQLException {
        String copyLength = "UPDATE anime SET length = episodes "
                + "WHERE length IS NULL AND episodes IS NOT NULL";
        try (PreparedStatement statement = connection.prepareStatement(copyLength)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            if (!e.getMessage().contains("no such column: episodes")) {
                throw e;
            }
        }
    }
    
    public void addAnime(Anime anime) {
        String insertAnime = """
                INSERT INTO anime
                    (title, length, status, rating, priority, notes,
                    current_episode_watched, year)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertGenre = """
                INSERT OR IGNORE INTO genres (name)
                VALUES (?)
                """;

        String findGenre = """
                SELECT id FROM genres
                WHERE name = ?
                """;

        String insertAnimeGenre = """
                INSERT INTO anime_genres (anime_id, genre_id)
                VALUES (?, ?)
                """;

        try (Connection connection = connect()) {

            // Start transaction
            connection.setAutoCommit(false);

            try {
                // 1. Insert the anime
                int animeId;

                try (PreparedStatement statement =
                        connection.prepareStatement(
                                insertAnime,
                                java.sql.Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, anime.getName());
                    statement.setInt(2, anime.getLength());
                    statement.setString(3, anime.getStatus());
                    statement.setDouble(4, anime.getRating());
                    statement.setInt(5, anime.getPriority());
                    statement.setString(6, anime.getNote());
                    statement.setInt(7, anime.getCurrentEpisodeWatched());
                    statement.setInt(8, anime.getYear());

                    statement.executeUpdate();

                    // Get the ID SQLite generated
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not get anime ID");
                        }

                        animeId = keys.getInt(1);
                    }
                }

                // Put the generated ID into our Java object
                anime.setId(animeId);

                // 2. Process each genre
                for (String genreName : anime.getGenre()) {

                    // Insert genre if it doesn't already exist
                    try (PreparedStatement statement =
                            connection.prepareStatement(insertGenre)) {

                        statement.setString(1, genreName);
                        statement.executeUpdate();
                    }

                    // Find the genre's ID
                    int genreId;

                    try (PreparedStatement statement =
                            connection.prepareStatement(findGenre)) {

                        statement.setString(1, genreName);

                        try (ResultSet result = statement.executeQuery()) {
                            if (!result.next()) {
                                throw new SQLException(
                                        "Could not find genre: " + genreName);
                            }

                            genreId = result.getInt("id");
                        }
                    }

                    // 3. Connect anime and genre
                    try (PreparedStatement statement =
                            connection.prepareStatement(insertAnimeGenre)) {

                        statement.setInt(1, animeId);
                        statement.setInt(2, genreId);

                        statement.executeUpdate();
                    }
                }

                // Everything succeeded
                connection.commit();

            } catch (SQLException e) {
                // Something failed → undo everything
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not add anime", e);
        }
    }

    public List<Anime> getAllAnime() {
        List<Anime> animeList = new ArrayList<>();

        String sql = """
                SELECT
                    anime.id,
                    anime.title,
                    anime.length,
                    anime.status,
                    anime.rating,
                    anime.priority,
                    anime.notes,
                    anime.current_episode_watched,
                    anime.year,
                    genres.name AS genre
                FROM anime
                LEFT JOIN anime_genres
                    ON anime.id = anime_genres.anime_id
                LEFT JOIN genres
                    ON anime_genres.genre_id = genres.id
                ORDER BY anime.id
                """;

        try (Connection connection = connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()) {

            Anime currentAnime = null;

            while (result.next()) {

                int animeId = result.getInt("id");

                // Create a new Anime when we encounter a new ID
                if (currentAnime == null
                        || currentAnime.getId() != animeId) {

                    currentAnime = new Anime(
                            result.getString("title"),
                            new ArrayList<>(),
                            result.getInt("length"),
                            result.getString("status"),
                            result.getString("notes"),
                            result.getInt("priority"),
                            result.getInt("year")
                    );

                    currentAnime.setId(animeId);
                    currentAnime.setRating(result.getDouble("rating"));
                    currentAnime.setCurrentEpisodeWatched(
                            result.getInt("current_episode_watched")
                    );

                    animeList.add(currentAnime);
                }

                // Add this row's genre
                String genre = result.getString("genre");

                if (genre != null) {
                    currentAnime.getGenre().add(genre);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not read anime", e);
        }

        return animeList;
    }

    public void updateAnime(Anime anime) {
        String updateAnime = """
                UPDATE anime
                SET title = ?,
                    length = ?,
                    status = ?,
                    rating = ?,
                    priority = ?,
                    notes = ?,
                    current_episode_watched = ?,
                    year = ?
                WHERE id = ?
                """;

        String deleteGenres = """
                DELETE FROM anime_genres
                WHERE anime_id = ?
                """;

        String insertGenre = """
                INSERT OR IGNORE INTO genres (name)
                VALUES (?)
                """;

        String findGenre = """
                SELECT id FROM genres
                WHERE name = ?
                """;

        String insertAnimeGenre = """
                INSERT INTO anime_genres (anime_id, genre_id)
                VALUES (?, ?)
                """;

        try (Connection connection = connect()) {

            connection.setAutoCommit(false);

            try {
                // 1. Update the anime itself
                try (PreparedStatement statement =
                        connection.prepareStatement(updateAnime)) {

                    statement.setString(1, anime.getName());
                    statement.setInt(2, anime.getLength());
                    statement.setString(3, anime.getStatus());
                    statement.setDouble(4, anime.getRating());
                    statement.setInt(5, anime.getPriority());
                    statement.setString(6, anime.getNote());
                    statement.setInt(7, anime.getCurrentEpisodeWatched());
                    statement.setInt(8, anime.getYear());
                    statement.setInt(9, anime.getId());

                    statement.executeUpdate();
                }

                // 2. Remove old genre relationships
                try (PreparedStatement statement =
                        connection.prepareStatement(deleteGenres)) {

                    statement.setInt(1, anime.getId());
                    statement.executeUpdate();
                }

                // 3. Add the new genre relationships
                for (String genreName : anime.getGenre()) {

                    // Make sure genre exists
                    try (PreparedStatement statement =
                            connection.prepareStatement(insertGenre)) {

                        statement.setString(1, genreName);
                        statement.executeUpdate();
                    }

                    // Find genre ID
                    int genreId;

                    try (PreparedStatement statement =
                            connection.prepareStatement(findGenre)) {

                        statement.setString(1, genreName);

                        try (ResultSet result = statement.executeQuery()) {
                            if (!result.next()) {
                                throw new SQLException(
                                        "Could not find genre: " + genreName);
                            }

                            genreId = result.getInt("id");
                        }
                    }

                    // Create Anime ↔ Genre relationship
                    try (PreparedStatement statement =
                            connection.prepareStatement(insertAnimeGenre)) {

                        statement.setInt(1, anime.getId());
                        statement.setInt(2, genreId);
                        statement.executeUpdate();
                    }
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not update anime", e);
        }
    }

    public void deleteAnime(Anime anime) {
        String deleteGenres = """
                DELETE FROM anime_genres
                WHERE anime_id = ?
                """;

        String deleteAnime = """
                DELETE FROM anime
                WHERE id = ?
                """;

        try (Connection connection = connect()) {

            connection.setAutoCommit(false);

            try {
                // 1. Delete Anime ↔ Genre relationships
                try (PreparedStatement statement =
                        connection.prepareStatement(deleteGenres)) {

                    statement.setInt(1, anime.getId());
                    statement.executeUpdate();
                }

                // 2. Delete the Anime itself
                try (PreparedStatement statement =
                        connection.prepareStatement(deleteAnime)) {

                    statement.setInt(1, anime.getId());
                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not delete anime", e);
        }
    }
}