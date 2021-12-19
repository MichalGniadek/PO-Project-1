package simulation;

public class LifeExpectancyChart extends DisplayChart implements IAnimalDiedObserver{
    private int deadAnimals = 0;
    private int totalLife = 0;

    public LifeExpectancyChart(WorldMap map) {
        super();
        map.addObserver(this);
    }

    @Override
    public void update() {
        super.update();
        if(deadAnimals != 0) addPoint("Life expectancy", totalLife / (double)deadAnimals);
    }

    @Override
    public void animalDied(Animal animal) {
        deadAnimals++;
        totalLife += animal.getDaysLived();
    }
}
