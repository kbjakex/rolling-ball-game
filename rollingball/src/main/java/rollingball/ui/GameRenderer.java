package rollingball.ui;

import java.util.Stack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import rollingball.expressions.ExpressionParser;
import rollingball.expressions.Expressions;
import rollingball.expressions.ExpressionParser.ParserException;
import rollingball.expressions.Expressions.Expr;
import rollingball.gamestate.GameState;
import rollingball.gamestate.GoldenSectionSearch;
import rollingball.gamestate.Level;

public final class GameRenderer {
    private static final int GRAPH_AREA_WIDTH = 8; // -8..8
    private static final int GRAPH_AREA_HEIGHT = 8; // -8..8

    private static final double PX_PER_GRAPH_AREA_UNIT = 50.0;
    private static final double GRAPH_AREA_WIDTH_PX = GRAPH_AREA_WIDTH * PX_PER_GRAPH_AREA_UNIT;
    private static final double GRAPH_AREA_HEIGHT_PX = GRAPH_AREA_HEIGHT * PX_PER_GRAPH_AREA_UNIT;

    private final Canvas canvas;
    private final GraphicsContext graphics;

    private final GameState state;

    private GameRenderer(Canvas canvas, GameState state) {
        this.canvas = canvas;
        this.state = state;
        this.graphics = canvas.getGraphicsContext2D();
    }

    private void onFrame(ActionEvent event) {
        state.update();

        var canvasWidth = canvas.getWidth();
        var canvasHeight = canvas.getHeight();
        graphics.clearRect(0, 0, canvasWidth, canvasHeight);
        graphics.translate(canvasWidth / 2.0, canvasHeight / 2.0);

        drawGrid();
        drawGraphs();

        graphics.translate(-canvasWidth / 2, -canvasHeight / 2);
    }

    private void drawGraphs() {
        var evalCtx = new Expressions.EvalContext(state.getPlayingTimeMs() / 1000.0);
        var xStepSize = 2.0 / PX_PER_GRAPH_AREA_UNIT;

        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(2.0);
        for (var graph : state.getGraphs()) {
            graphics.setStroke(graph.color);
            graphics.beginPath();

            evalCtx.varX = -GRAPH_AREA_WIDTH;
            graphics.moveTo(-GRAPH_AREA_WIDTH_PX, -PX_PER_GRAPH_AREA_UNIT * graph.fn.evaluate(evalCtx));

            var renderX = -GRAPH_AREA_WIDTH_PX + 2;
            for (int i = 2; i <= GRAPH_AREA_WIDTH_PX; ++i) {
                var y = graph.fn.evaluate(evalCtx) * -PX_PER_GRAPH_AREA_UNIT; // up is negative in screen coords
                graphics.lineTo(renderX, y);

                evalCtx.varX += xStepSize;
                renderX += 2;
            }
            graphics.stroke();
        }

        graphics.setStroke(Color.BLACK);
        graphics.setFill(Color.GRAY);
        var ball = state.getBall();
        var diameter = GoldenSectionSearch.BALL_RADIUS * PX_PER_GRAPH_AREA_UNIT * 2.0;
        graphics.fillOval(
                ball.getX() * PX_PER_GRAPH_AREA_UNIT - diameter / 2.0,
                -ball.getY() * PX_PER_GRAPH_AREA_UNIT - diameter,
                diameter, diameter);
    }

    private void drawGrid() {
        graphics.setStroke(Color.LIGHTGRAY);
        graphics.setLineWidth(1.0);

        // Horizontal lines
        for (int i = -GRAPH_AREA_WIDTH; i <= GRAPH_AREA_WIDTH; ++i) {
            var x = i * PX_PER_GRAPH_AREA_UNIT;
            graphics.strokeLine(-GRAPH_AREA_WIDTH_PX, x, GRAPH_AREA_WIDTH_PX, x);
        }

        // Vertical lines
        for (int i = -GRAPH_AREA_HEIGHT; i <= GRAPH_AREA_HEIGHT; ++i) {
            var x = i * PX_PER_GRAPH_AREA_UNIT;
            graphics.strokeLine(x, -GRAPH_AREA_HEIGHT_PX, x, GRAPH_AREA_HEIGHT_PX);
        }

        // Coordinate axes
        graphics.setStroke(Color.DARKGRAY);
        graphics.setLineWidth(2.0);
        graphics.strokeLine(-GRAPH_AREA_WIDTH_PX, 0.0, GRAPH_AREA_WIDTH_PX, 0.0);
        graphics.strokeLine(0.0, -GRAPH_AREA_HEIGHT_PX, 0.0, GRAPH_AREA_HEIGHT_PX);
    }

    public static Scene createGameScene(Stage primaryStage, Stack<Scene> sceneHistory) {
        var state = new GameState(Level.LEVEL_1);

        var desiredWidth = 800;
        Math.max(primaryStage.getWidth(), 2 * GRAPH_AREA_WIDTH_PX);
        var desiredHeight = 800;
        Math.max(primaryStage.getHeight(), 2 * GRAPH_AREA_HEIGHT_PX + 60);

        var canvas = new Canvas(desiredWidth, desiredHeight);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> canvas.setWidth(newVal.doubleValue()));
        // primaryStage.heightProperty().addListener((obs, oldVal, newVal) ->
        // canvas.setHeight(newVal.doubleValue()));

        var canvasPane = new Pane(canvas);
        canvasPane.heightProperty().addListener((obs, oldVal, newVal) -> canvas.setHeight(newVal.doubleValue()));

        var equationList = new ListView<HBox>();
        VBox.setVgrow(equationList, Priority.ALWAYS);

        var addEquationButton = new Button("+");

        var equationInput = new TextField();
        HBox.setHgrow(equationInput, Priority.ALWAYS);
        HBox.setMargin(equationInput, new Insets(0, 10, 0, 0));
        // Make life easier by allowing enter instead of requiring user to click
        equationInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == javafx.scene.input.KeyCode.ENTER) {
                addEquationButton.fire();
            }
        });

        addEquationButton.setOnAction(e -> addExpression(equationInput, equationList, state));

        var equationControls = new HBox(equationInput, addEquationButton);
        equationControls.setPadding(new Insets(10.0));
        equationControls.setPrefWidth(Double.MAX_VALUE);

        var split = new SplitPane(canvasPane, new VBox(equationControls, equationList));
        split.setDividerPositions(1.0);
        split.setOrientation(Orientation.VERTICAL);

        var backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (!state.getGraphs().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Discard progress?");
                alert.setHeaderText(
                        "State saving functionality is not yet implemented and exiting will discard any current progress.");
                alert.setContentText("Exit anyways?");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.CANCEL) {
                    return;
                }
            }

            primaryStage.setScene(sceneHistory.pop());
        });

        var playButton = new Button("Play");
        playButton.setOnAction(e -> {
            if (playButton.getText() == "Play")
                playButton.setText("Stop");
            else
                playButton.setText("Play");

            state.togglePlaying();
        });

        var timeLabel = new Label("0.0");

        var topUi = new BorderPane();
        topUi.setLeft(playButton);
        topUi.setCenter(timeLabel);
        topUi.setRight(backButton);

        var root = new VBox(topUi, split);
        var scene = new Scene(root, desiredWidth, desiredHeight);

        var game = new GameRenderer(canvas, state);

        var timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000.0 / 60.0), game::onFrame));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        return scene;
    }

    private static void addExpression(TextField equationInput, ListView<HBox> equationList, GameState state) {
        var exprAsString = equationInput.getText();
        var expr = readExpression(exprAsString);
        if (expr == null) {
            return;
        }

        equationInput.clear();

        var graph = state.addGraph(expr);

        var equationLabel = new Label(exprAsString);
        equationLabel.setFont(new Font("Arial", 18));
        equationLabel.setTextFill(graph.color);
        equationLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(equationLabel, Priority.ALWAYS);

        var equationListEntry = new HBox();
        var removeEquationButton = new Button("Remove");
        removeEquationButton.setOnAction(ee -> {
            state.removeGraph(graph);
            equationList.getItems().remove(equationListEntry);
        });

        equationListEntry.getChildren().addAll(equationLabel, removeEquationButton);

        equationList.getItems().add(equationListEntry);
    }

    private static Expr readExpression(String expressionString) {
        if (expressionString.isEmpty()) {
            return null;
        }

        try {
            return ExpressionParser.parse(expressionString);
        } catch (ParserException ex) {
            var errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText(ex.getMessage());
            errorAlert.showAndWait();
            return null;
        }
    }
}
