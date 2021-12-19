package simulation;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.*;

public class GenomeDisplay extends Display implements IAnimalSpawnedObserver, IAnimalDiedObserver{
    private Map<Genome, Integer> genomes = new HashMap<>();
    private VBox box;

    public GenomeDisplay(WorldMap map) {
        super();
        map.addObserver(this);
        box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
    }

    @Override
    public void update() {
        var topGenomes = genomes.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey)
                .map(Object::toString).map(Label::new).limit(5).toList();

        box.getChildren().clear();
        box.getChildren().setAll(topGenomes);
    }

    @Override
    public Node getNode() {
        return box;
    }

    @Override
    public void animalDied(Animal animal) {
        var genome = animal.getGenome();
        genomes.put(genome, genomes.get(genome) - 1);
    }

    @Override
    public void animalSpawned(Animal parentA, Animal parentB, Animal child) {
        var genome = child.getGenome();
        genomes.put(genome, genomes.getOrDefault(genome, 0) + 1);
    }
}
