package rollingball.ui;

import java.util.Stack;

import javafx.application.Application;
import javafx.stage.Stage;
import rollingball.gamestate.Levels;
import javafx.scene.Node;
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
        var layout = new VBox();
        layout.setPrefSize(800, 800);
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        var scene = new Scene(layout, 800, 800);
        
        var backButton = new Button("Back");
        backButton.setPrefSize(64, 48);
        backButton.setFont(new Font("Arial", 14));
        backButton.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5.0), Insets.EMPTY)));
        backButton.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5.0), new BorderWidths(2.0))));
        backButton.setOnAction(e -> primaryStage.setScene(sceneHistory.pop()));
        HBox.setMargin(backButton, new Insets(10, 10, 10, 10));

        var top = new HBox(createHSpacer(), backButton);
        top.setPrefHeight(100);

        var levels = new HBox();
        levels.setPadding(new Insets(50, 50, 50, 50));
        levels.setFocusTraversable(false);

        for (var level : Levels.values()) {
            var levelButton = new Button(level.name);
            levelButton.setPrefSize(250, 250);
            levelButton.setFont(new Font("Arial", 35));
            HBox.setMargin(levelButton, new Insets(10, 50, 10, 0));

            levelButton.setOnAction(e -> {
                sceneHistory.add(scene);
                primaryStage.setScene(GameRenderer.createGameScene(primaryStage, sceneHistory, level.createInstance()));
            });

            levels.getChildren().add(levelButton);
        }
        var levelView = new ScrollPane(levels);
        levelView.setPrefHeight(400);
        levelView.setBorder(Border.EMPTY);
        levelView.setFocusTraversable(false);

        var title = new Label("Level Select");
        title.setFont(new Font("Arial", 32));
        title.setTextFill(Color.BLACK);
        title.setAlignment(Pos.CENTER);
        HBox.setMargin(title, new Insets(10, 10, 10, 10));

        layout.getChildren().addAll(top, new HBox(createHSpacer(), title, createHSpacer()), createVSpacer(), levelView, createVSpacer());
        VBox.setMargin(levelView, new Insets(10, 10, 10, 10));

        return scene;
    }

    private Node createHSpacer() {
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private Node createVSpacer() {
        var spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
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
