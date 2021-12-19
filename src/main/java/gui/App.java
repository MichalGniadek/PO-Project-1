package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.ISimulationStarted;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

public class App extends Application implements ISimulationStarted {
    private Scene mainScene;
    private final List<Simulation> simulations = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        var configuration = new Configuration();
        configuration.AddSimulationStartedObserver(this);
        mainScene = new Scene(configuration.GetRoot(), 400, 650);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Evolution Sandbox");
        primaryStage.show();
    }

    @Override
    public void SimulationStarted(Configuration configuration) {
        Platform.runLater(() -> {
            var simulation = configuration.InitSimulation();
            simulations.add(simulation);
            var simulationThread = new Thread(simulation);
            simulationThread.start();
            mainScene.rootProperty().set(simulation.GetRoot());
        });
    }

    @Override
    public void stop() {
        for(var simulation : simulations) simulation.stop();
    }
}
