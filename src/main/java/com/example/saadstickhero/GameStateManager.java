//PACKAGE NAME
package com.example.saadstickhero;


//HEADER FILES
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
//HEADER FILES


/*
 * Created By - > Sambhav Gautam
 */

/**
 * Manages the game state by loading, updating, and saving player scores.
 */
public class GameStateManager {
    /** The file path for storing the game state. */
    private static final String FILE_PATH = "game_state.txt";

    /** Error message for when the resource file is not found. */
    private static final String FILE_NOT_FOUND_MESSAGE = "Resource not found: %s";

    /** The map to store player usernames and their corresponding scores. */
    private static Map<String, Integer> gameStates;

    /**
     * Constructs a GameStateManager and initializes the game state by loading it from a file.
     *
     */
    public GameStateManager() {
        gameStates = loadGameStates();
    }

    /**
     * Loads the game states from a file and returns a map of player usernames and scores.
     *
     * @return a map containing player usernames and their scores.
     */
    public Map<String, Integer> loadGameStates() {
        Map<String, Integer> loadedGameStates = new HashMap<>();

        URL resourceUrl = getClass().getClassLoader().getResource(FILE_PATH);
        if (resourceUrl == null) {
            System.err.printf((FILE_NOT_FOUND_MESSAGE) + "%n", FILE_PATH);
            return loadedGameStates;
        }

        try (InputStream inputStream = resourceUrl.openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    loadedGameStates.put(username, score);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loadedGameStates;
    }

    /**
     * Saves the current game states to the file.
     */
    private void saveGameStates() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Integer> entry : gameStates.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the score of the specified player and saves the game states.
     *
     * @param username the username of the player.
     * @param newScore the new score to be set for the player.
     */
    public void updateScore(String username, int newScore) {
        gameStates.put(username, newScore);
        saveGameStates();
    }

    /**
     * Finds and returns the player with the highest score.
     *
     * @return the entry representing the player with the highest score, or null if the game state is empty.
     */
    public static Map.Entry<String, Integer> findHighestScore() {
        return gameStates.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }
}
