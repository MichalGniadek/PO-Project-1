package simulation.display;

import javafx.scene.Node;
import simulation.CSVData;

public interface Display {
    void update();
    Node getNode();
    CSVData getCSVData();
}
