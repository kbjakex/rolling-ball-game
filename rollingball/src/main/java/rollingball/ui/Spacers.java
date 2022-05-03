package rollingball.ui;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Helper methods for centering/spacing elements
 */
public final class Spacers {
    private Spacers() { // make non-instantiable
    }

    /**
     * Creates a node that consumes as much horizontal space in a HBox as possible.
     * @return the node
     */
    public static Node createHSpacer() {
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Creates a node that consumes as much vertical space in a VBox as possible.
     * @return the node
     */
    public static Node createVSpacer() {
        var spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
