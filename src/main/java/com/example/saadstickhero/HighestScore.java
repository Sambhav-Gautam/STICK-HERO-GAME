package com.example.saadstickhero;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Map;

public class HighestScore {

        private static HighestScore instance = null;
        private Label scoreLabel = null;
        private Label highest = null;

    public HighestScore(Pane gamePane) {
        Map.Entry<String, Integer> highestScoreEntry = GameStateManager.findHighestScore();
//        if (highestScoreEntry != null) {
//            System.out.println("Highest Score: " + highestScoreEntry.getValue() + " by " + highestScoreEntry.getKey());
//        } else {
//            System.out.println("No high score found");
//        }
        scoreLabel = new Label("HighestScore: 0");
        scoreLabel.setFont(new Font(20));
        scoreLabel.setTextFill(Color.WHITE); // Set text color
        scoreLabel.setStyle("-fx-background-color: #2c3e50; -fx-padding: 5px; -fx-background-radius: 5px;"); // Set background color and padding
        // Position the score label at the middle top of the gamePane
        scoreLabel.setLayoutX((gamePane.getWidth() - scoreLabel.getWidth()) / 4);
        scoreLabel.setLayoutY(10); // Adjust this value for vertical positioning
        // Apply fade-in animation to the score label
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), scoreLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        scoreLabel.toFront();
        gamePane.getChildren().add(scoreLabel);

    }

        public static HighestScore getInstance(Pane gamePane) {
        instance = new HighestScore(gamePane);
        return instance;
//        if (instance == null) {
//            instance = new ScoreManager(gamePane);
//        }
//        return instance;
    }

        public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

}
