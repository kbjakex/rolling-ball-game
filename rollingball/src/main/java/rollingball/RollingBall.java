package rollingball;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.*;

public class RollingBall extends Application {
    
    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rolling Ball Game");
        primaryStage.setScene(createMainMenuScene(primaryStage));
        primaryStage.show();
    }

    private Scene createMainMenuScene(Stage primaryStage) {
        var titleLabel = createLabel("Rolling Ball Game", 50, 150);
        titleLabel.setUnderline(true);

        var startButton = createButton("Start", 30, 20);
        startButton.setOnAction(e -> primaryStage.setScene(createGameScene(primaryStage)));

        var levelsButton = createButton("Level Select", 30, 20);
        levelsButton.setOnAction(e -> primaryStage.setScene(createLevelSelectScene(primaryStage)));

        var quitButton = createButton("Quit", 30, 20);
        quitButton.setOnAction(e -> primaryStage.close());

        var mainMenu = new VBox(
            titleLabel,
            startButton,
            levelsButton,
            quitButton
        );
        mainMenu.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainMenu.setPadding(new Insets(10.0));
        mainMenu.setAlignment(Pos.TOP_CENTER);
        
        return new Scene(mainMenu, 800, 800);
    }

    private Scene createGameScene(Stage primaryStage) {
        var pane = new Pane();
        pane.getChildren().add(new Label("Not done"));
        return new Scene(pane, 800, 800);
    }

    private Scene createLevelSelectScene(Stage primaryStage) {
        var pane = new Pane();
        pane.getChildren().add(new Label("Not done"));
        return new Scene(pane, 800, 800);
    }

    private Button createButton(String text, double size, double padding) {
        var label = new Button(text);
        label.setFont(new Font("Arial", size));
        label.setPadding(new Insets(padding));
        return label;
    }

    private Label createLabel(String text, double size, double padding) {
        var label = new Label(text);
        label.setFont(new Font("Arial", size));
        label.setPadding(new Insets(padding));
        return label;
    }

    @Override
    public void stop() throws Exception {
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
