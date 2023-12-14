//Package Name
package com.example.saadstickhero;

//Header File
import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
//Header File

/**
 * The HighestScore class represents a label displaying the highest score in the Stick Hero game.
 * It provides methods to update and display the highest score label with a fade-in animation.
 */
public class HighestScore{

    private final Label scoreLabel;

    /**
     * Constructs a HighestScore object and initializes the score label with default values.
     *
     * @param gamePane the game pane where the score label will be displayed
     */
    public HighestScore(Pane gamePane){
        scoreLabel = createScoreLabel();
        fadeInScoreLabel();
        gamePane.getChildren().add(scoreLabel);
    }

    /**
     * Updates the displayed highest score on the label.
     *
     * @param score the new highest score
     */
    public void updateScore(int score){
        scoreLabel.setText("HighestScore: " + score);
    }

    /**
     * Creates the label to display the highest score.
     *
     * @return the created Label object
     */
    private Label createScoreLabel(){
        Label label = new Label("HighestScore: 0");
        label.setFont(new Font(20));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-background-color: #2c3e50; -fx-padding: 5px; -fx-background-radius: 5px;");
        label.setLayoutX(10);
        label.setLayoutY(10);
        return label;
    }

    /**
     * Fades in the highest score label using a FadeTransition.
     */
    private void fadeInScoreLabel(){
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1500), scoreLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        scoreLabel.toFront();
    }
}
