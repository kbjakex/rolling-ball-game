package rollingball.gamestate;

import java.util.List;

import rollingball.expressions.Expressions.Expr;
import rollingball.gamestate.GraphStorage.Graph;

public final class GameState {
    private final GraphStorage graphs;
    
    public GameState() {
        this.graphs = new GraphStorage();
    }

    public void update() {

    }

    public Graph addGraph(Expr fn) {
        return graphs.addGraph(fn);
    }

    public void removeGraph(Graph graph) {
        graphs.removeGraph(graph);
    }

    public List<Graph> getGraphs() {
        return graphs.getGraphs();
    }

}

/*             
    var bx = (Math.sin(frameCount*0.01)*300) * GRAPH_AREA_WIDTH / GRAPH_AREA_WIDTH_PX;
    var bx2 = GoldenSectionSearch.computeBallYOnCurve(graph.fn, evalCtx, bx);
    var y = -bx2.y() * -PX_PER_GRAPH_AREA_UNIT;
    var r = GoldenSectionSearch.BALL_RADIUS * PX_PER_GRAPH_AREA_UNIT;
    graphics.setFill(graph.color);
    graphics.fillOval(bx * PX_PER_GRAPH_AREA_UNIT - r, y - 2.0 * r, r * 2, r * 2);
    graphics.setFill(Color.ORANGE); 
*/
