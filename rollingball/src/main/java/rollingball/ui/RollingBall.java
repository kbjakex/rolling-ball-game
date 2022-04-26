package rollingball.ui;

import java.util.Stack;

import javafx.application.Application;
import javafx.stage.Stage;
import rollingball.gamestate.Level;
import rollingball.gamestate.Levels;
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
        primaryStage.setResizable(false);

        var mainMenu = new VBox();
        var scene = new Scene(mainMenu, 800, 800);
        
        var sceneStack = new Stack<Scene>();
        
        var titleLabel = createLabel("Rolling Ball Game", 50, 150);
        titleLabel.setUnderline(true);
        
        var startButton = createButton("Start", 30, 20);
        startButton.setOnAction(e -> {
            sceneStack.push(scene);
            primaryStage.setScene(GameRenderer.createGameScene(primaryStage, sceneStack, Levels.LEVEL_1.createInstance()));
        });
        startButton.requestFocus();

        var levelsButton = createButton("Level Select", 30, 20);
        levelsButton.setOnAction(e -> {
            sceneStack.push(scene);
            primaryStage.setScene(createLevelSelectScene(primaryStage, sceneStack));
        });

        var quitButton = createButton("Quit", 30, 20);
        quitButton.setOnAction(e -> primaryStage.close());

        mainMenu.getChildren().addAll(
            titleLabel,
            startButton,
            levelsButton,
            quitButton
        );

        mainMenu.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainMenu.setAlignment(Pos.TOP_CENTER);
        
        return scene;
    }

    private Scene createLevelSelectScene(Stage primaryStage, Stack<Scene> sceneHistory) {
        var pane = new Pane();
        pane.getChildren().add(new Label("Not done"));
        
        var scene = new Scene(pane, 800, 800);
        sceneHistory.push(scene);

        return scene;
    }

    private Button createButton(String text, double size, double padding) {
        var button = new Button(text);
        button.setFont(new Font("Arial", size));
        button.setPadding(new Insets(padding));
        button.backgroundProperty().set(Background.EMPTY);

        button.focusedProperty().addListener((obj, oldVal, newVal) -> {
            button.setText(button.isFocused() ? "> " + text + " <" : text);
        });

        button.hoverProperty().addListener((obj, oldVal, newVal) -> {
            button.setText(button.isHover() ? "> " + text + " <" : text);
        });

        button.pressedProperty().addListener((obj, oldVal, newVal) -> {
            button.setText(button.isPressed() ? ">" + text + "<" : text);
        });

        return button;
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
