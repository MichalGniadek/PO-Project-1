package simulation;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.*;

public class GenomeDisplay implements Display, IAnimalSpawnedObserver, IAnimalDiedObserver{
    private final WorldMap map;
    private final Set<Animal> animals = new HashSet<>();
    private final VBox box = new VBox();

    public GenomeDisplay(WorldMap map) {
        super();
        this.map = map;
        map.addObserver(this);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
    }

    @Override
    public void update() {}

    private void recalculateTopGenomes(){
        var genomes = new HashMap<Genome, Integer>();

        for(var animal : animals){
            var genome = animal.getGenome();
            genomes.put(genome, genomes.getOrDefault(genome, 0) + 1);
        }

        var topGenomes = genomes.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> -e.getValue())).limit(5).toList();
        var topGenome = topGenomes.isEmpty() ? null : topGenomes.get(0).getKey();

        Platform.runLater(() -> {
            box.getChildren().clear();

            var button = new Button("Highlight animals with the top genome");
            button.setOnAction(ev -> {
                for(var animal : animals) {
                    System.out.println("" + animal.getGenome() + " " + topGenome + (animal.getGenome() == topGenome));
                    if (animal.getGenome().equals(topGenome)) animal.highlightNextDraw();
                }
                map.updateNode();
            });
            box.getChildren().add(button);

            for (var genome : topGenomes){
                box.getChildren().add(new Label(
                        genome.getKey().toString() + " (" + genome.getValue().toString() + ")"));
            }

        });
    }

    @Override
    public Node getNode() {
        return box;
    }

    @Override
    public CSVData getCSVData() {
        return new CSVData();
    }

    @Override
    public void animalDied(Animal animal) {
        animals.remove(animal);
        recalculateTopGenomes();
    }

    @Override
    public void animalSpawned(Animal parentA, Animal parentB, Animal child) {
        animals.add(child);
        recalculateTopGenomes();
    }
}
