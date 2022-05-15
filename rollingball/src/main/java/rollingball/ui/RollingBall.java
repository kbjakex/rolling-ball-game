package rollingball.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.stage.Stage;
import rollingball.Main;
import rollingball.dao.FileUserProgressDao;
import rollingball.dao.UserProgressDao;
import rollingball.game.LevelBlueprint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.*;

/**
 * The main class for the UI.
 */
public final class RollingBall extends Application {
    private UserProgressDao progressDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        loadOrCreateSaveFile();
    }

    private void loadOrCreateSaveFile() throws IOException {
        var saveFile = new File("rollingballdata.dat");
        try {
            progressDao = FileUserProgressDao.loadFromFile(saveFile);
        } catch (FileNotFoundException ex) {
            progressDao = FileUserProgressDao.empty(saveFile);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {
        if (progressDao == null) {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load progress data");
            alert.setContentText("The save file \"rollingballdata.dat\" was found but could not be loaded (the file is corrupted).\n" +
                    "Do you want to override the file?");
            alert.showAndWait();
            
            if (alert.getResult() != ButtonType.OK) {
                primaryStage.close();
                return;
            }

            progressDao = FileUserProgressDao.empty(new File("rollingballdata.dat"));
        }

        primaryStage.setTitle("Rolling Ball Game");
        primaryStage.setScene(createMainMenuScene(primaryStage));
        primaryStage.show();
    }

    private Scene createMainMenuScene(Stage primaryStage) {
        primaryStage.setResizable(false);

        var mainMenu = new VBox();
        var scene = new Scene(mainMenu, 800, 800);

        var sceneStack = new Stack<Supplier<Scene>>();

        var titleLabel = createLabel("Rolling Ball Game", 50, 150);
        titleLabel.setUnderline(true);

        var startButton = createButton("Start", 30, 20);
        startButton.setOnAction(e -> {
            sceneStack.push(() -> scene);

            var nextLevel = progressDao.getNextUncompletedLevel();
            if (nextLevel == null) { // game completed; start from level 1 again
                nextLevel = LevelBlueprint.LEVEL_1;
            }
            primaryStage
                    .setScene(GameRenderer.createGameScene(primaryStage, sceneStack, progressDao, nextLevel.createInstance()));
        });
        startButton.requestFocus();

        var levelsButton = createButton("Level Select", 30, 20);
        levelsButton.setOnAction(e -> {
            sceneStack.push(() -> scene);
            primaryStage.setScene(createLevelSelectScene(primaryStage, sceneStack));
        });

        var quitButton = createButton("Quit", 30, 20);
        quitButton.setOnAction(e -> primaryStage.close());

        mainMenu.getChildren().addAll(
                titleLabel,
                startButton,
                levelsButton,
                quitButton);

        mainMenu.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainMenu.setAlignment(Pos.TOP_CENTER);

        return scene;
    }

    private Scene createLevelSelectScene(Stage primaryStage, Stack<Supplier<Scene>> sceneHistory) {
        var layout = new VBox();
        layout.setPrefSize(800, 800);
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        var scene = new Scene(layout, 800, 800);

        var backButton = new Button("Back");
        backButton.setPrefSize(64, 48);
        backButton.setFont(new Font("Arial", 14));
        backButton.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5.0), Insets.EMPTY)));
        backButton.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5.0),
                new BorderWidths(2.0))));
        backButton.setOnAction(e -> primaryStage.setScene(sceneHistory.pop().get()));
        HBox.setMargin(backButton, new Insets(10, 10, 10, 10));

        var top = new HBox(Spacers.createHSpacer(), backButton);
        top.setPrefHeight(100);

        var levels = new HBox();
        levels.setPadding(new Insets(50, 50, 50, 50));
        levels.setFocusTraversable(false);

        var lockImage = new Image(Main.class.getClassLoader().getResourceAsStream("lock.png"));
        var starImage = new Image(Main.class.getClassLoader().getResourceAsStream("star.png"));
        var noStarImage = new Image(Main.class.getClassLoader().getResourceAsStream("nostar.png"));
        var tint = new Image(Main.class.getClassLoader().getResourceAsStream("tint.png"));

        var completed = progressDao.getLevelCompletions();
        for (var level : LevelBlueprint.values()) {
            var levelPane = new BorderPane();

            var levelImg = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream(level.ordinal() + ".png")));

            var levelButton = levelImg;
            levelButton.setFitWidth(250);
            levelButton.setFitHeight(250);

            var buttonStack = new StackPane(levelButton);
            
            var completion = completed.stream().filter(c -> c.level() == level).findFirst().orElse(null);
            var starCount = completion == null ? 0 : (int)(completion.scorePercentage() * 3);
            var stars = new HBox();
            for (int i = 0; i < 3; i++) {
                var star = new ImageView(i < starCount ? starImage : noStarImage);
                star.setFitWidth(44);
                star.setFitHeight(44);
                stars.getChildren().add(star);
                HBox.setMargin(star, new Insets(0.0, 0.0, i == 1 ? 24.0 : 0.0, 0.0));
            }
            stars.setAlignment(Pos.CENTER);
            stars.setSpacing(10);
            stars.setVisible(false);
            
            levelPane.setTop(stars);

            if (completion == null && progressDao.getNextUncompletedLevel() != level) {
                levelButton.setDisable(true);

                var lockView = new ImageView(lockImage);
                lockView.setFitWidth(100);
                lockView.setFitHeight(100);
                buttonStack.getChildren().addAll(new ImageView(tint), lockView);
            } else if (progressDao.getNextUncompletedLevel() != level) {
                stars.setVisible(true);
            } else {
                buttonStack.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(2.0),
                        new BorderWidths(5.0))));
                levelButton.requestFocus();
            }

            levelPane.setCenter(buttonStack);

            var title = new Label(level.getName());
            title.setFont(new Font("Arial", 23));
            BorderPane.setAlignment(title, Pos.CENTER);
            BorderPane.setMargin(title, new Insets(10));
            levelPane.setBottom(title);
            HBox.setMargin(levelPane, new Insets(10, 50, 10, 0));
            
            levelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
                sceneHistory.add(() -> createLevelSelectScene(primaryStage, sceneHistory));
                primaryStage.setScene(GameRenderer.createGameScene(primaryStage, sceneHistory, progressDao, level.createInstance()));                
            });

            levels.getChildren().add(levelPane);
        }
        var levelView = new ScrollPane(levels);
        levelView.setPrefHeight(600);
        levelView.setBorder(Border.EMPTY);
        levelView.setFocusTraversable(false);

        var title = new Label("Level Select");
        title.setFont(new Font("Arial", 32));
        title.setTextFill(Color.BLACK);
        title.setAlignment(Pos.CENTER);
        HBox.setMargin(title, new Insets(10, 10, 10, 10));

        var subtitle = new Label("Completed " + progressDao.getLevelCompletions().size() + "/" + LevelBlueprint.values().length);
        subtitle.setFont(new Font("Arial", 26));
        subtitle.setTextFill(Color.BLACK);
        subtitle.setAlignment(Pos.CENTER);
        HBox.setMargin(subtitle, new Insets(10, 10, 10, 10));

        layout.getChildren().addAll(top, new HBox(Spacers.createHSpacer(), new VBox(title, subtitle), Spacers.createHSpacer()), Spacers.createVSpacer(), levelView,
                Spacers.createVSpacer());
        VBox.setMargin(levelView, new Insets(10, 10, 10, 10));

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
        if (this.progressDao != null) {
            this.progressDao.flushChanges();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
