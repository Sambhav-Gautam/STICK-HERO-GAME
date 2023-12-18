package com.example.StickHero;

import javafx.animation.*;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//TODO:
//3) Add sound and music
//4) Add characters with matching backgrounds
//5) Add load and save game
//6) Enhance the reset restart and quit
//7) Let the user load the previous state
//8) Pause
//9) Go back to the home screen again
//10) Enhance username login and add multiple characters at the same time
//11) Try to serialize the class for loading and saving

public class StickHeroController {
    protected static int currentScore =0;
    private ScoreManager score;
    private Cherry cherry;
    private Pane gamePane;
    private Line stick;
    private AtomicBoolean isMousePressed;
    private AtomicReference<Timeline> stickExtensionTimeline;
    private String stage = "waiting";
    private ImageView heroImageView;
    private static final ImageView heroImageViewWalk1 = new ImageView(new Image(Objects.requireNonNull(StickHeroController.class.getResourceAsStream("/com/example/StickHero/Images/player_walk1.png"))));
    private static final ImageView heroImageViewWalk2 = new ImageView(new Image(Objects.requireNonNull(StickHeroController.class.getResourceAsStream("/com/example/StickHero/Images/player_walk2.png"))));
    private double Stick_length;
    private final GameStateManager gameStateManager = new GameStateManager();
    private Rectangle Pillar0;
    private Rectangle Strip0;
    private Rectangle Pillar1;
    private Rectangle Strip1;
    private boolean isNotFlipped = true;
    private boolean isReleased;
    private  String username;

    public StickHeroController() {
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

        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/StickHero/Images/5386360.jpg")), 600, 700, false, true);
        ImageView backgroundImageView = new ImageView(bgImage);
        backgroundImageView.setFitWidth(600);
        backgroundImageView.setFitHeight(700);
        Image heroImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/StickHero/Images/player_idle.png")));
        heroImageView = new ImageView(heroImage);
        heroImageView.setFitWidth(30);
        heroImageView.setFitHeight(30);
        heroImageView.setX(75);
        heroImageView.setY(700 - 135);        double windowHeight = 700; // Height of the window
        Rectangle pillar1 = createGradientRectangle(50, windowHeight - 100, 50);
        Rectangle strip0 = new Rectangle((double) (50 + 100) /2-5,windowHeight-100,10,7);
        //(start + (start+width))/2 - strip_width/2
        strip0.setFill(Color.DARKGREEN);
        this.Strip0 = strip0;

        Random random = new Random();

        // Generate a random number in the range 1 to 499
        int randomNumber = random.nextInt(400) + 100;
        int randomNumber1 = random.nextInt(400)+25;
        int  x = (int) (pillar1.getX() + randomNumber + pillar1.getWidth());
        Rectangle pillar2 = createGradientRectangle(x,
                windowHeight - 100, randomNumber1);
        Rectangle strip1 = new Rectangle((double) (x + x + randomNumber1) /2-5,windowHeight-100,10,7);
        strip1.setFill(Color.DARKGREEN);
        this.Strip1 = strip1;


        heroImageView = new ImageView(heroImage);
        heroImageView.setFitWidth(30); // Set width of the image
        heroImageView.setFitHeight(30); // Set height of the image
        heroImageView.setX(75); // Set X-coordinate
        heroImageView.setY(windowHeight - 135); // Set Y-coordinate
        isMousePressed = new AtomicBoolean(false);
        stickExtensionTimeline = new AtomicReference<>(new Timeline());



        Text Hint = new Text("Press Mouse Key To Begin !!!");
        Hint.setTextAlignment(TextAlignment.CENTER);
        Hint.setFill(Color.BLACK);
        Hint.setFont(Font.font(25));
        // Position the bonus text (you may need to adjust the coordinates)
        Hint.setLayoutX(175);
        Hint.setLayoutY(121);
        gamePane.getChildren().addAll(new ImageView(bgImage),pillar1, pillar2, heroImageView,strip0,strip1,Hint);
        score =  ScoreManager.getInstance(gamePane);
        this.Pillar0 = pillar1;
        this.Pillar1 = pillar2;
        int cherryPosition = random.nextInt((int) (Pillar1.getX()-200))+100;
        cherry = new Cherry(cherryPosition, Pillar1.getY(),gamePane);


        gameScene.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && Objects.equals(stage, "waiting")) {
                gamePane.getChildren().remove(Hint);
                stickLengthIncrease();
            }

        });
        gameScene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && Objects.equals(stage, "walking")) {
                if (!isReleased) {
                    double pillarStartX = Pillar1.getX(); // Assuming the character width is 30
                    double characterX = 0;

                    if (gamePane.getChildren().contains(heroImageView)) {
                        characterX = heroImageView.getX();
                    } else if (gamePane.getChildren().contains(heroImageViewWalk1)) {
                        characterX = heroImageViewWalk1.getX();
                    } else if (gamePane.getChildren().contains(heroImageViewWalk2)) {
                        characterX = heroImageViewWalk2.getX();
                    }

                    if (characterX <= pillarStartX) {
                        flipCharacter();
                    }
                }
            }
        });



        gameScene.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY && Objects.equals(stage, "waiting")) {
                isReleased=true;
                isMousePressed.set(false);
                stickExtensionTimeline.get().stop();
                stage = "Falling";
                fallStick();
                walkOnStick();
            }
        });

        Stage stage = new Stage();
        stage.setScene(gameScene);
        stage.setTitle("ANIMAL_HERO_GAME");
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/StickHero/Images/one.png"))); // Replace with the path to your icon image
        stage.setResizable(false);
        stage.getIcons().add(iconImage);
        stage.show();
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
        stick.setStrokeWidth(3);


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
        double imageHeight = 30; // Assuming the image height is 30 units

        if (gamePane.getChildren().contains(heroImageView)) {
            if (!isNotFlipped) {
                heroImageView.setScaleY(1);
                heroImageView.setTranslateY(heroImageView.getTranslateY() - imageHeight);
            } else {
                heroImageView.setScaleY(-1);
                heroImageView.setTranslateY(heroImageView.getTranslateY() + imageHeight);
            }
        } else if (gamePane.getChildren().contains(heroImageViewWalk1)) {
            if (!isNotFlipped) {
                heroImageViewWalk1.setScaleY(1);
                heroImageViewWalk1.setTranslateY(heroImageViewWalk1.getTranslateY() - imageHeight);
            } else {
                heroImageViewWalk1.setScaleY(-1);
                heroImageViewWalk1.setTranslateY(heroImageViewWalk1.getTranslateY() + imageHeight);
            }
        } else if (gamePane.getChildren().contains(heroImageViewWalk2)) {
            if (!isNotFlipped) {
                heroImageViewWalk2.setScaleY(1);
                heroImageViewWalk2.setTranslateY(heroImageViewWalk2.getTranslateY() - imageHeight);
            } else {
                heroImageViewWalk2.setScaleY(-1);
                heroImageViewWalk2.setTranslateY(heroImageViewWalk2.getTranslateY() + imageHeight);
            }
        }

        isNotFlipped = !isNotFlipped;
    }


    private Rectangle createGradientRectangle(double x, double y, double width) {
        Rectangle rectangle = new Rectangle(x, y, width, 100);
        rectangle.setFill(Color.BLACK); // Set the fill color to black

        return rectangle;
    }




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

        // Calculate stick rotation angle

        // Rotate the stick by 90 degrees
        Rotate rotate = new Rotate(90, bottomX, bottomY);
        stick.getTransforms().clear();
        stick.getTransforms().add(rotate);

        // Set the end of the stick to remain parallel to the rectangle's top edge
        stick.setEndX(rotatedPillarX);
        stick.setEndY(pillar1Y - stickLength);

        // Set the start of the stick to the new rotated position (pillar's edge)
        stick.setStartX(rotatedPillarX);
        stick.setStartY(pillar1Y);
        stage = "walking";
    }

    private void characterFall() {
        stage = "falling";
        double fallDistance = 200;
        Duration duration = Duration.seconds(2);
        double initialY;
        if(gamePane.getChildren().contains(heroImageView)) {
            initialY = heroImageView.getY();
        } else if (gamePane.getChildren().contains(heroImageViewWalk1)) {
            initialY = heroImageViewWalk1.getY();
        }else{
            initialY = heroImageViewWalk2.getY();
        }

        double finalY = initialY + fallDistance;
        // Initialize the Timeline
        Timeline fallTimeline = new Timeline();
        KeyFrame fallKeyFrame ;
        KeyValue fallKeyValue;
        if(gamePane.getChildren().contains(heroImageView)) {
               fallKeyValue = new KeyValue(heroImageView.yProperty(), finalY);
        } else if (gamePane.getChildren().contains(heroImageViewWalk1)) {
            fallKeyValue = new KeyValue(heroImageViewWalk1.yProperty(), finalY);
        }else{
            fallKeyValue = new KeyValue(heroImageViewWalk2.yProperty(), finalY);
        }
        fallKeyFrame = new KeyFrame(duration, fallKeyValue);
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
        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/StickHero/Images/BG.jpg")), 300, 150, false, true);
        ImageView backgroundImageView = new ImageView(bgImage);
        backgroundImageView.setFitWidth(300);
        backgroundImageView.setFitHeight(150);


        // Message Label
        Label messageLabel = new Label("Do you want to revive and continue?");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Buttons
        Button reviveButton = createStyledButton("Revive");
        Button restartButton = createStyledButton("Restart");
        Button quitButton = createStyledButton("Quit");

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

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: " + "#4CAF50" + "; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #45a049;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + "#4CAF50" + ";"));
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

        heroImageViewWalk1.setFitWidth(30); // Set width of the image
        heroImageViewWalk1.setFitHeight(30); // Set height of the image
        heroImageViewWalk1.setX(75); // Set X-coordinate
        heroImageViewWalk1.setY(windowHeight - 135); // Set Y-coordinate

        heroImageViewWalk2.setFitWidth(30); // Set width of the image
        heroImageViewWalk2.setFitHeight(30); // Set height of the image
        heroImageViewWalk2.setX(75); // Set X-coordinate
        heroImageViewWalk2.setY(windowHeight - 135); // Set Y-coordinate
        gamePane.getChildren().remove(heroImageViewWalk1);
        gamePane.getChildren().remove(heroImageViewWalk2);
        if(!gamePane.getChildren().contains(heroImageView)) gamePane.getChildren().add(heroImageView);
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



    private void walkOnStick() {
        AtomicInteger walk = new AtomicInteger();
        double stickLength = Stick_length;
        double startX = stick.getStartX();
        double finalX;



        // Calculate finalX based on stick length and pillar position
        if (stickLength + startX >= Pillar1.getX() && stickLength + startX <= Pillar1.getX() + Pillar1.getWidth()) {
            finalX = Pillar1.getX() + Pillar1.getWidth() - 25;
        } else {
            finalX = startX + stickLength - 25;
        }

        final double increment = 1;
        final double[] currentX = { heroImageView.getX() };

        Timeline timeline = new Timeline();

        // Add key frame to the Timeline for animating the movement
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(5), e -> {
                    currentX[0] += increment;

                    if (!isNotFlipped && cherry.getXPosition() == currentX[0] ) {
                        gamePane.getChildren().remove(cherry);
                        cherry.collectCherry(gamePane);
                    }

                    if (currentX[0] == Pillar1.getX() && !isNotFlipped) {
                        timeline.stop();
                        characterFall();
                    }

                    if (currentX[0] <= finalX) {
                        if(walk.get()==0){
                            int x,y;
                            int translateX ;
                            int translateY ;
                            int scaleX ;
                            int scaleY ;

                            if(gamePane.getChildren().contains(heroImageView)){
                                gamePane.getChildren().remove(heroImageView);
                                x = (int) heroImageView.getX();
                                y = (int) heroImageView.getY();
                                translateX = (int) heroImageView.getTranslateX();
                                translateY = (int) heroImageView.getTranslateY();
                                scaleX = (int) heroImageView.getScaleX();
                                scaleY = (int) heroImageView.getScaleY();
                            }else{
                                gamePane.getChildren().remove(heroImageViewWalk2);
                                x = (int) heroImageViewWalk2.getX();
                                y = (int) heroImageViewWalk2.getY();
                                translateX = (int) heroImageViewWalk2.getTranslateX();
                                translateY = (int) heroImageViewWalk2.getTranslateY();
                                scaleX = (int) heroImageViewWalk2.getScaleX();
                                scaleY = (int) heroImageViewWalk2.getScaleY();
                            }

                            heroImageViewWalk1.setX(x);
                            heroImageViewWalk1.setY(y);

                            heroImageViewWalk1.setFitWidth(30); // Set width of the image
                            heroImageViewWalk1.setFitHeight(30); // Set height of the image
                            heroImageViewWalk1.setScaleY(scaleY);
                            heroImageViewWalk1.setScaleX(scaleX);
                            heroImageViewWalk1.setTranslateX(translateX);
                            heroImageViewWalk1.setTranslateY(translateY);

                            gamePane.getChildren().add(heroImageViewWalk1);
                            heroImageViewWalk1.setX(currentX[0]);
                            walk.set(1);
                        }else if(walk.get()==1){
                            int x = (int) heroImageViewWalk1.getX();
                            int y = (int) heroImageViewWalk1.getY();
                            int translateX = (int) heroImageViewWalk1.getTranslateX();
                            int translateY = (int) heroImageViewWalk1.getTranslateY();
                            int scaleX = (int) heroImageViewWalk1.getScaleX();
                            int scaleY = (int) heroImageViewWalk1.getScaleY();
                            gamePane.getChildren().remove(heroImageViewWalk1);
                            heroImageViewWalk2.setX(x);
                            heroImageViewWalk2.setY(y);

                            heroImageViewWalk2.setFitWidth(30); // Set width of the image
                            heroImageViewWalk2.setFitHeight(30); // Set height of the image
                            heroImageViewWalk2.setScaleY(scaleY);
                            heroImageViewWalk2.setScaleX(scaleX);
                            heroImageViewWalk2.setTranslateX(translateX);
                            heroImageViewWalk2.setTranslateY(translateY);
                            gamePane.getChildren().add(heroImageViewWalk2);
                            heroImageViewWalk2.setX(currentX[0]);
                            walk.set(0);
                        }

                        //heroImageView.setX(currentX[0]);
                        isReleased = false;
                    } else {
                        int x = (int) heroImageViewWalk1.getX();
                        int y = (int) heroImageViewWalk1.getY();
                        int translateX = (int) heroImageViewWalk1.getTranslateX();
                        int translateY = (int) heroImageViewWalk1.getTranslateY();
                        int scaleX = (int) heroImageViewWalk1.getScaleX();
                        int scaleY = (int) heroImageViewWalk1.getScaleY();
                        gamePane.getChildren().remove(heroImageViewWalk1);
                        if(gamePane.getChildren().contains(heroImageViewWalk2)){
                            x = (int) heroImageViewWalk2.getX();
                            y = (int) heroImageViewWalk2.getY();
                            translateX = (int) heroImageViewWalk2.getTranslateX();
                            translateY = (int) heroImageViewWalk2.getTranslateY();
                            scaleX = (int) heroImageViewWalk2.getScaleX();
                            scaleY = (int) heroImageViewWalk2.getScaleY();
                            gamePane.getChildren().remove(heroImageViewWalk2);
                        }
                        heroImageView.setX(finalX);
                        if(!gamePane.getChildren().contains(heroImageView)){
                            heroImageView.setX(x);
                            heroImageView.setY(y);

                            heroImageView.setFitWidth(30); // Set width of the image
                            heroImageView.setFitHeight(30); // Set height of the image
                            heroImageView.setScaleY(scaleY);
                            heroImageView.setScaleX(scaleX);
                            heroImageView.setTranslateX(translateX);
                            heroImageView.setTranslateY(translateY);
                            gamePane.getChildren().add(heroImageView);
                        }
                        timeline.stop();
                        stage = "waiting";

                        if (stickLength + startX >= Pillar1.getX() && stickLength + startX <= Pillar1.getX() + Pillar1.getWidth()) {
                            performPillarTransition();

                            if ((stickLength + startX >= Strip0.getX() && stickLength + startX <= Strip0.getX()+10) || (stickLength + startX >= Strip1.getX() && stickLength + startX <= Strip1.getX()+10)) {
                                // Stick is in the range of strip0, increase the score by +2
                                showBonusText();
                                showBonusTextAnimation(true);
                                currentScore += 2;
                            }
                            else {
                                currentScore++;
                                showBonusTextAnimation(false);
                            }
                            score.updateScore(currentScore);
                        } else {
                            timeline.stop();
                            characterFall();
                        }
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    private void showBonusText() {

            Text bonusText = new Text("Perfect!");
            bonusText.setTextAlignment(TextAlignment.CENTER);
            bonusText.setFill(Color.BLACK);
            bonusText.setFont(Font.font(30));

            // Position the bonus text (you may need to adjust the coordinates)
            bonusText.setLayoutX(250);
            bonusText.setLayoutY(111);

        // Add the bonus text to your gamePane
        gamePane.getChildren().add(bonusText);

        // Set up a timeline to fade out and remove the bonus text after a short duration
        Timeline bonusTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), new KeyValue(bonusText.opacityProperty(), 0))
        );
        bonusTimeline.setOnFinished(event -> gamePane.getChildren().remove(bonusText));
        bonusTimeline.play();
    }
    // Function to show bonus text pop-up with animation
    private void showBonusTextAnimation(Boolean value) {
        Text bonusText;
        if(value)  bonusText = new Text("+2");
        else bonusText = new Text("+1");
        bonusText.setFill(Color.BLACK);
        bonusText.setFont(Font.font(20));

        // Position the bonus text at the center of the strip
        double bonusTextX = (Strip1.getX() + Strip1.getX() + 10) / 2 - bonusText.getBoundsInLocal().getWidth() / 2;
        double bonusTextY = Strip1.getY() - 20; // Adjust the Y position as needed

        bonusText.setLayoutX(bonusTextX);
        bonusText.setLayoutY(bonusTextY);

        // Add the bonus text to your gamePane
        gamePane.getChildren().add(bonusText);

        // Create a translation animation for the bonus text
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), bonusText);
        translateTransition.setByY(-50); // Adjust the distance the text moves up
        translateTransition.setInterpolator(Interpolator.EASE_OUT);

        // Set up a timeline to fade out and remove the bonus text after a short duration
        Timeline bonusTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), new KeyValue(bonusText.opacityProperty(), 0))
        );
        bonusTimeline.setOnFinished(event -> gamePane.getChildren().remove(bonusText));

        // Play the translation animation and fade-out timeline in sequence
        translateTransition.setOnFinished(event -> bonusTimeline.play());
        translateTransition.play();
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
        int x = (int) (this.Pillar1.getX()+this.Pillar1.getWidth()+randomNumber);
        Rectangle newPillar = createGradientRectangle(x, windowHeight - 100, randomNumber1);

        Rectangle newStrip =new Rectangle((double) (2 * x + randomNumber1) /2-5,windowHeight-100,10,7);
        newStrip.setFill(Color.DARKGREEN);

        Timeline timeline = new Timeline();
        gamePane.getChildren().addAll(newPillar,newStrip);
        final double[] currentX = { this.Pillar0.getX(), this.Pillar1.getX(), this.stick.getStartX(),newPillar.getX(),
                stickLength+stick.getStartX(),
                this.cherry.getXPosition(),this.Strip0.getX(),this.Strip1.getX(),newStrip.getX()}; // Current X-coordinate of the hero
        Duration seconds = Duration.seconds(1);
        System.out.println(seconds);

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
                    currentX[6] -= decrement;
                    currentX[7] -=decrement;
                    currentX[8] -=decrement;
                    totalDecrement[0] += decrement;

                    if (currentX[1]!=100-Pillar1.getWidth()) {
                        newPillar.setX(currentX[3]);
                        this.Pillar0.setX(currentX[0]);
                        this.Pillar1.setX(currentX[1]);
                        this.cherry.setXPosition(currentX[5]);
                        this.heroImageView.setX(currentX[1]+ this.Pillar1.getWidth()- 25);
                        this.stick.setTranslateX(-totalDecrement[0]);
                        this.Strip0.setX(currentX[6]);
                        this.Strip1.setX(currentX[7]);
                        newStrip.setX(currentX[8]);
                    } else {
                        this.Strip0.setX(currentX[6]);
                        this.Strip1.setX(currentX[7]);
                        newStrip.setX(currentX[8]);
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
                        this.Strip0 = this.Strip1;
                        this.Strip1 = newStrip;
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




    public void setCurrentScore(int currentScore) {
        StickHeroController.currentScore = currentScore;
    }

    public void optionsAction() {
    }

    

    public void quitAction() {
        Platform.exit();
    }


}
