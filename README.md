# AniLog
### A Personalized Anime Management & Recommendation Engine

## Project Proposal

### What will the application do?
**AniLog** is a Java-based desktop application designed to help anime fans track, organize, and optimize their media consumption. It manages watch progress and ratings, provides filtering and sorting, displays statistics, searches AniList, and generates personalized recommendations from the user's watchlist.

### Who will use it?
The application is tailored for **anime and potentially other media hobbyists** who want a lightweight, offline-first alternative to massive web platforms like MyAnimeList. It targets users who want deep insights into their viewing habits and an automated way to sort through a massive backlog of unwatched shows.

### Why is this project of interest to you?
As someone who enjoys watching and being emotionally attached to animes, I frequently lose track of my current episode progress, forget about past masterpieces, stuck choosing what to watch, and even lose intrest in the dimishing lesiure time. Building this application allows me to solve this personal frustration. Architecturally, it serves as an ideal canvas to master object-oriented programming design patterns, encapsulation, state-machine automation, and decoupled data layers in Java.

## Current Implementation

### Persistence

The desktop application uses SQLite through `AnimeRepository`. The database is stored at `data/anilog.db` and is created automatically when the application starts. The repository persists:

* Anime title, total episode length, status, rating, priority, notes, release year, and watched episode progress.
* AniList ID (`0` means the anime has not been associated with AniList).
* AniList cover image URLs.
* Genres and AniList tags through normalized relationship tables.

The model layer owns persistence calls through `AnimeList`. Adding, editing, deleting, and reloading watchlist entries therefore updates the database without requiring the UI to access SQL directly. Older databases are migrated when possible, including copying legacy episode data into the current `length` column.

JSON reader/writer classes remain available for the original data-format tests and legacy files, but the desktop GUI uses SQLite as its active data store.

### AniList API Integration

`AniListService` communicates with the AniList GraphQL API at `https://graphql.anilist.co`. It supports:

* Searching for anime by title.
* Loading a list of popular anime for recommendation candidates.
* Importing total episode length, genres, tags, release year, cover image URL, and AniList ID.

The Anime Base search view displays AniList cover images and lets the user add a result to the watchlist. The AniList ID and imported metadata are retained when the result is added and saved.

### Recommendation Algorithm

`RecommendationService` uses a content-based weighted scoring model. For each rated watchlist anime, it calculates:

```text
preference weight = (rating - 5.0) * status multiplier
```

Status multipliers are:

* Completed: `1.0`
* Watching: `0.8`
* Plan to Watch: `0.3`
* Unrated or other statuses: `0.0`

The weight is accumulated independently for every genre and AniList tag. Positive values represent preferences; negative values represent dislikes. Candidate scores are calculated as:

```text
score = matching genre weights + (2 * matching tag weights)
```

Tags receive double weight because they are more specific than broad genres. Candidates already in the watchlist are removed using AniList ID when available, with title matching as a fallback. Results are sorted by raw score from highest to lowest, and positive matching genres/tags are retained so the UI can explain each recommendation.

The service normalizes scores between the lowest and highest candidate score for display. The recommendation cards show this normalized value as a percentage; the underlying raw score remains available for ranking and testing.

### Building and Testing

The project uses Java 21, the SQLite JDBC driver, `org.json`, and JUnit 5. From the project root, compile and run the tests with:

```powershell
$main = Get-ChildItem src/main -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$tests = Get-ChildItem src/test -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp "lib/json-20250517.jar;lib/sqlite-jdbc-3.53.4.0.jar;lib/junit-platform-console-standalone-1.10.2.jar" -d bin $main $tests
java -jar lib/junit-platform-console-standalone-1.10.2.jar execute --class-path "bin;lib/json-20250517.jar;lib/sqlite-jdbc-3.53.4.0.jar" --scan-class-path
```

Run the graphical application with `ui.Main` from the project root using the same classpath.

---

## User Stories

* As a user, I want to be able to add a new anime entry to my personal watchlist , specifying its title, total episodes, genre, and more.
* As a user, I want to be able to view my entire watchlist organized by current status (e.g., *Watching*, *Plan to Watch*, *Completed*).
* As a user, I want to be able to increment my watched episode count for a specific anime, having the application automatically flip its status to "Completed" if I hit the maximum episode count.
* As a user, I want to view an automatically calculated statistics dashboard showing my total hours watched, overall average rating, and a breakdown of my top genres.
* As a user, I want to request a personalized recommendation and output the top suggested titles based on my favorite genres.
* As a user, I want to be able to optionally save my entire watchlist when I quit the application.
* As a user, I want to be able to optionally load my saved application when I start up the application.


# Instruction for End user

* You can view the panel that displays the animes that have already been added to the anime watch list by clicking on the "Watchlist" tab in the main sidebar menu.

* You can generate the first required action related to the user story "adding multiple animes to an anime watch list" by going to the "Watchlist" panel, clicking the "+ Add Anime" button in the bottom bar, entering the anime details into the popup dialog, and clicking "OK".

* You can generate the second required action related to the user story "adding multiple animes to an anime watch list" by doing the previous step multiple times.

* You can locate the visual component by viewing the custom rendered cover art images and dynamic canvas vector placeholders displayed at the top of each card in the "Anime Base" panel.

* Watchlist changes are saved automatically to SQLite through the model and repository layers.

* You can reload the current SQLite watchlist by selecting the database reload action in Settings.

### Phase 4: Task 2, Event Log Sample

```text
Sun Aug 09 19:33:02 PDT 2026
Added anime: Naruto to watchlist.
Sun Aug 09 19:33:07 PDT 2026
Updated episode progress for Naruto to episode 12.
Sun Aug 09 19:33:13 PDT 2026
Updated rating for Naruto to 10.0/10.0.
Sun Aug 09 19:33:14 PDT 2026
Added anime: Fullmetal Alchemist: Brotherhood to watchlist.
Sun Aug 09 19:33:32 PDT 2026
Updated episode progress for Fullmetal Alchemist: Brotherhood to episode 5.
Sun Aug 09 19:33:32 PDT 2026
Updated status for Fullmetal Alchemist: Brotherhood to Watching.
Sun Aug 09 19:33:32 PDT 2026
Updated note for Fullmetal Alchemist: Brotherhood.
Sun Aug 09 19:33:32 PDT 2026
Updated priority for Fullmetal Alchemist: Brotherhood to 2.
Sun Aug 09 19:33:37 PDT 2026
Removed anime: Fullmetal Alchemist: Brotherhood from watchlist.
```

### Phase 4: Task 3
One of the main issue I had with the design of this project with the cumbersome and time consuming procedure of developing panels for the GUI. However, most panels share the same structure: they take an AnimeList, build a themed layout, and refresh when data changes. In the diagram, they appear as separate classes with similar associations but no shared supertype type. I would refactor by introducing an abstract base class that holds the shared AnimeList reference and refresh logic. Subclasses would only implement panel-specific behaviour. This would reduce duplicated code  like scroll bar styling, header layout, etc. and make the UI package easier to extend with new tabs.