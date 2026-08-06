# Anilog 
### A Personalized Anime Management & Recommendation Engine

## Project Proposal

### What will the application do?
**Anilog** is a Java-based desktop application designed to help otakus track, organize, and optimize their media consumption. To diffrentiate from a passive spreadsheet, the application acts as an active assistant. It manages individual anime tracking states automatically, provides easy-to-use filtering and searching functions, builds a dynamic Personal Statistics Dashboard, and later features a local JSON-based anime database for quick searching and importing. Additionally, it implements a custom, rule-based recommendation feature that parses user priorities, genres, and length preferences to rank and suggest what the user should watch next from their "Plan to Watch" backlog.

### Who will use it?
The application is tailored for **anime and potentially other media hobbyists** who want a lightweight, offline-first alternative to massive web platforms like MyAnimeList. It targets users who want deep insights into their viewing habits and an automated way to sort through a massive backlog of unwatched shows.

### Why is this project of interest to you?
As someone who enjoys watching and being emotionally attached to animes, I frequently lose track of my current episode progress, forget about past masterpieces, stuck choosing what to watch, and even lose intrest in the dimishing lesiure time. Building this application allows me to solve this personal frustration. Architecturally, it serves as an ideal canvas to master object-oriented programming design patterns, encapsulation, state-machine automation, and decoupled data layers in Java.

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

* You can save the state of the application by selecting the "Save Watchlist" button in the settings.

* You can reload the state of my application by selecting the "Load Watchlist" button in the settings.