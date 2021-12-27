package simulation.events;

import simulation.map.Animal;

public interface IAnimalDiedObserver {
    void animalDied(Animal animal);
}
