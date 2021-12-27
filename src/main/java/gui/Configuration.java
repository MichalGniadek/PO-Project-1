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
import simulation.events.ISimulationStarted;
import simulation.map.Vector2d;
import simulation.map.WorldMap;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private final List<ISimulationStarted> simulationStartedObservers = new ArrayList<>();
    private final VBox root;

    private final Spinner<Integer> width = new Spinner<>(5, 100, 17);
    private final Spinner<Integer> height = new Spinner<>(5, 100, 17);
    private final Spinner<Integer> jungleRatio = new Spinner<>(0, 100, 30);
    private final Spinner<Integer> plantEnergy = new Spinner<>(0, 100, 15);

    private final Spinner<Integer> startAnimals = new Spinner<>(1, 100, 10);
    private final Spinner<Integer> startEnergy = new Spinner<>(1, 100000, 70);
    private final Spinner<Integer> moveEnergy = new Spinner<>(1, 100000, 1);

    private final CheckBox leftWraps = new CheckBox("Left wraps?");
    private final CheckBox rightWraps = new CheckBox("Right wraps?");
    private final CheckBox rightMap = new CheckBox("Create right map?");

    private final CheckBox leftMagic = new CheckBox("Left magic evolution?");
    private final CheckBox rightMagic = new CheckBox("Right magic evolution?");
    private final CheckBox leftMap = new CheckBox("Create left map?");

    public Configuration(){
        var start_button = new Button("Start simulations");
        start_button.setOnAction(ev -> {
            for (var observer: simulationStartedObservers)
                observer.SimulationStarted(this);
        });

        leftWraps.setSelected(true);
        leftMap.setSelected(true);

        root = new VBox(
                new Label("Select configuration"),
                DisplayBox(new Label("Size"), new Label("X:"), width, new Label("Y:"), height),
                DisplayBox(new Label("Jungle ratio:"), jungleRatio),
                DisplayBox(new Label("Plant energy:"), plantEnergy),
                DisplayBox(new Label("Starting animals:"), startAnimals),
                DisplayBox(new Label("Starting energy:"), startEnergy),
                DisplayBox(new Label("Move energy:"), moveEnergy),
                leftWraps,
                rightWraps,
                leftMagic,
                rightMagic,
                leftMap,
                rightMap,
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

    public List<Simulation> InitSimulations(){
        List<Simulation> simulations = new ArrayList<>();
        if(leftMap.isSelected()){
            simulations.add(new Simulation(new WorldMap(
                    new Vector2d(width.getValue(), height.getValue()),
                    leftWraps.isSelected(), jungleRatio.getValue(),
                    startAnimals.getValue(), startEnergy.getValue(), moveEnergy.getValue(),
                    plantEnergy.getValue(), leftMagic.isSelected()
            )));
        }
        if(rightMap.isSelected()){
            simulations.add(new Simulation(new WorldMap(
                    new Vector2d(width.getValue(), height.getValue()),
                    rightWraps.isSelected(), jungleRatio.getValue(),
                    startAnimals.getValue(), startEnergy.getValue(), moveEnergy.getValue(),
                    plantEnergy.getValue(), rightMagic.isSelected()
            )));
        }
        return simulations;
    }
}
