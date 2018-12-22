package application.other;

import javafx.scene.chart.XYChart;
import robohound_trajectory.Trajectory;

public class SeriesFactory {
	
	public static XYChart.Series<Double, Double> buildPositionSeries(Trajectory t) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();

        for (int i = 0; i < t.segments.length; i++) {
            XYChart.Data<Double, Double> data = new XYChart.Data<>();

            data.setXValue(t.get(i).x);
            data.setYValue(t.get(i).y);

            series.getData().add(data);
        }
        return series;
    }
}
