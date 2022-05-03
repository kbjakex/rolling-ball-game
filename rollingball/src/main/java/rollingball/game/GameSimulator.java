package rollingball.game;

import java.util.List;

import rollingball.functions.EvalContext;
import rollingball.functions.Function;
import rollingball.game.FunctionStorage.Graph;

/**
 * Contains the game state for a level, and the update logic.
 * The purpose of the class is to simulate the path of the ball to
 * figure out if the ball will end up reaching the flag using
 * the entered equations.
 */
public final class GameSimulator {
    /**
     * Width of the playing field, to both directions from the origin.
     * Total width is therefore technically twice this.
     */
    public static final int LEVEL_WIDTH = 8; // -8..8
    /**
     * Height of the playing field, to both directions from the origin.
     * Total height is therefore technically twice this.
     */
    public static final int LEVEL_HEIGHT = 8; // -8..8

    private static final double FLAG_SIZE = 1.0;

    /**
     * A callback for communicating when the simulation stops for any reason.
     */
    @FunctionalInterface
    public interface GameEndCallback {
        /**
         * Called when the game ends.
         * @param victory true if the ball reached the flag, false if it died or escaped the screen
         * @param playTimeSeconds the time, in seconds, the ball spent in the game
         */
        void onGameEnd(boolean victory, double playTimeSeconds);
    }

    private static final double BALL_SPEED = 1.5; // grid units per second
    private static final double GRAVITY = 0.67; // acceleration; grid units per second^2

    private final FunctionStorage graphs;

    private Level level;

    private double simulationTimeSeconds;
    private boolean isPlaying;
    private final GameEndCallback playEndCallback;

    private Ball theBall;

    /**
     * Creates a new simulator for the given level. 
     * @param graphs the function storage
     * @param playEndCallback called when the game ends
     */
    public GameSimulator(Level level, GameEndCallback playEndCallback) {
        this.graphs = new FunctionStorage();
        this.simulationTimeSeconds = 0.0;
        this.isPlaying = false;
        this.level = level;
        this.theBall = new Ball(level);
        this.playEndCallback = playEndCallback;
    }

    /**
     * Starts or stops the simulation. The simulation is reset each time this is called.
     */
    public void togglePlaying() {
        this.isPlaying = !this.isPlaying;
        this.theBall.reset(this.level);
    }

    /**
     * Returns the time the simulation has been running, or 0.0 if the simulation is not running.
     * @return the time in seconds
     */
    public double getPlayingTimeSeconds() {
        if (!this.isPlaying) {
            return 0.0;
        }
        return simulationTimeSeconds;
    }

    /**
     * Returns the ball. Never null, even if the simulation is not active.
     * @return the ball
     */
    public Ball getBall() {
        return theBall;
    }

    public Level getLevel() {
        return this.level;
    }

    /**
     * Adds a graph to the simulation.
     * @param graph the graph to add
     * @see {@link rollingball.game.FunctionStorage#addGraph(Graph)}
     */
    public Graph addGraph(Function fn) {
        return graphs.addGraph(fn);
    }

    /**
     * Removes a graph from the simulation.
     * @param graph the graph to remove
     * @see {@link rollingball.game.FunctionStorage#removeGraph()}
     */
    public void removeGraph(Graph graph) {
        graphs.removeGraph(graph);
    }

    /**
     * Returns the list of graphs currently in use.
     * @see {@link rollingball.game.FunctionStorage#getGraphs()}
     */
    public List<Graph> getGraphs() {
        return graphs.getGraphs();
    }

    /**
     * Advances the simulation by one simulation tick.
     */
    public void update() {
        if (this.isPlaying) {
            var timeSeconds = simulationTimeSeconds;
            var deltaTime = 1.0 / 60.0; // 60 FPS

            this.level.onUpdate(timeSeconds, deltaTime);

            updateBallPos(timeSeconds, deltaTime);
            if (checkShouldDie()) {
                togglePlaying();
                playEndCallback.onGameEnd(false, timeSeconds);
            } else if (checkIsTouchingFlag()) {
                togglePlaying();
                playEndCallback.onGameEnd(true, timeSeconds);
            }

            this.simulationTimeSeconds += deltaTime;
        }
    }

    private boolean checkShouldDie() {
        if (theBall.y < -LEVEL_HEIGHT - 1 || theBall.y > LEVEL_HEIGHT + 1) {
            return true;
        }

        for (var obstacle : level.getObstacles()) {
            if (obstacle.checkWouldKill(theBall)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsTouchingFlag() {
        var end = level.getEnd();
        var r = Ball.RADIUS;
        return end.x() <= theBall.x + r && theBall.x - r <= end.x() + FLAG_SIZE &&
                end.y() <= theBall.y + r && theBall.y - r <= end.y() + FLAG_SIZE;
    }

    private void updateBallPos(double time, double deltaTime) {
        var ctx = new EvalContext(time);

        var nextY = theBall.y - computeGravity(time) - Ball.RADIUS;
        var nextX = theBall.x + computeHorizontalSpeed(deltaTime, ctx);

        Graph curve = null;
        for (var graph : getGraphs()) {
            var fn = graph.geFunction();

            ctx.x = theBall.x;
            var y = fn.eval(ctx);
            if (Double.isNaN(y) || y - 0.005 > theBall.y || !fn.canEval(ctx)) {
                continue;
            }

            var adjustedY = GoldenSectionSearch.computeBallYOnCurve(fn, ctx, theBall.x);
            if (nextY < adjustedY) {
                nextY = adjustedY;
                curve = graph;
                theBall.lastCollisionTimestamp = time;
            }
        }
        theBall.x = nextX;
        theBall.y = nextY + Ball.RADIUS;
        theBall.collidingCurve = curve;
    }

    private double computeHorizontalSpeed(double deltaTime, EvalContext ctx) {
        if (theBall.collidingCurve == null) {
            return BALL_SPEED * deltaTime;
        }

        var dy = computeApproxCurveDerivative(theBall.collidingCurve, theBall.x, ctx);

        // By max(0, ...)'ing, only upwards slopes affect the speed. This is "unfair" to
        // the player,
        // but I hadn't realized that *not* doing that would make any curve equally fast
        // to travel, regardless of speed.
        // That is because, for example, `integral(derivative(x^2), -p, p) = 0` for any
        // p, and same goes for sin(), etc;
        // any function that start and ends from some height h.
        // Really, the speed should be calculated such that a curve of length x takes
        // the same amount of time to travel
        // as a straight, flat line of length x - as in, only curve length should affect
        // the total time.
        return (BALL_SPEED - 0.5 * Math.max(0, dy)) * deltaTime;
    }

    private double computeGravity(double time) {
        return Math.max(0.1, Math.min(0.5, time - theBall.lastCollisionTimestamp)) * GRAVITY;
    }

    private static double computeApproxCurveDerivative(Graph graph, double x, EvalContext ctx) {
        var dx = 0.01;
        var y1 = graph.geFunction().evalAt(x - dx / 2.0, ctx);
        var y2 = graph.geFunction().evalAt(x + dx / 2.0, ctx);
        return (y2 - y1) / dx;
    }
}