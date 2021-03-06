package simulation.display;

import simulation.map.Animal;
import simulation.map.WorldMap;
import simulation.events.IAnimalDiedObserver;
import simulation.events.IAnimalSpawnedObserver;

public class AverageChildrenChart extends ChartDisplay implements IAnimalDiedObserver, IAnimalSpawnedObserver {
    private int aliveAnimals = 0;
    private int totalChildren = 0;

    public AverageChildrenChart(WorldMap map) {
        super();
        map.addObserver(this);
    }

    @Override
    public void update() {
        super.update();
        if(aliveAnimals != 0) addPoint("Children count", totalChildren / (double)aliveAnimals);
    }

    @Override
    public void animalDied(Animal animal) {
        aliveAnimals--;
        totalChildren -= animal.getChildrenCount();
    }

    @Override
    public void animalSpawned(Animal parentA, Animal parentB, Animal child) {
        aliveAnimals++;
        if(parentA != null && parentB != null) totalChildren += 2;
    }
}
