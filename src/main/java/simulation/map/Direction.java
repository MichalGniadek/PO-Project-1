package simulation.map;

import java.util.Random;

public enum Direction {
    NORTH,
    NORTH_WEST,
    WEST,
    SOUTH_WEST,
    SOUTH,
    SOUTH_EAST,
    EAST,
    NORTH_EAST;

    public String toString() {
        return switch (this) {
            case NORTH -> "Polnoc";
            case NORTH_WEST -> "Polnocnyzachod";
            case WEST -> "Zachod";
            case SOUTH_WEST -> "Poludniowyzachod";
            case SOUTH -> "Poludnie";
            case SOUTH_EAST -> "Poludniowywschod";
            case EAST -> "Wschod";
            case NORTH_EAST -> "Polnocnywschod";
        };
    }

    public static Direction GetRandom(){
        var rand = new Random();
        return Direction.values()[rand.nextInt(8)];
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTH_WEST -> new Vector2d(-1, 1);
            case WEST -> new Vector2d(-1, 0);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case EAST -> new Vector2d(1, 0);
            case NORTH_EAST -> new Vector2d(1, 1);
        };
    }

    public Direction rotateBy(int steps){
        return Direction.values()[(ordinal() + steps) % 8];
    }
}
