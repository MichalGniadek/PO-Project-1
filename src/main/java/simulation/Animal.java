package simulation;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal {
    private static final String imagePath = "src/main/resources/hyena-head.png";
    static Image imageCache;
    private final Random rand;

    private Direction direction;
    private final int moveEnergy;
    private final int startEnergy;
    private int energy;
    private int daysLived = 0;
    private int childrenCount = 0;
    private final Genome genome;

    private boolean isSelected = false;
    private boolean highlightNextDraw = false;

    List<ITotalEnergyChanged> totalEnergyChangedObservers = new ArrayList<>();
    List<IAnimalSelected> animalSelectedObservers = new ArrayList<>();

    public Animal(Random rand, int startEnergy, int moveEnergy, Genome genome){
        this.rand = rand;
        this.direction = Direction.GetRandom();
        this.moveEnergy = moveEnergy;
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.genome = genome;
    }

    public Animal(Random rand, int startEnergy, int moveEnergy){
        this(rand, startEnergy, moveEnergy, new Genome(rand));
    }

    public Animal(Animal clone){
        this(clone.rand, clone.startEnergy, clone.moveEnergy, clone.genome);
    }

    public Animal(Random rand, Animal stronger, Animal weaker){
        stronger.childrenCount++;
        weaker.childrenCount++;

        this.rand = rand;
        this.direction = Direction.GetRandom();
        this.moveEnergy = stronger.moveEnergy;
        this.startEnergy = stronger.startEnergy;

        this.energy = (stronger.energy + weaker.energy) / 4;
        stronger.energy -= stronger.energy / 4;
        weaker.energy -= weaker.energy / 4;

        var splitIndex = 32.0 * weaker.energy / (weaker.energy + stronger.energy);
        this.genome = new Genome(weaker.genome, stronger.genome, (int)splitIndex, rand);
    }

    public void addEnergyObserver(ITotalEnergyChanged observer){
        totalEnergyChangedObservers.add(observer);
        observer.totalEnergyChanged(energy);
    }

    public void addSelectedObserver(IAnimalSelected observer){
        animalSelectedObservers.add(observer);
    }

    public void setSelection(boolean selected){
        isSelected = selected;
    }

    public void highlightNextDraw(){
        highlightNextDraw = true;
    }

    public boolean isDead(){
        return energy <= 0;
    }

    public boolean canReproduce(){
        return energy >= (startEnergy / 2);
    }

    public void feed(int energy){
        this.energy += energy;
        for(var obs : totalEnergyChangedObservers) obs.totalEnergyChanged(energy);
    }

    public Vector2d move(){
        daysLived++;
        energy -= moveEnergy;
        for(var obs : totalEnergyChangedObservers) obs.totalEnergyChanged(-moveEnergy);

        var move = genome.getRandom(rand);
        if (move == 0){
            return direction.toUnitVector();
        }else if (move == 4){
            return direction.toUnitVector().opposite();
        }else{
            direction = direction.rotateBy(move);
            return new Vector2d(0,0);
        }
    }

    public Genome getGenome(){
        return genome;
    }

    public int getDaysLived(){
        return daysLived;
    }

    public int getEnergy() {
        return energy;
    }

    public int getChildrenCount(){
        return childrenCount;
    }

    public ImageView getImageView(){
        if (imageCache == null){
            try {
                imageCache = new Image(new FileInputStream(imagePath));
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't find file " + imagePath);
            }
        }

        var imageView = new ImageView(imageCache);

        switch (direction){
            case NORTH -> imageView.setRotate(0);
            case NORTH_EAST -> imageView.setRotate(45);
            case EAST -> imageView.setRotate(90);
            case SOUTH_EAST -> imageView.setRotate(135);
            case SOUTH -> imageView.setRotate(180);
            case SOUTH_WEST -> imageView.setRotate(225);
            case WEST -> imageView.setRotate(270);
            case NORTH_WEST -> imageView.setRotate(315);
        }

        var hue = highlightNextDraw ? -0.6 : isSelected ? 1 : 0.1;
        var saturation = Math.min(energy, startEnergy * 1.5) / (startEnergy * 1.5) * 0.8 + 0.2;
        imageView.setEffect(new ColorAdjust(hue, saturation, -0.2,0.0));

        highlightNextDraw = false;

        imageView.setPickOnBounds(true);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            for(var observer : animalSelectedObservers) observer.animalSelected(this);
            event.consume();
        });

        return imageView;
    }

    public static int compareEnergy(Animal a, Animal b){
        return Integer.compare(a.energy, b.energy);
    }
}
