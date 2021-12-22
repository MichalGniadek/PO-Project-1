package simulation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CSVData {
    Map<String, List<Double>> data;

    public CSVData(){
        this.data = new HashMap<>();
    }

    public void add(String name, List<Double> data){
        this.data.put(name, data);
    }

    public void union(CSVData other){
        if(other == null) return;
        data.putAll(other.data);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        var out = new StringBuilder();

        var entries = data.entrySet().stream().toList();

        for(var e : entries){
            out.append(e.getKey());
            out.append(", ");
        }
        out.delete(out.length()-2, out.length());
        out.append('\n');

        var days = entries.iterator().next().getValue().size();
        for (int i = 0; i < days; i++){
            for(var e : entries){
                out.append(decimalFormat.format(e.getValue().get(i)));
                out.append(", ");
            }
            out.delete(out.length()-2, out.length());
            out.append('\n');
        }

        // Average
        for(var e : entries){
            double sum = 0;
            for(var v : e.getValue()) sum += v;
            out.append(decimalFormat.format(sum / e.getValue().size()));
            out.append(", ");
        }
        out.delete(out.length()-2, out.length());
        out.append('\n');

        return out.toString();
    }
}
