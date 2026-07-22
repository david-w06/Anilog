package ui;

import model.Anime;
import model.AnimeList;
import persistence.JsonWriter;
import persistence.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// User interface for the app.
// Currently only fufills the minimum of the user stories.
public class AnimeApp {
    private static final String LOCATION = "./data/animeList.json";

    private Scanner input;
    private AnimeList animeList;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: runs the anime tracker application
    public AnimeApp() {
        input = new Scanner(System.in);
        animeList = new AnimeList();
        jsonWriter = new JsonWriter(LOCATION);
        jsonReader = new JsonReader(LOCATION);
        runAnimeApp();
    }

    // EFFECTS: show title and display command list input until the user quits
    private void runAnimeApp() {
        boolean running = true;

        System.out.println("Welcome to Anilog!");
        askLoad();

        while (running) {
            displayMenu();
            // wait for input
            String command = input.nextLine();

            if (command.equals("quit")) {
                running = false;
                quit();
            } else {
                processCommand(command);
            }
        }

        System.out.println("Goodbye!");
    }

    // EFFECTS: displays the main menu
    private void displayMenu() {
        System.out.println("\nThere are currently " + animeList.getAnimes().size() + " in list");
        System.out.println("add        -> Add an anime");
        System.out.println("remove     -> Remove an anime");
        System.out.println("modify     -> Modify an anime");
        System.out.println("view       -> View all anime");
        System.out.println("sort       -> Sort anime");
        System.out.println("filter     -> Filter anime");
        System.out.println("recommend  -> View recommendations");
        System.out.println("stats      -> View statistics");
        System.out.println("quit       -> Exit");
        System.out.print("\nCommand: ");
    }

    // EFFECTS: processes the user's command
    private void processCommand(String command) {
        if (command.equals("add")) {
            addAnime();
        } else if (command.equals("remove")) {
            removeAnime();
        } else if (command.equals("modify")) {
            modifyAnime();
        } else if (command.equals("view")) {
            viewAnime();
        } else if (command.equals("sort")) {
            sortAnime();
        } else if (command.equals("filter")) {
            filterAnime();
        } else if (command.equals("recommend")) {
            recommendAnime();
        } else if (command.equals("stats")) {
            viewStats();
        } else {
            System.out.println("Invalid command.");
        }
    }

    // EFFECTS: displays list in simple index/name format for selection
    private void displayNumberedAnimeList(List<Anime> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i).getName() + " (" + list.get(i).getStatus() + ")");
        }
    }

    // EFFECTS: adds a new anime to the list
    private void addAnime() {
        System.out.print("Enter name: ");
        String name = input.nextLine();

        System.out.print("Enter genres (e.g., Action, Romance): ");
        String genreInput = input.nextLine();
        List<String> genres = new ArrayList<>();
        for (String g : genreInput.split(",")) {
            if (!g.isEmpty()) {
                genres.add(g);
            }
        }

        System.out.print("Enter the total number of episodes: ");
        int length = Integer.parseInt(input.nextLine());
       
        System.out.print("Enter the number of seasons: ");
        int seasons = Integer.parseInt(input.nextLine());
        
        System.out.print("Enter status (Not Watched, Watching, Completed, Plan to Watch): ");
        String status = input.nextLine();

        System.out.print("Enter personal note: ");
        String note = input.nextLine();

        System.out.print("Enter watch priority (1 is highest): ");
        int priority = Integer.parseInt(input.nextLine());

        Anime newAnime = new Anime(name, genres, length, seasons, status, note, priority);
        animeList.addAnime(newAnime);
        System.out.println("Successfully added " + name);
    }

    // EFFECTS: removes an anime from the list
    private void removeAnime() {
        if (animeList.getAnimes().isEmpty()) {
            System.out.println("Your list is empty!");
            return;
        }
        displayNumberedAnimeList(animeList.getAnimes());
        System.out.print("Select the number of the anime to remove (or 0 to cancel): ");
        int choice = Integer.parseInt(input.nextLine());
        if (choice != 0) {
            Anime selected = animeList.getAnimes().get(choice - 1);
            animeList.removeAnime(selected);
            System.out.println("Successfully removed " + selected.getName());
        }
    }

    // EFFECTS: modifies an existing anime
    // Overhual later to enable other function without surpassing line limit
    private void modifyAnime() {
        if (animeList.getAnimes().isEmpty()) {
            System.out.println("Your list is empty!");
            return;
        }
        displayNumberedAnimeList(animeList.getAnimes());
        System.out.print("Select the number of the anime to modify (or 0 to cancel): ");
        int choice = Integer.parseInt(input.nextLine());
        if (choice != 0) {
            Anime selected = animeList.getAnimes().get(choice - 1);
            System.out.print("Enter episodes watched (Current: " + selected.getCurrentEpisodeWatched() + "): ");
            int currentEp = Integer.parseInt(input.nextLine());
            selected.setCurrentEpisodeWatched(currentEp);
            System.out.println("Updated progress successfully!");
        }
    }

    // EFFECTS: displays every anime in the list
    private void viewAnime() {
        if (animeList.getAnimes().isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }
        for (Anime a : animeList.getAnimes()) {
            System.out.println("Name: " + a.getName());
            System.out.println("Genres: " + String.join(", ", a.getGenre()));
            System.out.println("Status: " + a.getStatus());
            System.out.println("Progress: " + a.getCurrentEpisodeWatched() + "/" + a.getLength() + " episodes");
            System.out.println("Seasons: " + a.getSeasons());
            System.out.println("Priority: " + a.getPriority());
            System.out.println("Rating: " + (a.getRating() == -1.0 ? "Unrated" : a.getRating()));
            System.out.println("Note: " + a.getNote());
            System.out.println("--------------------");
        }
    }

    // EFFECTS: lets the user choose a sorting method
    private void sortAnime() {
        System.out.print("Enter how you would like to sort (Name, Status, Rating): ");
        String sortBy = input.nextLine();

        if (sortBy.equalsIgnoreCase("Name")) {
            sortWatchlistByName();
        } else if (sortBy.equalsIgnoreCase("Status")) {
            sortWatchlistByStatus();
        } else if (sortBy.equalsIgnoreCase("Rating")) {
            sortWatchlistByRating();
        } else {
            System.out.println("Invalid sort type.");
        }
    }

    // Stubs for specific sorting methods called by sortAnime()
    private void sortWatchlistByName() {
        List<Anime> sorted = animeList.sortByName();
        if (sorted.isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }
        System.out.println("\n--- Watchlist by Name ---");
        for (Anime a : sorted) {
            System.out.println(" - " + a.getName() + " (" + a.getStatus() + ")");
        }
    }

    private void sortWatchlistByRating() {
        List<Anime> sorted = animeList.sortByRating();
        if (sorted.isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }
        System.out.println("\n--- Watchlist by Rating ---");
        for (Anime a : sorted) {
            String ratingStr = a.getRating() == -1.0 ? "Unrated" : String.valueOf(a.getRating());
            System.out.println(" - " + a.getName() + " (Rating: " + ratingStr + ")");
        }
    }

    // EFFECTS: Print out watchlist sorted by status
    private void sortWatchlistByStatus() {
        if (animeList.getAnimes().isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }

        System.out.println("\n--- Watchlist by Status ---");
        List<String> statuses = List.of("Watching", "Plan to Watch", "Completed", "Not Watched");
        
        for (String status : statuses) {
            List<Anime> matching = animeList.filterByStatus(status);
            if (!matching.isEmpty()) {
                System.out.println("\n[" + status + "]");
                for (Anime a : matching) {
                    System.out.println(" - " + a.getName() + " (Progress: " 
                            + a.getCurrentEpisodeWatched() + "/" + a.getLength() + ")");
                }
            }
        }
    }

    // EFFECTS: lets the user filter the anime list
    private void filterAnime() {
        System.out.print("Filter by (Genre, Status, Season): ");
        String choice = input.nextLine();
        if (choice.equalsIgnoreCase("Genre")) {
            System.out.print("Enter genre: ");
            String genre = input.nextLine();
            List<Anime> filtered = animeList.filterByGenre(genre);
            if (filtered.isEmpty()) {
                System.out.println("No matching anime found.");
            } else {
                displayNumberedAnimeList(filtered);
            }
        } else if (choice.equalsIgnoreCase("Status")) {
            System.out.print("Enter status (Not Watched, Watching, Completed, Plan to Watch): ");
            String status = input.nextLine();
            List<Anime> filtered = animeList.filterByStatus(status);
            if (filtered.isEmpty()) {
                System.out.println("No matching anime found.");
            } else {
                displayNumberedAnimeList(filtered);
            }
        } else if (choice.equalsIgnoreCase("Season")) {
            System.out.print("Enter number of seasons: ");
            int seasons = Integer.parseInt(input.nextLine());
            List<Anime> filtered = animeList.filterBySeasonCount(seasons);
            if (filtered.isEmpty()) {
                System.out.println("No matching anime found.");
            } else {
                displayNumberedAnimeList(filtered);
            }
        } else {
            System.out.println("Invalid filter option.");
        }
    }

    // EFFECTS: allows the user to choose a recommendation genre and recommends
    private void recommendAnime() {
        String favorite = animeList.calculateFavoriteGenre();
        if (favorite == null) {
            System.out.println("Add and watch some anime first to establish favorite genre preferences.");
            return;
        }
        System.out.print("Choose a genre to recommend: ");
        String chosen = input.nextLine();
        getRecommendations(chosen);
    }

    // EFFECTS: print out recommend anime based on genre
    private void getRecommendations(String genre) {
        List<Anime> recs = animeList.getTopRecommendations(genre);

        if (recs.isEmpty()) {
            System.out.println("No 'Plan to Watch' titles match your top genre (" + genre + ") right now.");
        } else {
            for (Anime r : recs) {
                System.out.println("Based on your preference for **" + genre + "**, AniLog recommend:");
                System.out.println(r.getName() + " (Priority: " + r.getPriority() + ")");
            }
        }
    }

    // EFFECTS: print out all statistics
    private void viewStats() {
        List<Anime> animes = animeList.getAnimes();
        if (animes.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        int totalHours = animeList.calculateTotalWatchTime();

        // Calculate average rating
        double totalRatingSum = 0;
        int ratedCount = 0;
        for (Anime a : animes) {
            if (a.getRating() != -1.0) {
                totalRatingSum += a.getRating();
                ratedCount++;
            }
        }
        
        double avgRating;
        if (ratedCount == 0) {
            avgRating = 0;
        } else {
            avgRating = totalRatingSum / ratedCount;
        }

        // Find favorite genre
        String favGenre = animeList.calculateFavoriteGenre();

        System.out.println("Total Time Watched: " + totalHours + " hours;" + "Overall Average Rating: " + avgRating);
        System.out.println("Top Genre: " + favGenre);
    }
    
    // EFFECTS: prompt user to save watch list
    private void quit() {
        System.out.println("Would you like to save your watch list? (Y/N)");
        
        while (true) {
            String command = input.nextLine();
        
            if (command.equalsIgnoreCase("Y")) {
                try {
                    jsonWriter.open();
                    jsonWriter.write(animeList);
                    jsonWriter.close();
                    System.out.println("Watch list successfully saved!");
                } catch (FileNotFoundException e) {
                    System.out.println("Unable to write to file to: " + LOCATION);
                }
                break;
            } else if (command.equalsIgnoreCase("N")) {
                break;
            } else {
                System.out.println("Please input Y or N");
            }
        }
    }

    // EFFECTS: prompt user to load their saved watch list
    private void askLoad() {
        System.out.print("Would you like to load your saved anime list? (Y/N): ");
        String choice = input.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            try {
                animeList = jsonReader.read();
                System.out.println("Saved list successfully loaded");
            } catch (IOException e) {
                System.out.println("Unable to read from file: " + LOCATION);
            }
        }
    }
}