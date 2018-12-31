package application.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import application.other.Helper;
import application.other.SeriesFactory;
import application.other.TableWaypoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import robohound_trajectory.RoboHound_Trajectory;
import robohound_trajectory.generating.Trajectory;
import robohound_trajectory.other.RobotMath;
import robohound_trajectory.position.Waypoint;

public class MainController {
	
	@FXML TableView<TableWaypoint> waypointTable;
	@FXML TableColumn<TableWaypoint, Double> waypointX, waypointY, waypointAngle;
	@FXML Button btnAdd, btnClear, btnDelete;
	@FXML LineChart<Double, Double> chartXY, chartVT;
	@FXML NumberAxis chartXY_X, chartXY_Y, chartVT_V, chartVT_T;
	@FXML TextField inputTimestep, inputMaxV, inputMaxA, inputWidth, inputDepth;
	
	ObservableList<TableWaypoint> waypointList;
	ArrayList<Waypoint> waypoints = new ArrayList<>();
	
	 @FXML
	 public void initialize() {
		waypointList = FXCollections.observableList(new ArrayList<TableWaypoint>());
		
		waypointX.setCellValueFactory(new PropertyValueFactory<>("x"));
		waypointX.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointX.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setX(t.getNewValue());
	                waypoints.get(t.getTablePosition().getRow()).getTranslation().setX(t.getNewValue());
	                updateXYChart();
	                updateVTChart();
	            });
		
		waypointY.setCellValueFactory(new PropertyValueFactory<>("y"));
		waypointY.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointY.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setY(t.getNewValue());
	                t.getTablePosition().getRow();
	                waypoints.get(t.getTablePosition().getRow()).getTranslation().setY(t.getNewValue());
	                updateXYChart();
	                updateVTChart();
	            });
		
		waypointAngle.setCellValueFactory(new PropertyValueFactory<>("angle"));
		waypointAngle.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointAngle.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAngle(t.getNewValue());
	                waypoints.get(t.getTablePosition().getRow()).getRotation().setHeading(t.getNewValue());
	                updateXYChart();
	                updateVTChart();
	            });
		
		waypointTable.setItems(waypointList);
		
		Node background = chartXY.lookup(".chart-plot-background");	
		background.setOnMousePressed((event) -> {
			double x = RobotMath.roundApprox(chartXY_X.getValueForDisplay(event.getX()).doubleValue(), .5);
			double y = RobotMath.roundApprox(chartXY_Y.getValueForDisplay(event.getY()).doubleValue(), .5);;
			
			double angle = 0;
	         
	         if (!waypointList.isEmpty()) {
	             TableWaypoint prev = waypointList.get(waypointList.size() - 1);
	             angle = RobotMath.rad_to_deg(Math.atan2(y - prev.getY(), x - prev.getX()));
	             angle = RobotMath.roundApprox(angle, 45.0);
	         }
	         
	         if (x >= chartXY_X.getLowerBound() && x <= chartXY_X.getUpperBound() &&
	                 y >= chartXY_Y.getLowerBound() && y <= chartXY_Y.getUpperBound()) {
	             waypointList.add(new TableWaypoint(new Waypoint(x, y, angle)));
	             waypoints.add(new Waypoint(x, y, angle));
	             updateXYChart();
	             updateVTChart();
	         }
		});
	}
	
	public void addClicked() {
		Waypoint pos = Helper.waypointDialog();
		
		if (pos != null) {
			waypointList.add(new TableWaypoint(pos));
			waypoints.add(pos);
			updateXYChart();
			updateVTChart();
		}
	}
	
	public void deleteClicked() {
		int selected = waypointTable.getSelectionModel().getSelectedIndex();
		
		if (selected >= 0) {
			waypointList.remove(selected);
			waypoints.remove(selected);
			updateXYChart();
			updateVTChart();	
		}
	}
	
	public void clearClicked() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Clear Points");
        alert.setHeaderText("Clear All Points?");
        alert.setContentText("Are you sure you want to clear all points?");

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent((ButtonType t) -> {
            if (t.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                waypointList.clear();
                waypoints.clear();
                updateXYChart();
                updateVTChart();
            }
        });
	}
	
	public void inputChanged() {
		updateXYChart();
		updateVTChart();
	}

	public void exportTrajectory() {
		 Trajectory traj = createTrajectory();
		 
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Export");
		 fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("Comma Separated Values", "*.csv"));
		 
         File result = fileChooser.showSaveDialog(waypointTable.getScene().getWindow());
		 
		 try {
			RoboHound_Trajectory.exportToCSV(traj, result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateXYChart() {
		 ObservableList<XYChart.Series<Double, Double>> posData = chartXY.getData();
		 posData.clear();

		 double timestep = Double.parseDouble(inputTimestep.getText());
		 double maxV = Double.parseDouble(inputMaxV.getText());
		 double maxA = Double.parseDouble(inputMaxA.getText());
		 
		 if (!waypointList.isEmpty()) {
			 if (waypointList.size() > 1) {
				 Waypoint[] test = new Waypoint[waypoints.size()];
				 test = waypoints.toArray(test);
				 Trajectory traj = RoboHound_Trajectory.generateTrajectory(test, timestep, maxV, maxA);
				 XYChart.Series<Double, Double> sourceSeries = SeriesFactory.buildPositionSeries(traj);
				 posData.add(sourceSeries);
				 
				 int waypointLength = (int) Math.ceil(traj.profile.getFinalTime() / timestep);
				 int i = 0;
				 for (XYChart.Data<Double, Double> data : sourceSeries.getData()) {
					 if (!((i+1) % waypointLength == 0 || i == 0)) {
						 data.getNode().setVisible(false);
					 }
					 i++;
				 }
			 }
		 }

	}
	
	public void updateVTChart() {
		chartVT.getData().clear();
		
		double timestep = Double.parseDouble(inputTimestep.getText());
		double maxV = Double.parseDouble(inputMaxV.getText());
		double maxA = Double.parseDouble(inputMaxA.getText());
		
		if (waypointList.size() > 1) {
			Waypoint[] test = new Waypoint[waypoints.size()];
			test = waypoints.toArray(test);
			Trajectory traj = RoboHound_Trajectory.generateTrajectory(test, timestep, maxV, maxA);
			
			chartVT_T.setUpperBound(traj.profile.getFinalTime() + .05*traj.profile.getFinalTime());
			chartVT_V.setUpperBound(maxV + .05*maxV);
			
			XYChart.Series<Double, Double> sourceSeries = SeriesFactory.buildVelocitySeries(traj);
			chartVT.getData().add(sourceSeries);
		}
	}
	
	public Trajectory createTrajectory() {
		double timestep = Double.parseDouble(inputTimestep.getText());
		double maxV = Double.parseDouble(inputMaxV.getText());
		double maxA = Double.parseDouble(inputMaxA.getText());
		 
		Waypoint[] test = new Waypoint[waypoints.size()];
		test = waypoints.toArray(test);
		 
		return RoboHound_Trajectory.generateTrajectory(test, timestep, maxV, maxA);
	}
	
}
