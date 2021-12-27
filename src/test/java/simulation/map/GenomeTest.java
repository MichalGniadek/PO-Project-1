package simulation.map;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {
    @Test
    public void testGenomes(){
        var rand = new Random(123);
        var g0 = new Genome(rand);
        assertEquals("00000011111222223444444555667777", g0.toString());
        var g1 = new Genome(rand);
        assertEquals("00000111112233333333445556666777", g1.toString());

        var g2 = new Genome(g0, g1, 5, rand);
        assertEquals("00000111112233333333445556667777", g2.toString());

        var g3 = new Genome(g0, g1, 15, rand);
        assertEquals("00000011111222233333445556666777", g3.toString());
    }
}