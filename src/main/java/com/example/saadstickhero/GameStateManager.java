package com.example.saadstickhero;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GameStateManager {
    private static final String FILE_PATH = "game_state.txt";

    private static Map<String, Integer> gameStates;

    public GameStateManager() throws IOException {
        this.gameStates = loadGameStates();
    }

    public Map<String, Integer> loadGameStates() throws IOException {
        Map<String, Integer> loadedGameStates = new HashMap<>();

        URL resourceUrl = getClass().getClassLoader().getResource(FILE_PATH);
        if (resourceUrl == null) {
            System.err.println("Resource not found: " + FILE_PATH);
            return loadedGameStates;
        }

        try (InputStream inputStream = resourceUrl.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
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

    private void saveGameStates() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Integer> entry : gameStates.entrySet()) {
                writer.append(entry.getKey()).append(":").append(String.valueOf(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getScore(String username) {
        return gameStates.getOrDefault(username, 0);
    }

    public void updateScore(String username, int newScore) {
        gameStates.put(username, newScore);
        saveGameStates();
    }

    // New method to find the highest score
    public static Map.Entry<String, Integer> findHighestScore() {
        return gameStates.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }
}
