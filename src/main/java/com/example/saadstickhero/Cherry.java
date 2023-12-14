//PACKAGE NAME
package com.example.saadstickhero;

//HEADER FILES
import com.example.saadstickhero.Errors.CherryImageNotFoundException;
import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.io.IOException;
import java.io.InputStream;
//HEADER FILES


/*
 * Created By - > Sambhav Gautam
 */

/**
 * The Cherry class represents a cherry in the Stick Hero game.
 * It provides methods to collect, add, and update cherries, as well as manage the cherry's appearance.
 */
public class Cherry {
    private static final int CHERRY_SIZE = 20;
    private static final int CHERRY_LABEL_FONT_SIZE = 20;
    private static final String CHERRY_LABEL_STYLE = "-fx-background-color: #2c3e50; -fx-padding: 5px; -fx-background-radius: 5px;";
    private static final Duration FADE_DURATION = Duration.millis(1500);
    private static final String CHERRY_IMAGE_PATH = "/com/example/saadstickhero/Images/cherry.png";
    private static final Color CHERRY_LABEL_TEXT_COLOR = Color.WHITE;

    private final ImageView cherryImageView;
    protected static int cherryScore = 0;
    private double xPosition;
    private final Label cherryLabel;

    /**
     * Constructs a Cherry object at a random position on the stick.
     *
     * @param stickTopX the x-coordinate of the top of the stick
     * @param stickTopY the y-coordinate of the top of the stick
     * @param gamePane  the game pane where the cherry will be displayed
     */
    public Cherry(double stickTopX, double stickTopY, Pane gamePane) {
        Image cherryImage = loadImage();

        cherryImageView = new ImageView(cherryImage);
        cherryImageView.setFitWidth(CHERRY_SIZE);
        cherryImageView.setFitHeight(CHERRY_SIZE);

        initializeCherryPosition(stickTopX, stickTopY);

        cherryLabel = createCherryLabel();
        fadeInCherryLabel();

        gamePane.getChildren().addAll(cherryImageView, cherryLabel);
    }

    /**
     * Loads the cherry image from the specified path.
     *
     * @return the loaded Image object
     */
    private Image loadImage() {
        try (InputStream stream = getClass().getResourceAsStream(Cherry.CHERRY_IMAGE_PATH)) {
            if (stream == null) {
                throw new CherryImageNotFoundException("Cherry image not found: " + Cherry.CHERRY_IMAGE_PATH);
            }
            return new Image(stream);
        } catch (CherryImageNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the position of the cherry on the stick.
     *
     * @param stickTopX the x-coordinate of the top of the stick
     * @param stickTopY the y-coordinate of the top of the stick
     */
    private void initializeCherryPosition(double stickTopX, double stickTopY) {
        double randomX = stickTopX + Math.random() * 50;
        double stickBottomY = stickTopY + 10;

        cherryImageView.setX(randomX);
        cherryImageView.setY(stickBottomY);

        xPosition = randomX;
    }

    /**
     * Creates the label to display the cherry score.
     *
     * @return the created Label object
     */
    private Label createCherryLabel() {
        Label label = new Label("Cherry: 0");
        label.setFont(new Font(CHERRY_LABEL_FONT_SIZE));
        label.setTextFill(CHERRY_LABEL_TEXT_COLOR);
        label.setStyle(CHERRY_LABEL_STYLE);
        label.setLayoutX(500);
        label.setLayoutY(10);

        return label;
    }

    /**
     * Fades in the cherry label using a FadeTransition.
     */
    private void fadeInCherryLabel() {
        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, cherryLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        cherryLabel.toFront();
    }

    /**
     * Collects the cherry, updates the cherry score, and removes the cherry image from the game pane.
     *
     * @param gamePane the game pane from which the cherry is collected
     */
    public void collectCherry(Pane gamePane) {
        cherryScore++;
        updateCherryScore(cherryScore);
        gamePane.getChildren().remove(cherryImageView);
    }

    /**
     * Adds the cherry image to the game pane if it is not already present.
     *
     * @param gamePane the game pane to which the cherry image is added
     */
    public void addCherry(Pane gamePane) {
        if (!gamePane.getChildren().contains(cherryImageView)) {
            gamePane.getChildren().add(cherryImageView);
        }
    }

    /**
     * Updates the displayed cherry score on the label.
     *
     * @param score the new cherry score
     */
    public void updateCherryScore(int score) {
        cherryLabel.setText("Cherry: " + score);
    }

    /**
     * Gets the x-coordinate position of the cherry image.
     *
     * @return the x-coordinate position of the cherry image
     */
    public double getXPosition() {
        return xPosition;
    }

    /**
     * Sets the x-coordinate position of the cherry image.
     *
     * @param x the new x-coordinate position
     */
    public void setXPosition(double x) {
        xPosition = x;
        cherryImageView.setX(xPosition);
    }
}
