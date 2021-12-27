package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import simulation.events.ISimulationStarted;
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
        mainScene = new Scene(configuration.GetRoot(), 800, 650);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Evolution Sandbox");
        primaryStage.show();
    }

    @Override
    public void SimulationStarted(Configuration configuration) {
        Platform.runLater(() -> {
            var simulations = configuration.InitSimulations();
            this.simulations.addAll(simulations);

            for(var simulation : simulations){
                var simulationThread = new Thread(simulation);
                simulationThread.start();
            }

            var box = new HBox();

            for(var simulation : simulations){
                box.getChildren().add(simulation.GetRoot());
            }

            mainScene.rootProperty().set(box);
        });
    }

    @Override
    public void stop() {
        for(var simulation : simulations) simulation.stop();
    }
}
