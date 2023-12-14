package com.example.saadstickhero;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;



public class StickHeroController {
    private int currentScore =0;
    private ScoreManager score;
    private Cherry cherry;


    private Pane gamePane;
    private Line stick;
    private AtomicBoolean isMousePressed;
    private AtomicReference<Timeline> stickExtensionTimeline;
    private String stage = "waiting";
    private ImageView heroImageView;
    private double Stick_length;
    private GameStateManager gameStateManager = new GameStateManager();
    private Rectangle Pillar0;
    private Rectangle Pillar1;
    private boolean isNotFlipped = true;
    private boolean isReleased;
    private  String username;
    private Label highestScoreLabel = null;

    public StickHeroController() throws IOException {
    }

    private String getUsername() {
        TextInputDialog username = new TextInputDialog();
        username.setTitle("Username");
        username.setHeaderText("Enter your username:");
        username.initStyle(StageStyle.UTILITY);
        return username.showAndWait().orElse(null);
    }
    public  void init() throws IOException {
        username = getUsername();
        if (username == null) {

            return;
        }
        gamePane = new Pane();
        Scene gameScene = new Scene(gamePane, 600, 700); // Adjust the window width if needed
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/saadstickhero/Images/one.png"), 600, 700, false, true);
        ImageView backgroundImageView = new ImageView(bgImage);
        backgroundImageView.setFitWidth(600);
        backgroundImageView.setFitHeight(700);
        Image heroImage = new Image(getClass().getResourceAsStream("/com/example/saadstickhero/Images/player_idle.png"));
        heroImageView = new ImageView(heroImage);
        heroImageView.setFitWidth(30);
        heroImageView.setFitHeight(30);
        heroImageView.setX(75);
        heroImageView.setY(700 - 135);        double windowHeight = 700; // Height of the window
        Rectangle pillar1 = createGradientRectangle(50, windowHeight - 100, 50, 100, Color.DARKBLUE);
        pillar1.setEffect(createDropShadowEffect()); // Apply drop shadow effect
        Random random = new Random();

        // Generate a random number in the range 1 to 499
        int randomNumber = random.nextInt(400) + 100;
        int randomNumber1 = random.nextInt(400)+25;
        Rectangle pillar2 = createGradientRectangle(pillar1.getX() + randomNumber + pillar1.getWidth(),
                windowHeight - 100, randomNumber1, 100, Color.DARKBLUE);
        pillar2.setEffect(createDropShadowEffect()); // Apply glow effect
        heroImageView = new ImageView(heroImage);
        heroImageView.setFitWidth(30); // Set width of the image
        heroImageView.setFitHeight(30); // Set height of the image
        heroImageView.setX(75); // Set X-coordinate
        heroImageView.setY(windowHeight - 135); // Set Y-coordinate
        isMousePressed = new AtomicBoolean(false);
        stickExtensionTimeline = new AtomicReference<>(new Timeline());


        gamePane.getChildren().addAll(new ImageView(bgImage),pillar1, pillar2, heroImageView);
        score =  ScoreManager.getInstance(gamePane);
        this.Pillar0 = pillar1;
        this.Pillar1 = pillar2;
        int cherryPosition = random.nextInt((int) (Pillar1.getX()-200))+100;
        cherry = new Cherry(cherryPosition, Pillar1.getY(),gamePane);
        //cherry.addToPane(gamePane);
        HighestScore ab = new HighestScore(gamePane);
        Map.Entry<String, Integer> highestScoreEntry = GameStateManager.findHighestScore();
        if(highestScoreEntry==null){
            ab.updateScore(0);
        }
        else ab.updateScore(highestScoreEntry.getValue());

        gameScene.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && stage=="waiting") {
               stickLengthIncrease();
            }

        });
        gameScene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && stage=="walking") {
                if (stage.equals("walking") && !isReleased) {
                    // Check if the character is near the end of the current pillar
                    double pillarStartX = Pillar1.getX() ; // Assuming the character width is 30
                    double characterX = heroImageView.getX();

                    if (characterX <= pillarStartX) {
                        flipCharacter();
                    }
                } else if (stage.equals("waiting")) {
                    // Handle actions when waiting for input, if necessary
                }
            }
        });


        gameScene.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY && stage=="waiting") {
                isReleased=true;
                this.stick = stick;
                isMousePressed.set(false);
                stickExtensionTimeline.get().stop();
                stage = "Falling";
                fallStick();
                walkOnStick();
            }
        });

        Stage stage = new Stage();
        stage.setScene(gameScene);
        stage.show();
//        primaryStage.setScene(gameScene);
//        primaryStage.show();
    }
    // Method to perform actions when the character reaches the end of the pillar
    private void stickLengthIncrease(){
        stick = new Line();
        isMousePressed.set(true);
        stick.setStartX(Pillar0.getX() + Pillar0.getWidth());
        stick.setStartY(Pillar0.getY());
        stick.setEndX(Pillar0.getX() + Pillar0.getWidth());
        stick.setEndY(Pillar0.getY());
        stick.setStroke(Color.BLACK);


        gamePane.getChildren().add(stick);
        stickExtensionTimeline.set(new Timeline(
                new KeyFrame(Duration.millis(3.5), e -> {
                    stick.setEndX(Pillar0.getX() + Pillar0.getWidth());
                    stick.setEndY(stick.getEndY() - 1);
                })
        ));
        stickExtensionTimeline.get().setCycleCount(Timeline.INDEFINITE);
        stickExtensionTimeline.get().play();
    }

    // Method to flip the character vertically
    private void flipCharacter() {
        double currentScaleY = heroImageView.getScaleY();
        double imageHeight = 30; // Assuming the image height is 30 units
        if (!isNotFlipped) {
            heroImageView.setScaleY(1);
            heroImageView.setTranslateY(heroImageView.getTranslateY() - imageHeight);
        } else {
            heroImageView.setScaleY(-1);
            heroImageView.setTranslateY(heroImageView.getTranslateY() + imageHeight);
        }

        isNotFlipped = !isNotFlipped;
    }

    private Rectangle createGradientRectangle(double x, double y, double width, double height, Color baseColor) {
        double gradientStopOffset = 0.5;

        // Define gradient stops based on the base color
        Stop[] stops = new Stop[] {
                new Stop(0, baseColor),
                new Stop(gradientStopOffset, baseColor.darker()),
                new Stop(1, baseColor.brighter())
        };

        // Create a linear gradient
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, null, stops);

        // Create a rectangle with gradient fill
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(linearGradient);

        return rectangle;
    }

    // Function to create a drop shadow effect
    private DropShadow createDropShadowEffect() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        return dropShadow;
    }

    // Function to create a glow effect

    private void fallStick() {
        double stickLength = Math.abs(stick.getEndY() - stick.getStartY());
        this.Stick_length = stickLength;
        double platformHeight = 100; // Replace with the height of your platform
        double canvasHeight = 700; // Replace with the height of your canvas
        // Pillar 1 dimensions (hardcoded for demonstration)
        double pillar1X = Pillar0.getX();
        double pillar1Y = Pillar0.getY();
        double pillar1Width = Pillar0.getWidth();
        // Find the bottom of the stick (fixed position)
        double bottomX = stick.getStartX();
        double bottomY = canvasHeight - platformHeight;

        // Calculate the new start point of the stick after rotation
        double rotatedPillarX = pillar1X + pillar1Width;
        double rotatedPillarY = pillar1Y;

        // Calculate stick rotation angle
        double angle = Math.atan2(stick.getEndY() - stick.getStartY(), stick.getEndX() - stick.getStartX());
        double degrees = Math.toDegrees(angle);

        // Rotate the stick by 90 degrees
        Rotate rotate = new Rotate(90, bottomX, bottomY);
        stick.getTransforms().clear();
        stick.getTransforms().add(rotate);

        // Set the end of the stick to remain parallel to the rectangle's top edge
        stick.setEndX(rotatedPillarX);
        stick.setEndY(rotatedPillarY - stickLength);

        // Set the start of the stick to the new rotated position (pillar's edge)
        stick.setStartX(rotatedPillarX);
        stick.setStartY(rotatedPillarY);
        stage = "walking";
    }

    private void characterFall() {
        stage = "falling";
        double fallDistance = 200;
        Duration duration = Duration.seconds(2);
        double initialY = heroImageView.getY();
        double finalY = initialY + fallDistance;
        // Initialize the Timeline
        Timeline fallTimeline = new Timeline();
        KeyValue fallKeyValue = new KeyValue(heroImageView.yProperty(), finalY);
        KeyFrame fallKeyFrame = new KeyFrame(duration, fallKeyValue);
        // Add key frame to the Timeline for animating the fall movement of the ImageView
        fallTimeline.getKeyFrames().add(fallKeyFrame);
        fallTimeline.play();

        // Optional: Add an event handler for when the fall animation completes

            // Show the revive and restart pop-up
        fallTimeline.setOnFinished(event -> {
            // Show the revive and restart pop-up
            showReviveRestartPopup();
        });



    }


    private void showReviveRestartPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Revive and Restart");

        // Background
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/saadstickhero/Images/BG.jpg"), 300, 150, false, true);
        ImageView backgroundImageView = new ImageView(bgImage);
        backgroundImageView.setFitWidth(300);
        backgroundImageView.setFitHeight(150);


        // Message Label
        Label messageLabel = new Label("Do you want to revive and continue?");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Buttons
        Button reviveButton = createStyledButton("Revive", "#4CAF50");
        Button restartButton = createStyledButton("Restart", "#4CAF50");
        Button quitButton = createStyledButton("Quit", "#4CAF50");

        // Button Actions
        reviveButton.setOnAction(event -> {
            // Handle the revive option
            reviveAndContinue();
            popupStage.close();
        });

        restartButton.setOnAction(event -> {
            // Handle the restart option
            restartGame();
            popupStage.close();
        });

        quitButton.setOnAction(event -> {
            gameStateManager.updateScore(username,Cherry.cherryScore);
            Platform.exit();
        });

        // Layout
        VBox popupLayout = new VBox(10);
        popupLayout.getChildren().addAll(messageLabel, reviveButton, restartButton, quitButton);
        popupLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        popupLayout.setPadding(new Insets(20));
        popupLayout.setEffect(new DropShadow());

        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundImageView, popupLayout);

        Scene popupScene = new Scene(root, 300, 150);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: " + color + "; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #45a049;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + ";"));
        return button;
    }



    private void restartGame() {
        if(!isNotFlipped) flipCharacter();
        isNotFlipped = true;
        this.setCurrentScore(0);
        gamePane.getChildren().remove(stick);
        heroImageView.setFitWidth(30); // Set width of the image
        heroImageView.setFitHeight(30); // Set height of the image
        heroImageView.setX(75); // Set X-coordinate
        double windowHeight = 700;
        heroImageView.setY(windowHeight - 135); // Set Y-coordinate
        score.updateScore(0);
        Cherry.cherryScore =0;
        cherry.updateCherryScore(0);
        stage = "waiting";
    }

    private void reviveAndContinue() {
        // TODO
        // Check if we should revive the person or not
        if (Cherry.cherryScore >= 3) {
            Cherry.cherryScore -= 3;
            cherry.updateCherryScore(Cherry.cherryScore);
            if (!isNotFlipped) flipCharacter();
            isNotFlipped = true;
            gamePane.getChildren().remove(stick);
            heroImageView.setFitWidth(30); // Set width of the image
            heroImageView.setFitHeight(30); // Set height of the image
            heroImageView.setX(75); // Set X-coordinate
            double windowHeight = 700;
            heroImageView.setY(windowHeight - 135); // Set Y-coordinate
            stage = "waiting";
        } else {
            showInsufficientCherriesPopup();
            gameStateManager.updateScore(username,Cherry.cherryScore);

        }
    }

    private void showInsufficientCherriesPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Insufficient Cherries");

        Label messageLabel = new Label("You don't have enough cherries to revive.");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        closeButton.setOnAction(event -> {
            popupStage.close();
            showReviveRestartPopup();
        });


        VBox popupLayout = new VBox(10);
        popupLayout.getChildren().addAll(messageLabel, closeButton);
        popupLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        popupLayout.setPadding(new Insets(20));
        popupLayout.setEffect(new DropShadow());

        Scene popupScene = new Scene(popupLayout, 300, 150);
        popupStage.setScene(popupScene);
        popupStage.show();
    }



    private void walkOnStick(){
        double stickLength = Stick_length;
        double startX = this.stick.getStartX();
        double finalX ;
        // Final X-coordinate for the Pillar1
        if(stickLength+stick.getStartX()>=Pillar1.getX() && stickLength+stick.getStartX()<=Pillar1.getX() +Pillar1.getWidth() ){
            finalX = Pillar1.getX() +Pillar1.getWidth() - 25;
        }
        else{
            finalX = startX + stickLength -25 ;
        }
        final double increment = 1; // Adjust the increment value for slower/faster movement
        final double[] currentX = { heroImageView.getX() }; // Current X-coordinate of the hero
        // Initialize the Timeline
        Timeline timeline = new Timeline();
        // Add key frame to the Timeline for animating the movement of the circle
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(5), e -> {
                    // Update the X-coordinate of the circle
                    currentX[0] += increment;
                    //TODO
                    //CHECK THIS
                    if(!isNotFlipped && cherry.getXPosition()==currentX[0] ){
                        gamePane.getChildren().remove(cherry);
                        cherry.collectCherry(gamePane);
                    }
                    if(currentX[0]==Pillar1.getX() && !isNotFlipped){
                        timeline.stop();
                        characterFall();
                    }
                    if (currentX[0] <= finalX) {
                        heroImageView.setX(currentX[0]);
                        isReleased= false;
                    } else {
                        heroImageView.setX(finalX);
                        timeline.stop(); // Stop the animation when the final position is reached
                        stage= "waiting";
                        if(stickLength+stick.getStartX()>=Pillar1.getX() && stickLength+stick.getStartX()<=Pillar1.getX() +Pillar1.getWidth() ){
                            performPillarTransition();
                            currentScore++;
                            score.updateScore(currentScore);
                        }
                        else{
                            timeline.stop();
                            characterFall();

                        }

                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        // Call performPillarTransition() after walkOnStick() animation finishes
        timeline.play();
    }


    private void performPillarTransition() {
        stage = "transitioning";
        double stickLength = Stick_length;
        double startX = this.Pillar0.getX();
        int windowHeight = 700;
        final double decrement = 1; // Adjust the decrement value for slower/faster movement
        final double[] totalDecrement = {1};
        // Initialize the Timeline
        Random random = new Random();
        // Generate a random number in the range 1 to 499
        int randomNumber = random.nextInt(400) + 100;
        int randomNumber1 = random.nextInt(400)+25;
        Rectangle newPillar = createGradientRectangle(this.Pillar1.getX()+this.Pillar1.getWidth()+randomNumber, windowHeight - 100, randomNumber1, 100,Color.DARKBLUE);
        newPillar.setEffect(createDropShadowEffect());
        Timeline timeline = new Timeline();
        gamePane.getChildren().add(newPillar);
        final double[] currentX = { this.Pillar0.getX(), this.Pillar1.getX(), this.stick.getStartX(),newPillar.getX(),stickLength+stick.getStartX(),this.cherry.getXPosition() }; // Current X-coordinate of the hero
        Duration.seconds(1);

        // Add key frame to the Timeline for animating the movement of the pillars and stick
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(5), e -> {
                    // Update the X-coordinate of the pillars and stick
                    currentX[0] -= decrement;
                    currentX[1] -= decrement;
                    currentX[2] -= decrement;
                    currentX[3] -= decrement;
                    currentX[4] +=decrement;
                    currentX[5] -= decrement;
                    totalDecrement[0] += decrement;
                    //if (currentX[0] >= startX - 100 || currentX[1] > startX) {
                    if (currentX[1]!=100-Pillar1.getWidth()) {
                        newPillar.setX(currentX[3]);
                        this.Pillar0.setX(currentX[0]);
                        this.Pillar1.setX(currentX[1]);
                        this.cherry.setXPosition(currentX[5]);
                        this.heroImageView.setX(currentX[1]+ this.Pillar1.getWidth()- 25);
                        this.stick.setTranslateX(-totalDecrement[0]);
                    } else {
                        newPillar.setX(currentX[3]);
                        this.cherry.setXPosition(currentX[5]);
                        this.Pillar0.setX(startX - 1000);
                        //this.Pillar1.setX(startX);
                        this.Pillar1.setX(100-Pillar1.getWidth());
                        this.heroImageView.setX(currentX[1]+this.Pillar1.getWidth()- 25);
                        this.stick.setTranslateX(-totalDecrement[0]);
                        timeline.stop(); // Stop the animation when the final position is reached
                        stage = "waiting";
                        gamePane.getChildren().remove(stick);
                        gamePane.getChildren().remove(Pillar0);
                        this.Pillar0 = this.Pillar1;
                        this.Pillar1 = newPillar;
                        stick.setStartX(Pillar0.getX() + Pillar0.getWidth());
                        stick.setStartY(Pillar0.getY());
                        stick.setEndX(Pillar0.getX() + Pillar0.getWidth());
                        stick.setEndY(Pillar0.getY());
                        int cherryPosition = random.nextInt((int) (Pillar1.getX()-200))+100;
                        cherry.setXPosition(cherryPosition);
                        cherry.addCherry(gamePane);

                    }
                })
        );
        // Set the cycle count and play the animation
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }



    public double getStick_length() {
        return Stick_length;
    }

    public void setStick_length(double stick_length) {
        Stick_length = stick_length;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public void optionsAction() {
    }

    public void loadAction() {
    }

    public void quitAction() {
        Platform.exit();
    }

    public void stopSnowflakes() {
        StickHeroGame.snowflakesActive =false;
    }

    public Cherry getCherry() {
        return cherry;
    }

    public void setCherry(Cherry cherry) {
        this.cherry = cherry;
    }
}
