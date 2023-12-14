package com.example.saadstickhero;

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

public class StickHeroGame extends Application {
    public static boolean snowflakesActive = true;
    @Override
    public void start(Stage primaryStage) throws IOException {
        StickHeroController controller = new StickHeroController();
        primaryStage.setScene(new Scene(createContent(controller,primaryStage)));
        primaryStage.show();
    }
    private void showLoadPopup(StickHeroController controller) throws IOException {
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
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/saadstickhero/Images/BG.jpg"), 600, 700, false, true);
        VBox box = new VBox(5,
                new CircleMenuItem("PLAY", () -> {
                    controller.stopSnowflakes(); // Call the method in the controller to stop snowflakes
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
                        showLoadPopup(controller);
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

    private static class MenuItem extends StackPane{
        MenuItem(String name, Runnable action){
            LinearGradient gradient = new LinearGradient(0,0.5,1,0.5,true, CycleMethod.NO_CYCLE,new Stop(0.1,Color.web("black",0.75)),new Stop(1.0,Color.web("black",0.15)));
            Rectangle bg0  = new Rectangle(250,30,gradient);
            Rectangle bg1 = new Rectangle(250,30,Color.web("black",0.2));
            FillTransition ft = new FillTransition(Duration.seconds(0.33),bg1,Color.web("black",0.2),Color.web("white",0.69));
            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);
            hoverProperty().addListener((o,oldValue, isHovering) -> {
                if(isHovering){
                    ft.playFromStart();
                }
                else{
                    ft.stop();
                    bg1.setFill(Color.web("black",0.2));
                }
            });
            Rectangle line = new Rectangle(5,30);
            line.widthProperty().bind(Bindings.when(hoverProperty()).then(8).otherwise(5));
            line.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.RED).otherwise(Color.GRAY));
            Text text = new Text(name);
            text.setFont(Font.font(22.0));
            text.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.WHITE).otherwise(Color.GRAY));
            setOnMouseClicked(e -> action.run());
            setOnMousePressed(e -> bg0.setFill(Color.LIGHTBLUE));
            setOnMouseReleased(e -> bg0.setFill(gradient));
            setAlignment(Pos.CENTER_LEFT);
            HBox box = new HBox(15,line,text );
            box.setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(bg0,bg1,box);
        }
    }


}
