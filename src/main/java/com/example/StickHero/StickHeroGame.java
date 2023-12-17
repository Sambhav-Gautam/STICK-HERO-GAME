package com.example.StickHero;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class StickHeroGame extends Application {
    @Override
    public void start(Stage primaryStage) {
        StickHeroController controller = new StickHeroController();
        primaryStage.setScene(new Scene(createContent(controller,primaryStage)));
        primaryStage.show();
    }
    private void showLoadPopup() throws IOException {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Load Game");


        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label usernameLabel = new Label("Username");
        Label scoreLabel = new Label("Cherries");
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(scoreLabel, 1, 0);

        GameStateManager gameStateManager = new GameStateManager();
        int row = 1;
        for (Map.Entry<String, Integer> entry : gameStateManager.loadGameStates().entrySet()) {
            String username = entry.getKey();
            int score = entry.getValue();
            Label usernameValue = new Label(username);
            Label scoreValue = new Label(Integer.toString(score));
            gridPane.add(usernameValue, 0, row);
            gridPane.add(scoreValue, 1, row);

            row++;
        }

        Scene popupScene = new Scene(gridPane);
        popupStage.setScene(popupScene);
        popupStage.show();
    }
    private Parent createContent(StickHeroController controller,Stage primary){
        Pane root = new Pane();
        root.setPrefSize(600, 700);
        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/StickHero/Images/BG.jpg")), 600, 700, false, true);
        VBox box = new VBox(5,
                new CircleMenuItem("PLAY", () -> {

                    try {
                        controller.init(); // Call init method or any other actions in the controller
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    primary.hide();
                }),
                new StickHeroGame.MenuItem("SETTINGS", controller::optionsAction),
                new StickHeroGame.MenuItem("LOAD", () -> {
                    try {
                        showLoadPopup();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }),
                new StickHeroGame.MenuItem("QUIT", controller::quitAction)
        );
        box.setBackground(new Background(new BackgroundFill(Color.web("black", 0), null, null)));
        Text text0 = new Text("STICK");
        Text text1 = new Text("HERO");
        text0.setFont(Font.font("Arial", FontWeight.BOLD, 70));
        text1.setFont(Font.font("Arial", FontWeight.BOLD, 70));
        text0.setFill(Color.RED);
        text1.setFill(Color.BLUE);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.rgb(50, 50, 50, 0.8));
        text0.setEffect(dropShadow);
        text1.setEffect(dropShadow);
        text0.setTextAlignment(TextAlignment.CENTER);
        text1.setTextAlignment(TextAlignment.CENTER);
        text1.setTranslateX(330);
        text1.setTranslateY(70);
        text0.setTranslateX(100);
        text0.setTranslateY(70);
        box.setTranslateX(200);
        box.setTranslateY(200);
        root.getChildren().addAll(new ImageView(bgImage), box, text1, text0);
        return root;
    }

    private static class CircleMenuItem extends StackPane {
        public CircleMenuItem(String name, Runnable action) {
            LinearGradient gradient = new LinearGradient(
                    0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0.1, Color.web("black", 0.75)),
                    new Stop(1.0, Color.web("black", 0.15))
            );
            Circle circle = new Circle(100, gradient);
            circle.setFill(Color.BLACK);
            circle.setStroke(Color.BLACK);

            Circle smallCircle = new Circle(100);
            smallCircle.setFill(null);
            smallCircle.setStroke(Color.RED);
            smallCircle.setStrokeWidth(4);
            smallCircle.setVisible(false);

            FillTransition ft = new FillTransition(Duration.seconds(0.33), circle);
            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);
            ft.setFromValue(Color.BLACK);
            ft.setToValue(Color.LIGHTBLUE);


            circle.setOnMouseEntered(event -> {
                ft.play();
                smallCircle.setVisible(true);
            });

            circle.setOnMouseExited(event -> {
                ft.stop();
                circle.setFill(Color.BLACK);
                smallCircle.setVisible(false);
            });

            Text text = new Text(name);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            text.setFill(Color.WHITE);
            text.fillProperty().bind(Bindings.when(circle.hoverProperty()).then(Color.WHITE).otherwise(Color.GRAY));
            setOnMouseClicked(e -> action.run());
            setAlignment(Pos.CENTER);
            getChildren().addAll(circle, text, smallCircle);
        }
    }

    private static class MenuItem extends StackPane {
        private static final double WIDTH = 250;
        private static final double HEIGHT = 30;
        private static final Duration TRANSITION_DURATION = Duration.seconds(0.33);

        public MenuItem(String name, Runnable action) {
            LinearGradient gradient = new LinearGradient(0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0.1, Color.web("black", 0.75)),
                    new Stop(1.0, Color.web("black", 0.15)));

            Rectangle bg0 = createRectangle(WIDTH, gradient);
            Rectangle bg1 = createRectangle(WIDTH, gradient);
            FillTransition ft = createFillTransition(bg1,
                    Color.web("black", 0.2), Color.web("white", 0.69));
            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);

            Rectangle line = createRectangle(5, gradient);
            bindHoverProperties(line);

            Text text = createText(name);
            bindHoverProperties(text);

            setOnMouseClicked(e -> action.run());
            setOnMousePressed(e -> bg0.setFill(Color.LIGHTBLUE));
            setOnMouseReleased(e -> bg0.setFill(gradient));

            setAlignment(Pos.CENTER_LEFT);
            HBox box = new HBox(15, line, text);
            box.setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg0, bg1, box);

            hoverProperty().addListener((o, oldValue, isHovering) -> handleHoverChange(isHovering, ft, bg1));
        }

        private Rectangle createRectangle(double width, LinearGradient fill) {
            Rectangle rectangle = new Rectangle(width, MenuItem.HEIGHT, fill);
            rectangle.setStroke(Color.BLACK);
            return rectangle;
        }

        private FillTransition createFillTransition(Rectangle rectangle, Color fromValue, Color toValue) {
            FillTransition fillTransition = new FillTransition(MenuItem.TRANSITION_DURATION, rectangle, fromValue, toValue);
            fillTransition.setAutoReverse(true);
            fillTransition.setCycleCount(Integer.MAX_VALUE);
            return fillTransition;
        }

        private void bindHoverProperties(Text text) {
            text.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.WHITE).otherwise(Color.GRAY));
        }

        private void bindHoverProperties(Rectangle rectangle) {
            rectangle.widthProperty().bind(Bindings.when(hoverProperty()).then(8).otherwise(5));
            rectangle.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.RED).otherwise(Color.GRAY));
        }

        private void handleHoverChange(boolean isHovering, FillTransition ft, Rectangle bg1) {
            if (isHovering) {
                ft.playFromStart();
            } else {
                ft.stop();
                bg1.setFill(Color.web("black", 0.2));
            }
        }

        private Text createText(String content) {
            Text text = new Text(content);
            text.setFont(Font.font(22.0));
            return text;
        }
    }
}
