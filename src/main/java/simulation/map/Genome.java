package simulation.map;

import java.util.Arrays;
import java.util.Random;

public class Genome {
    private final int[] genes;

    public Genome(Random rand){
        genes = new int[32];
        Arrays.setAll(genes, i -> rand.nextInt(8));
        Arrays.sort(genes);
    }

    public Genome(Genome g0, Genome g1, int splitIndex, Random rand){
        genes = new int[32];
        var side = rand.nextBoolean();

        for(int i = 0; i < 32; i++){
            if(side)
                genes[i] = (i < splitIndex) ? g0.genes[i] : g1.genes[i];
            else
                genes[31 - i] = (i < splitIndex) ? g0.genes[31 - i] : g1.genes[31 - i];
        }
        Arrays.sort(genes);
    }

    public int getRandom(Random rand){
        return genes[rand.nextInt(32)];
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(var g : genes) s.append(g);
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genome genome = (Genome) o;
        return Arrays.equals(genes, genome.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }
}
