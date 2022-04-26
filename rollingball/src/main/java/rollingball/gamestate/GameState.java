package rollingball.gamestate;

import java.util.List;
import java.util.function.Consumer;

import rollingball.functions.EvalContext;
import rollingball.functions.Function;
import rollingball.gamestate.FunctionStorage.Graph;

public final class GameState {
    public static final class Ball {
        private double x;
        private double y;
        private Graph collidingCurve;

        private double lastCollisionTimestamp;

        public Ball(Level level) {
            reset(level);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public void reset(Level level) {
            this.x = level.startX;
            this.y = level.startY;
            this.lastCollisionTimestamp = 0.0;
        }
    }

    private static final double BALL_SPEED = 1.5; // grid units per second
    private static final double GRAVITY = 0.67; // acceleration; grid units per second^2

    private final FunctionStorage graphs;

    private Level level;

    private double startTimeMs;
    private boolean isPlaying;
    private final Consumer<Boolean> playEndCallback;

    private double timeOnLastUpdate;

    private Ball theBall;

    public GameState(Level level, Consumer<Boolean> playEndCallback) {
        this.graphs = new FunctionStorage();
        this.startTimeMs = 0.0;
        this.timeOnLastUpdate = 0.0;
        this.isPlaying = false;
        this.level = level;
        this.theBall = new Ball(level);
        this.playEndCallback = playEndCallback;
    }

    public void togglePlaying() {
        this.isPlaying = !this.isPlaying;
        if (this.isPlaying) {
            this.startTimeMs = System.nanoTime() / 1_000_000.0;
            this.timeOnLastUpdate = 0.0;
        }
        this.theBall.reset(this.level);
    }

    public double getPlayingTimeMs() {
        if (!this.isPlaying) {
            return 0.0;
        }
        return System.nanoTime() / 1_000_000.0 - this.startTimeMs;
    }

    public Ball getBall() {
        return theBall;
    }

    public Level getLevel() {
        return this.level;
    }

    public Graph addGraph(Function fn) {
        return graphs.addGraph(fn);
    }

    public void removeGraph(Graph graph) {
        graphs.removeGraph(graph);
    }

    public List<Graph> getGraphs() {
        return graphs.getGraphs();
    }

    public void update() {
        if (this.isPlaying) {
            var timeSeconds = (System.nanoTime() / 1_000_000.0 - this.startTimeMs) / 1000.0;
            var deltaTime = timeSeconds - this.timeOnLastUpdate;

            updateBallPos(timeSeconds, deltaTime);
            if (theBall.y < -10.0) {
                togglePlaying();
                playEndCallback.accept(false);
            }
            else if (checkIsTouchingFlag()) {
                togglePlaying();
                playEndCallback.accept(true);
            }

            this.timeOnLastUpdate = timeSeconds;
        }
    }

    private boolean checkIsTouchingFlag() {
        var dx = theBall.x - level.endX;
        var dy = theBall.y - level.endY;
        return dx*dx + dy*dy < 0.25;
    }

    private void updateBallPos(double time, double deltaTime) {
        var ctx = new EvalContext(time);

        var nextY = theBall.y - computeGravity(time);
        var nextX = theBall.x + computeHorizontalSpeed(deltaTime, ctx);

        Graph curve = null;
        for (var graph : getGraphs()) {
            ctx.x = theBall.x;
            var y = graph.fn.eval(ctx);
            if (Double.isNaN(y) || y - 0.005 > theBall.y || !graph.fn.canEval(ctx)) {
                continue;
            }

            var adjustedY = GoldenSectionSearch.computeBallYOnCurve(graph.fn, ctx, theBall.x);
            if (nextY < adjustedY) {
                nextY = adjustedY;
                curve = graph;
                theBall.lastCollisionTimestamp = time;
            }
        }
        theBall.x = nextX;
        theBall.y = nextY;
        theBall.collidingCurve = curve;
    }

    private double computeHorizontalSpeed(double deltaTime, EvalContext ctx) {
        if (theBall.collidingCurve == null) {
            return BALL_SPEED * deltaTime;
        }

        var dy = computeApproxCurveDerivative(theBall.collidingCurve, theBall.x, ctx);

        // By max(0, ...)'ing, only upwards slopes affect the speed. This is "unfair" to the player,
        // but I hadn't realized that *not* doing that would make any curve equally fast to travel, regardless of speed.
        // That is because, for example, `integral(derivative(x^2), -p, p) = 0` for any p, and same goes for sin(), etc;
        // any function that start and ends from some height h.
        // Really, the speed should be calculated such that a curve of length x takes the same amount of time to travel
        // as a straight, flat line of length x - as in, only curve length should affect the total time.
        return (BALL_SPEED - 0.5*Math.max(0, dy)) * deltaTime;
    }

    private double computeGravity(double time) {
        return Math.max(0.1, Math.min(0.5, time - theBall.lastCollisionTimestamp)) * GRAVITY;
    }

    private static double computeApproxCurveDerivative(Graph graph, double x, EvalContext ctx) {
        var dx = 0.01;
        var y1 = graph.fn.evalAt(x - dx/2.0, ctx);
        var y2 = graph.fn.evalAt(x + dx/2.0, ctx);
        return (y2 - y1) / dx;
    }
}