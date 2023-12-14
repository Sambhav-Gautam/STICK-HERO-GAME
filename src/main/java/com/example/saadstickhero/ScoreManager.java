package com.example.saadstickhero;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * The ScoreManager class represents a label displaying the current score in the Stick Hero game.
 * It provides methods to update and display the score label with a fade-in animation.
 */
public class ScoreManager {
    private static ScoreManager instance = null;
    private final Label scoreLabel;

    /**
     * Constructs a ScoreManager object and initializes the score label with default values.
     *
     * @param gamePane the game pane where the score label will be displayed
     */
    public ScoreManager(Pane gamePane) {
        scoreLabel = createScoreLabel();
        fadeInScoreLabel();
        gamePane.getChildren().add(scoreLabel);
    }

    /**
     * Gets the singleton instance of the ScoreManager.
     * If the instance is null, it creates a new instance.
     *
     * @param gamePane the game pane where the score label will be displayed
     * @return the ScoreManager instance
     */
    public static ScoreManager getInstance(Pane gamePane) {
        if (instance == null) {
            instance = new ScoreManager(gamePane);
        }
        return instance;
    }

    /**
     * Updates the displayed score on the label.
     *
     * @param score the new score
     */
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Creates the label to display the score.
     *
     * @return the created Label object
     */
    private Label createScoreLabel() {
        Label label = new Label("Score: 0");
        label.setFont(new Font(20));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-background-color: #2c3e50; -fx-padding: 5px; -fx-background-radius: 5px;");
        label.setLayoutX(300);
        label.setLayoutY(10);
        return label;
    }

    /**
     * Fades in the score label using a FadeTransition.
     */
    private void fadeInScoreLabel() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), scoreLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        scoreLabel.toFront();
    }
}
