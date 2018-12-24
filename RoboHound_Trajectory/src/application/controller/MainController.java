package application.controller;

import java.util.ArrayList;
import java.util.Optional;

import application.other.Helper;
import application.other.SeriesFactory;
import application.other.TableWaypoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
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
import javafx.util.converter.DoubleStringConverter;
import robohound_trajectory.RoboHound_Trajectory;
import robohound_trajectory.RobotMath;
import robohound_trajectory.position.Waypoint;

public class MainController {
	
	@FXML TableView<TableWaypoint> waypointTable;
	@FXML TableColumn<TableWaypoint, Double> waypointX, waypointY, waypointAngle;
	@FXML Button btnAdd, btnClear, btnDelete;
	@FXML LineChart<Double, Double> chartXY;
	@FXML NumberAxis chartXY_X, chartXY_Y;
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
	            });
		
		waypointY.setCellValueFactory(new PropertyValueFactory<>("y"));
		waypointY.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointY.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setY(t.getNewValue());
	                t.getTablePosition().getRow();
	                waypoints.get(t.getTablePosition().getRow()).getTranslation().setY(t.getNewValue());
	                updateXYChart();
	            });
		
		waypointAngle.setCellValueFactory(new PropertyValueFactory<>("angle"));
		waypointAngle.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointAngle.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAngle(t.getNewValue());
	                waypoints.get(t.getTablePosition().getRow()).getRotation().setHeading(t.getNewValue());
	                updateXYChart();
	            });
		
		waypointTable.setItems(waypointList);
	}
	
	public void addClicked() {
		Waypoint pos = Helper.waypointDialog();
		
		if (pos != null) {
			waypointList.add(new TableWaypoint(pos));
			waypoints.add(pos);
			updateXYChart();
		}
	}
	
	public void deleteClicked() {
		int selected = waypointTable.getSelectionModel().getSelectedIndex();
		
		waypointList.remove(selected);
		waypoints.remove(selected);
		updateXYChart();
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
            }
        });
	}
	
	public void addPointOnClick(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
			double xLocal = chartXY_X.sceneToLocal(mouseSceneCoords).getX();
			double yLocal = chartXY_Y.sceneToLocal(mouseSceneCoords).getY();
			
			 double x = RobotMath.round(chartXY_X.getValueForDisplay(xLocal).doubleValue(), 2);
             double y = RobotMath.round(chartXY_Y.getValueForDisplay(yLocal).doubleValue(), 2);
             double angle = 0;
             
             if (!waypointList.isEmpty()) {
                 TableWaypoint prev = waypointList.get(waypointList.size() - 1);
                 angle = RobotMath.rad_to_deg(Math.atan2(y - prev.getY(), x - prev.getX()));
                 //angle = RobotMath.deg_to_rad(RobotMath.round(angle, 45.0));
                 angle = RobotMath.round(angle, 45.0);
             }
             
             if (x >= chartXY_X.getLowerBound() && x <= chartXY_X.getUpperBound() &&
                     y >= chartXY_Y.getLowerBound() && y <= chartXY_Y.getUpperBound()) {
                 waypointList.add(new TableWaypoint(new Waypoint(x, y, angle)));
                 waypoints.add(new Waypoint(x, y, angle));
                 updateXYChart();
             }
//             if (!currentTrajValid) {
//                 waypointList.remove(waypointList.size() - 1);
//             }
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
				 XYChart.Series<Double, Double> sourceSeries = SeriesFactory.buildPositionSeries(RoboHound_Trajectory.generateTrajectory(test, timestep, maxV, maxA));
				 posData.add(sourceSeries);
				 
				 int i = 0;
				 for (XYChart.Data<Double, Double> data : sourceSeries.getData()) {
					 if (!(i % 20 == 0)) {
						 data.getNode().setVisible(false);
					 }
					 if (i == 0) {
						 i+=2;
					 } else {
						 i++;
					 }
				 }
				 //flSeries.getNode().setStyle("-fx-stroke: blue");
                 //frSeries = SeriesFactory.buildPositionSeries(backend.getFrontRightTrajectory());
			 }
		 }

	}
	
}
