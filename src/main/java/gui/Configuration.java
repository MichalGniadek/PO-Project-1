package gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulation.*;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private final List<ISimulationStarted> simulationStartedObservers = new ArrayList<>();
    private final VBox root;

    private final Spinner<Integer> width = new Spinner<>(5, 100, 17);
    private final Spinner<Integer> height = new Spinner<>(5, 100, 17);
    private final CheckBox wraps = new CheckBox("Wraps?");
    private final Spinner<Integer> jungleRatio = new Spinner<>(0, 100, 20);
    private final Spinner<Integer> plantEnergy = new Spinner<>(0, 100, 10);

    private final Spinner<Integer> startAnimals = new Spinner<>(1, 100, 10);
    private final Spinner<Integer> startEnergy = new Spinner<>(1, 100000, 50);
    private final Spinner<Integer> moveEnergy = new Spinner<>(1, 100000, 1);

    public Configuration(){
        var start_button = new Button("Start simulation");
        start_button.setOnAction(ev -> {
            for (var observer: simulationStartedObservers)
                observer.SimulationStarted(this);
        });

        root = new VBox(
                new Label("Select configuration"),
                DisplayBox(new Label("Size"), new Label("X:"), width, new Label("Y:"), height),
                wraps,
                DisplayBox(new Label("Jungle ratio:"), jungleRatio),
                DisplayBox(new Label("Plant energy:"), plantEnergy),
                DisplayBox(new Label("Starting animals:"), startAnimals),
                DisplayBox(new Label("Starting energy:"), startEnergy),
                DisplayBox(new Label("Move energy:"), moveEnergy),
                start_button
        );
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
    }

    private HBox DisplayBox(Node... children){
        var box = new HBox(children);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        return box;
    }

    public void AddSimulationStartedObserver(ISimulationStarted observer){
        simulationStartedObservers.add(observer);
    }

    public Parent GetRoot(){
        return root;
    }

    public Simulation InitSimulation(){
        var map = new WorldMap(new Vector2d(width.getValue(), height.getValue()),
                wraps.isSelected(), jungleRatio.getValue(),
                startAnimals.getValue(), startEnergy.getValue(), moveEnergy.getValue(),
                plantEnergy.getValue());
        return new Simulation(map);
    }
}
