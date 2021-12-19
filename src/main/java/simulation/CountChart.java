package simulation;

public class CountChart extends DisplayChart implements
        IAnimalSpawnedObserver, IAnimalDiedObserver, IGrassCountChanged {
    private int animalCount = 0;
    private int grassCount = 0;

    public CountChart(WorldMap map) {
        super();
        map.addObserver(this);
    }

    @Override
    public void update() {
        super.update();
        addPoint("Animals", animalCount);
        addPoint("Grass", grassCount);
    }

    @Override
    public void animalDied(Animal animal) {
        animalCount--;
    }

    @Override
    public void animalSpawned(Animal parentA, Animal parentB, Animal child) {
        animalCount++;
    }

    @Override
    public void grassCountChanged(int delta) {
        grassCount += delta;
    }
}
