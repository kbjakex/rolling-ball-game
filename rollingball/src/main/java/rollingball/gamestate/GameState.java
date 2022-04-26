package rollingball.gamestate;

import java.util.List;

import rollingball.functions.EvalContext;
import rollingball.functions.Function;
import rollingball.gamestate.FunctionStorage.Graph;

public final class GameState {
    public static final class Ball {
        private double x;
        private double y;

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

    private Ball theBall;

    public GameState(Level level) {
        this.graphs = new FunctionStorage();
        this.startTimeMs = 0.0;
        this.isPlaying = false;
        this.level = level;
        this.theBall = new Ball(level);
    }

    public void togglePlaying() {
        this.isPlaying = !this.isPlaying;
        if (this.isPlaying) {
            this.startTimeMs = System.nanoTime() / 1_000_000.0;
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
            theBall.x = level.startX + timeSeconds * BALL_SPEED;

            updateBallY(timeSeconds);
        }
    }

    private void updateBallY(double time) {
        var ctx = new EvalContext(time);

        var nextY = theBall.y - computeGravity(time);
        for (var graph : getGraphs()) {
            ctx.x = theBall.x;
            var y = graph.fn.eval(ctx);
            if (Double.isNaN(y) || y - 0.005 > theBall.y || !graph.fn.canEval(ctx)) {
                continue;
            }

            var adjustedY = GoldenSectionSearch.computeBallYOnCurve(graph.fn, ctx, theBall.x);
            if (nextY < adjustedY) {
                nextY = adjustedY;
                theBall.lastCollisionTimestamp = time;
            }
        }
        theBall.y = nextY;
    }

    private double computeGravity(double time) {
        return Math.max(0.1, Math.min(0.5, time - theBall.lastCollisionTimestamp)) * GRAVITY;
    }
}