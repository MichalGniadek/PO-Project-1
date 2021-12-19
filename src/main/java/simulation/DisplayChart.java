package simulation;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.Map;

public abstract class DisplayChart extends Display {
    private LineChart<Number, Number> lineChart;
    private Map<String, XYChart.Series<Number, Number>> series = new HashMap<>();
    private int day = 0;

    public DisplayChart(){
        lineChart = new LineChart<>(getAxis("Day"), getAxis("Count"));
        lineChart.setCreateSymbols(false);
    }

    private NumberAxis getAxis(String label){
        var axis = new NumberAxis();
        axis.setAnimated(false);
        axis.setTickUnit(1);
        axis.setLabel(label);
        return axis;
    }

    protected void addPoint(String seriesName, double value){
        if(!series.containsKey(seriesName)){
            var s = new XYChart.Series<Number, Number>();
            s.setName(seriesName);
            series.put(seriesName, s);
            Platform.runLater(() -> lineChart.getData().add(s));
        }

        var data = series.get(seriesName).getData();
        data.add(new XYChart.Data<>(day, value));
    }

    @Override
    public void update() {
        day++;
    }

    @Override
    public Node getNode() {
        return lineChart;
    }
}
