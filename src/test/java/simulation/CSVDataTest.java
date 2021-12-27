package simulation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CSVDataTest {
    @Test
    public void testCSVData(){
        var data = new CSVData();
        var a = new ArrayList<Double>();
        a.add(1.0);
        a.add(2.0);
        a.add(3.0);
        data.add("A", a);
        var b = new ArrayList<Double>();
        b.add(10.0);
        b.add(20.0);
        b.add(30.0);
        data.add("B", b);
        assertEquals("""
                A, B
                1, 10
                2, 20
                3, 30
                2, 20
                """, data.toString());
    }
}