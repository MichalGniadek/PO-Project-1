package simulation;

public class EnergyChart extends ChartDisplay
        implements ITotalEnergyChanged, IAnimalSpawnedObserver, IAnimalDiedObserver{
    private int animals = 0;
    private int totalEnergy = 0;

    public EnergyChart(WorldMap map){
        super();
        map.addObserver(this);
    }

    @Override
    public void update() {
        super.update();
        if(animals != 0) addPoint("Average energy", totalEnergy / (double) animals);
    }

    @Override
    public void animalDied(Animal animal) {
        animals--;
        totalEnergy -= animal.getEnergy();
    }

    @Override
    public void animalSpawned(Animal parentA, Animal parentB, Animal child) {
        animals++;
    }

    @Override
    public void totalEnergyChanged(int delta) {
        totalEnergy += delta;
    }
}
