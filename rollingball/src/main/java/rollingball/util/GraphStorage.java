package rollingball.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import rollingball.expressions.Expressions.Expr;

public final class GraphStorage {
    public static final class Graph {
        public final Expr fn;
        public final Paint color;

        private final int id;

        public Graph(int id, Expr fn, Paint color) {
            this.id = id;
            this.fn = fn;
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Graph graph) {
                return this.id == graph.id;
            }
            return false;
        }
    }

    private final List<Graph> graphs;

    private final List<Integer> recycledIds;
    private int graphId = 0;

    public GraphStorage() {
        this.graphs = new ArrayList<>();
        this.recycledIds = new ArrayList<>();
    }

    public Graph addGraph(Expr expr) {
        var id = computeNewGraphId();
        var color = computeColorFor(id);
        var graph = new Graph(id, expr, color);
        graphs.add(graph);
        return graph;
    }

    private int computeNewGraphId() {
        if (!recycledIds.isEmpty()) {
            return recycledIds.remove(recycledIds.size()-1);
        }

        return graphId++;
    }

    private Paint computeColorFor(int id) {
        if (id < GRAPH_COLOR_RBG_TABLE.length) {
            var rgb = GRAPH_COLOR_RBG_TABLE[id];
            return Color.rgb(rgb & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 16) & 0xFF);
        }

        // Generate a random color and hope it's not too close to any other colors!
        return Color.hsb(Math.random() * 360.0, 1.0, 0.85);
    }

    public void removeGraph(Graph graph) {
        if (graphs.remove(graph)) {
            recycledIds.add(graph.id);
        }
    }

    public List<Graph> getGraphs() {
        return Collections.unmodifiableList(graphs);
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
