package rollingball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.paint.Color;
import rollingball.expressions.Expressions.Expr;
import rollingball.gamestate.GraphStorage;

public class GraphStorageTest {
    private static final Expr DUMMY_EXPR = ctx -> 0.0;
    private static final GraphStorage.Graph DUMMY_GRAPH = new GraphStorage.Graph(0, DUMMY_EXPR, Color.WHITE);

    GraphStorage graphs;
    
    @BeforeEach
    public void init() {
        graphs = new GraphStorage();
    }

    @Test
    public void testGraphsGetDifferentColors() {
        var used = new HashSet<>();
        // Empirical testing, best-effort. Not entirely provable.
        for (int i = 0; i < 100; ++i) {
            var graph = graphs.addGraph(DUMMY_EXPR);
            assertEquals(true, used.add(graph.color));
        }
    }

    @Test
    public void testColorsAreReused() {
        var graph = graphs.addGraph(DUMMY_EXPR);
        graphs.removeGraph(graph);

        var graph2 = graphs.addGraph(DUMMY_EXPR);

        assertEquals(graph.color, graph2.color);
    }

    @Test
    public void testGraphStorageDoesntExposeInternalList() {
        var graphList = graphs.getGraphs();
        assertThrows(RuntimeException.class, () -> graphList.add(DUMMY_GRAPH));
    }

    @Test
    public void testGetGraphsContainsAddedGraphs() {
        var graph = graphs.addGraph(DUMMY_EXPR);
        var graphList = graphs.getGraphs();
        assertEquals(true, graphList.contains(graph));
    }

    @Test
    public void testGraphEqualsDoesTypeChecking() {
        var graph = new GraphStorage.Graph(0, DUMMY_EXPR, Color.WHITE);
        assertEquals(false, graph.equals(new Object()));
    }

    @Test
    public void testGraphEqualsDoesNullChecking() {
        var graph = new GraphStorage.Graph(0, DUMMY_EXPR, Color.WHITE);
        assertEquals(false, graph.equals(null));
    }

    @Test
    public void testGraphEqualsDistinguishesGraphs() {
        var graph1 = new GraphStorage.Graph(0, DUMMY_EXPR, Color.WHITE);
        var graph2 = new GraphStorage.Graph(1, DUMMY_EXPR, Color.WHITE);
        assertNotEquals(graph1, graph2);
        assertEquals(graph1, graph1);
        assertEquals(graph2, graph2);
    }
}
