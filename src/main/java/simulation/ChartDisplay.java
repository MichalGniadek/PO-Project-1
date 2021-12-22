package simulation;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.*;

public abstract class ChartDisplay implements Display {
    private final LineChart<Number, Number> lineChart;
    private final Map<String, XYChart.Series<Number, Number>> series = new HashMap<>();
    protected int day = 0;

    public ChartDisplay(){
        lineChart = new LineChart<>(createAxis("Day"), createAxis("Count"));
        lineChart.setCreateSymbols(false);
    }

    protected void addPoint(String seriesName, double value){
        if(!series.containsKey(seriesName)){
            var s = new XYChart.Series<Number, Number>();
            s.setName(seriesName);
            series.put(seriesName, s);
            Platform.runLater(() -> lineChart.getData().add(s));
        }

        series.get(seriesName).getData().add(new XYChart.Data<>(day, value));
    }

    protected void resetLineChart(){
        lineChart.getData().clear();
        series.clear();
    }

    private NumberAxis createAxis(String label){
        var axis = new NumberAxis();
        axis.setAnimated(false);
        axis.setTickUnit(1);
        axis.setLabel(label);
        return axis;
    }

    @Override
    public void update() {
        day++;
    }

    @Override
    public Node getNode() {
        return lineChart;
    }

    @Override
    public CSVData getCSVData() {
        var csv = new CSVData();
        for(var s : series.entrySet()){
            List<Double> data = new ArrayList<>();

            // Pad the data if it doesn't start day 1
            int firstDay = s.getValue().getData().get(0).getXValue().intValue();
            for(int i = 1; i < firstDay; i++){
                data.add(0.0);
            }

            data.addAll(s.getValue().getData().stream()
                    .map(XYChart.Data::getYValue).map(Number::doubleValue).toList());

            csv.add(s.getKey(), data);
        }
        return csv;
    }
}
