package application.other;

import javafx.scene.chart.XYChart;
import robohound_trajectory.generating.Trajectory;

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
	
	public static XYChart.Series<Double, Double> buildVelocitySeries(Trajectory t) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();

//        for (int i = 0; i < t.segments.length; i++) {
//            XYChart.Data<Double, Double> data = new XYChart.Data<>();
//
//            data.setXValue(t.get(i).dt * i);
//            data.setYValue(t.get(i).velocity);
//
//            series.getData().add(data);
//        }
        
        int tFinal = (int) Math.ceil(t.profile.getFinalTime());
        for (double	i = 0; i < tFinal; i += 0.01) {
            XYChart.Data<Double, Double> data = new XYChart.Data<>();

            data.setXValue(i);
            data.setYValue(t.profile.getVelocity(i));

            series.getData().add(data);
        }
        return series;
    }
}
