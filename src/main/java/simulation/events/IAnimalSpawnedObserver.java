package simulation.events;

import simulation.map.Animal;

public interface IAnimalSpawnedObserver {
    void animalSpawned(Animal parentA, Animal parentB, Animal child);
}
