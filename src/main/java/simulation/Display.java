package simulation;

import javafx.scene.Node;

public interface Display {
    void update();
    Node getNode();
    CSVData getCSVData();
}
