package rollingball.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;
import rollingball.functions.Function;

/**
 * A container for the functions entered by the user.
 * FunctionStorage is not permanent, and is created
 * separately for each level.
 */
public final class FunctionStorage {
    /**
     * Represents a single function. Each function has a different color and id.
     */
    public static final class Graph {
        private Function fn;
        private final Color color;

        private final int id;

        /**
         * Creates a new graph with the given function and color.
         * @param id an identifier for the graph
         * @param fn the function
         * @param color the color of the graph
         */
        public Graph(int id, Function fn, Color color) {
            this.id = id;
            this.fn = fn;
            this.color = color;
        }

        public Function geFunction() {
            return fn;
        }

        public Color getColor() {
            return color;
        }

        /**
         * Replaces the function in the graph without affecting the id or color.
         * Intended for permitting the editability of already-entered functions.
         * @param fn the new function
         */
        public void setFunction(Function fn) {
            this.fn = fn;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Graph graph && graph.id == this.id;
        }
    }

    private final List<Graph> graphs;

    private final List<Integer> recycledIds;
    private int graphId = 0;

    /**
     * Creates an empty FunctionStorage.
     */
    public FunctionStorage() {
        this.graphs = new ArrayList<>();
        this.recycledIds = new ArrayList<>();
    }

    /**
     * Adds a new graph to the storage.
     * @param expr the function
     * @return the graph
     */
    public Graph addGraph(Function expr) {
        var id = computeNewGraphId();
        var color = computeColorFor(id);
        var graph = new Graph(id, expr, color);
        graphs.add(graph);
        return graph;
    }

    /**
     * Removes the given graph from the storage.
     * @param graph the graph to remove
     */
    public void removeGraph(Graph graph) {
        if (graphs.remove(graph)) {
            recycledIds.add(graph.id);
        }
    }

    /**
     * Returns a list of graphs stored in this storage.
     * @return an immutable view of the graphs
     */
    public List<Graph> getGraphs() {
        return Collections.unmodifiableList(graphs);
    }

    private int computeNewGraphId() {
        if (!recycledIds.isEmpty()) {
            return recycledIds.remove(recycledIds.size() - 1);
        }

        return graphId++;
    }

    private Color computeColorFor(int id) {
        if (id < GRAPH_COLOR_RBG_TABLE.length) {
            var rgb = GRAPH_COLOR_RBG_TABLE[id];
            return Color.rgb(rgb & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 16) & 0xFF);
        }

        // Generate a random color and hope it's not too close to any other colors!
        return Color.hsb(Math.random() * 360.0, 1.0, 0.85);
    }

    // From https://stackoverflow.com/a/20298027, credit to Tatarize
    private static final int[] GRAPH_COLOR_RBG_TABLE = {
        0x000000, 0x00FF00, 0x0000FF, 0xFF0000, 0x01FFFE, 0xFFA6FE, 0xFFDB66, 0x006401, 0x010067, 0x95003A,
        0x007DB5, 0xFF00F6, 0xFFEEE8, 0x774D00, 0x90FB92, 0x0076FF, 0xD5FF00, 0xFF937E, 0x6A826C, 0xFF029D,
        0xFE8900, 0x7A4782, 0x7E2DD2, 0x85A900, 0xFF0056, 0xA42400, 0x00AE7E, 0x683D3B, 0xBDC6FF, 0x263400,
        0xBDD393, 0x00B917, 0x9E008E, 0x001544, 0xC28C9F, 0xFF74A3, 0x01D0FF, 0x004754, 0xE56FFE, 0x788231,
        0x0E4CA1, 0x91D0CB, 0xBE9970, 0x968AE8, 0xBB8800, 0x43002C, 0xDEFF74, 0x00FFC6, 0xFFE502, 0x620E00,
        0x008F9C, 0x98FF52, 0x7544B1, 0xB500FF, 0x00FF78, 0xFF6E41, 0x005F39, 0x6B6882, 0x5FAD4E, 0xA75740,
        0xA5FFD2, 0xFFB167, 0x009BFF, 0xE85EBE
    };

}
