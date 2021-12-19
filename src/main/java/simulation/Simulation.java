package simulation;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable{
    private SimulationStatus status = SimulationStatus.RunOnce;
    private final WorldMap map;
    private final VBox root;

    private final List<Display> displayList = new ArrayList<>();
    private final VBox displayBox = new VBox(new Label("display temp"));

    public Simulation(WorldMap map){
        this.map = map;

        var start_button = new Button("Start");
        start_button.setOnAction(ev -> status = SimulationStatus.Running);
        var pause_button = new Button("Pause");
        pause_button.setOnAction(ev -> status = SimulationStatus.Paused);
        var step_button = new Button("Step");
        step_button.setOnAction(ev -> status = SimulationStatus.RunOnce);

        var control_box = new HBox(start_button, pause_button, step_button);
        control_box.setSpacing(5);
        control_box.setAlignment(Pos.CENTER);

        var display_control_box = new HBox(
                newDisplay("Count", new CountChart(map)),
                newDisplay("Genomes", new GenomeDisplay(map)),
                newDisplay("Avg life", new LifeExpectancyChart(map)),
                newDisplay("Avg children", new AverageChildrenChart(map)),
                newDisplay("Avg energy", new EnergyChart(map))
        );
        display_control_box.setSpacing(10);
        display_control_box.setAlignment(Pos.CENTER);

        displayBox.getChildren().set(0, displayList.get(0).getNode());

        root = new VBox(map.getGrid(), control_box, display_control_box, displayBox);
        root.setSpacing(10);
    }

    private Button newDisplay(String label, Display display){
        displayList.add(display);
        var index = displayList.size() - 1;

        var button = new Button(label);
        button.setOnAction(ev -> displayBox.getChildren().set(0, displayList.get(index).getNode()));

        return button;
    }

    public Parent GetRoot(){
        return root;
    }

    @Override
    public void run() {
        while(status != SimulationStatus.Stop){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(status == SimulationStatus.Running || status == SimulationStatus.RunOnce) {
                Platform.runLater(() -> {
                    map.runTurn();
                    map.updateGrid();
                    for(var d : displayList) d.update();
                });
            }

            if (status == SimulationStatus.RunOnce){
                status = SimulationStatus.Paused;
            }
        }
    }

    public void stop(){
        status = SimulationStatus.Stop;
    }
}
