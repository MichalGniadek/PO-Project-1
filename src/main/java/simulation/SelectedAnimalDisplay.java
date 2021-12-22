package simulation;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

public class SelectedAnimalDisplay extends ChartDisplay implements IAnimalSelected, IAnimalDiedObserver, IAnimalSpawnedObserver{
    private final WorldMap map;
    private Animal selected;
    private final Label infoLabel = new Label("");
    private Set<Animal> children;
    private Set<Animal> descendants;

    public SelectedAnimalDisplay(WorldMap map){
        super();
        this.map = map;
        map.addObserver(this);
    }

    @Override
    public void update() {
        super.update();
        if(children != null && descendants != null) {
            addPoint("Children", children.size());
            addPoint("Descendants", children.size() + descendants.size());
        }
    }

    @Override
    public Node getNode() {
        var box = new VBox(infoLabel, super.getNode());
        box.setAlignment(Pos.CENTER);
        return box;
    }

    @Override
    public void animalDied(Animal animal) {
        if(selected == animal) {
            selected = null;
            infoLabel.setText(infoLabel.getText() + ", Died in: " + day);
        }
        if(children != null) children.remove(animal);
        if(descendants != null) descendants.remove(animal);
    }

    @Override
    public void animalSelected(Animal animal) {
        if(selected != null) selected.setSelection(false);

        selected = animal;
        selected.setSelection(true);
        infoLabel.setText("Genome: " + animal.getGenome());

        children = new HashSet<>();
        descendants = new HashSet<>();

        resetLineChart();
        map.updateNode();
    }

    @Override
    public void animalSpawned(Animal parentA, Animal parentB, Animal child) {
        if(selected == null) return;

        if(parentA == selected || parentB == selected) children.add(child);
        else if(children.contains(parentA) || descendants.contains(parentB)) descendants.add(child);
    }
}
