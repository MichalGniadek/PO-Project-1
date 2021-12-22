package simulation;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class WorldMap {
    private static final String grassImagePath = "src/main/resources/high-grass.png";
    private static Image grassImageCache;
    private final Random rand = new Random();

    private final Vector2d size;
    private final boolean wraps;
    private final Vector2d jungleStart;
    private final int jungleSize;

    private final Map<Vector2d, List<Animal>> animals = new HashMap<>();
    private final int plantEnergy;

    private final int maxMagicEvolution;
    private int currentMagicEvolution = 0;
    private final Label magicEvolutionLabel = new Label("");

    private final Set<Vector2d> grass = new HashSet<>();

    private final GridPane grid = new GridPane();

    private final List<IAnimalDiedObserver> animalDiedObservers = new ArrayList<>();
    private final List<IAnimalSpawnedObserver> animalsReproduceObservers = new ArrayList<>();
    private final List<IGrassCountChanged> grassCountChangedObservers = new ArrayList<>();
    private final List<ITotalEnergyChanged> totalEnergyChangedObservers = new ArrayList<>();
    private final List<IAnimalSelected> animalSelectedObservers = new ArrayList<>();

    public WorldMap(Vector2d size, boolean wraps, int jungleRatio, int numberOfAnimals,
                    int startEnergy, int moveEnergy, int plantEnergy, boolean magicEvolution) {
        this.size = size;
        this.wraps = wraps;

        for (int i = 0; i < numberOfAnimals; i++) {

            Vector2d pos;
            do {
                pos = new Vector2d(rand.nextInt(size.x), rand.nextInt(size.y));
            } while (occupiedByAnimals(pos));

            var animal = new Animal(rand, startEnergy, moveEnergy);
            addAnimal(animal, pos);
            initNewAnimal(animal, null, null);
        }
        this.plantEnergy = plantEnergy;

        this.jungleSize = (int) Math.sqrt((double) (size.x * size.y * jungleRatio) / 100);
        this.jungleStart = new Vector2d(size.x / 2 - jungleSize / 2, size.y / 2 - jungleSize / 2);

        for (int i = 0; i < jungleSize * jungleSize / 2; i++) spawnGrass();

        if (magicEvolution) maxMagicEvolution = 3;
        else maxMagicEvolution = 0;
    }

    public void addObserver(Object observer) {
        if (observer instanceof IAnimalDiedObserver) {
            animalDiedObservers.add((IAnimalDiedObserver) observer);
        }
        if (observer instanceof IAnimalSpawnedObserver) {
            animalsReproduceObservers.add((IAnimalSpawnedObserver) observer);
            for (var animals : animals.values()) {
                for (var animal : animals) {
                    ((IAnimalSpawnedObserver) observer)
                            .animalSpawned(null, null, animal);
                }
            }
        }
        if (observer instanceof IGrassCountChanged) {
            grassCountChangedObservers.add((IGrassCountChanged) observer);
            ((IGrassCountChanged) observer).grassCountChanged(grass.size());
        }
        if (observer instanceof ITotalEnergyChanged) {
            totalEnergyChangedObservers.add((ITotalEnergyChanged) observer);
            for (var animals : animals.values()) {
                for (var animal : animals) {
                    animal.addEnergyObserver((ITotalEnergyChanged) observer);
                }
            }
        }
        if (observer instanceof IAnimalSelected) {
            animalSelectedObservers.add((IAnimalSelected) observer);
            for (var animals : animals.values()) {
                for (var animal : animals) {
                    animal.addSelectedObserver((IAnimalSelected) observer);
                }
            }
        }
    }

    public void runTurn() {
        removeDeadAnimals();
        moveAnimals();
        eatAndReproduceAnimals();
        handleMagicEvolution();

        for (var animals : animals.values()) animals.sort(Animal::compareEnergy);

        spawnGrass();
    }

    private void removeDeadAnimals() {
        for (var animals : animals.values()) {
            var iter = animals.iterator();
            while (iter.hasNext()) {
                var animal = iter.next();
                if (animal.isDead()) {
                    iter.remove();
                    for (var obs : animalDiedObservers) {
                        obs.animalDied(animal);
                    }
                }
            }
        }
    }

    private void moveAnimals() {
        var movedAnimals = new ArrayList<Animal>();
        var movedPositions = new ArrayList<Vector2d>();

        for (var entry : animals.entrySet()) {
            var position = entry.getKey();
            var iter = entry.getValue().iterator();
            while (iter.hasNext()) {
                var animal = iter.next();
                var move = animal.move();
                var newPosition = position.add(move);

                if (wraps) {
                    newPosition = new Vector2d(
                            (newPosition.x + size.x) % size.x,
                            (newPosition.y + size.y) % size.y
                    );
                } else {
                    newPosition = new Vector2d(
                            Math.max(0, Math.min(size.x - 1, newPosition.x)),
                            Math.max(0, Math.min(size.y - 1, newPosition.y))
                    );
                }

                if (!newPosition.equals(position)) {
                    iter.remove();
                    movedAnimals.add(animal);
                    movedPositions.add(newPosition);
                }
            }
        }

        for (int i = 0; i < movedAnimals.size(); i++)
            addAnimal(movedAnimals.get(i), movedPositions.get(i));
    }

    private void eatAndReproduceAnimals() {
        for (var entry : animals.entrySet()) {
            var position = entry.getKey();
            var animals = entry.getValue();

            if (animals.size() > 0 && grass.contains(position)) {
                var maxEnergy = animals.get(0).getEnergy();
                var topAnimals = animals.stream()
                        .filter(a -> a.getEnergy() == maxEnergy).toList();
                var energy = plantEnergy / topAnimals.size();
                for (var animal : topAnimals) animal.feed(energy);
                grass.remove(position);
                for (var obs : grassCountChangedObservers) obs.grassCountChanged(-1);
            }

            if (animals.size() > 1) {
                var iter = animals.iterator();
                var first = iter.next();
                var second = iter.next();
                if (first.canReproduce() && second.canReproduce()) {
                    var child = new Animal(rand, first, second);
                    addAnimal(child, position);
                    initNewAnimal(child, first, second);
                }
            }
        }
    }

    private void spawnGrass() {
        final int SPAWN_TRIES = 50;

        for (int i = 0; i < SPAWN_TRIES; i++) {
            var pos = new Vector2d(
                    jungleStart.x + rand.nextInt(jungleSize),
                    jungleStart.y + rand.nextInt(jungleSize)
            );
            if (grass.contains(pos) || occupiedByAnimals(pos)) continue;

            grass.add(pos);
            for (var obs : grassCountChangedObservers) obs.grassCountChanged(1);
            break;
        }

        for (int i = 0; i < SPAWN_TRIES; i++) {
            var pos = new Vector2d(
                    rand.nextInt(size.x),
                    rand.nextInt(size.y)
            );

            Vector2d jungleRect = jungleStart.add(
                    new Vector2d(jungleSize - 1, jungleSize - 1));

            if (grass.contains(pos) || occupiedByAnimals(pos) ||
                    (pos.follows(jungleStart) && pos.precedes(jungleRect))) {
                continue;
            }

            grass.add(pos);
            for (var obs : grassCountChangedObservers) obs.grassCountChanged(1);
            break;
        }
    }

    private void handleMagicEvolution() {
        if (currentMagicEvolution < maxMagicEvolution) {
            List<Animal> allAnimals = new ArrayList<>();
            for (var entry : animals.entrySet()) {
                allAnimals.addAll(entry.getValue());
            }

            if (allAnimals.size() <= 5) {
                currentMagicEvolution++;

                for (var animal : allAnimals) {

                    Vector2d pos;
                    do {
                        pos = new Vector2d(rand.nextInt(size.x), rand.nextInt(size.y));
                    } while (occupiedByAnimals(pos));

                    var clone = new Animal(animal);
                    addAnimal(clone, pos);
                    initNewAnimal(clone, null, null);
                }
            }
        }
    }

    public void updateNode() {
        magicEvolutionLabel.setText(
                "Magic evolution: " + currentMagicEvolution + " out of " + maxMagicEvolution);

        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        grid.getColumnConstraints().add(new ColumnConstraints(20));
        grid.getRowConstraints().add(new RowConstraints(20));
        addGridLabel("x/y", 0, 0);

        for (int i = 0; i < size.x; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(20));
            addGridLabel(i, i + 1, 0);
        }

        for (int i = 0; i < size.y; i++) {
            grid.getRowConstraints().add(new RowConstraints(20));
            addGridLabel(i, 0, i + 1);
        }

        for (var grass : grass) {
            grid.add(getGrassImageView(), grass.x + 1, grass.y + 1);
        }

        for (var entry : animals.entrySet()) {
            var pos = entry.getKey();
            var animals = entry.getValue().stream().map(Animal::getImageView).toList();

            if (!animals.isEmpty()) {
                var imageSize = 20 / animals.size();

                for (var image : animals) {
                    image.setFitWidth(imageSize);
                    image.setFitHeight(imageSize);
                }

                var field = new HBox();
                field.getChildren().setAll(animals);

                grid.add(field, pos.x + 1, pos.y + 1);
            }
        }
    }

    private void addAnimal(Animal animal, Vector2d position) {
        if (!animals.containsKey(position)) animals.put(position, new ArrayList<>());
        animals.get(position).add(animal);
    }

    private void initNewAnimal(Animal animal, Animal parentA, Animal parentB) {
        for (var obs : totalEnergyChangedObservers) animal.addEnergyObserver(obs);
        for (var obs : animalSelectedObservers) animal.addSelectedObserver(obs);
        for (var obs : animalsReproduceObservers)
            obs.animalSpawned(parentA, parentB, animal);
    }

    private boolean occupiedByAnimals(Vector2d position) {
        if (!animals.containsKey(position)) return false;
        return !animals.get(position).isEmpty();
    }

    private ImageView getGrassImageView() {
        if (grassImageCache == null) {
            try {
                grassImageCache = new Image(new FileInputStream(grassImagePath));
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't found file " + grassImagePath);
            }
        }

        var imageView = new ImageView(grassImageCache);
        imageView.setEffect(new ColorAdjust(0.7, 1.0, -0.5, 0.0));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        return imageView;
    }

    private void addGridLabel(Object o, int x, int y) {
        var label = new Label(o.toString());
        GridPane.setHalignment(label, HPos.CENTER);
        grid.add(label, x, y);
    }

    public Node getNode() {
        if (maxMagicEvolution != 0) {
            var box = new VBox(grid, magicEvolutionLabel);
            box.setAlignment(Pos.CENTER);
            return box;
        }
        return grid;
    }
}
