package application.controller;

import java.util.ArrayList;

import application.other.Helper;
import application.other.SeriesFactory;
import application.other.TableWaypoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import robohound_trajectory.Waypoint;

public class MainController {
	
	@FXML TableView<TableWaypoint> waypointTable;
	@FXML TableColumn<TableWaypoint, Double> waypointX, waypointY, waypointAngle;
	@FXML Button btnAdd, btnClear, btnDelete;
	@FXML LineChart<Double, Double> chartXY;
	@FXML NumberAxis chartXY_X, chartXY_Y;
	
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
	            });
		
		waypointY.setCellValueFactory(new PropertyValueFactory<>("y"));
		waypointY.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointY.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setY(t.getNewValue());
	                t.getTablePosition().getRow();
	                waypoints.get(t.getTablePosition().getRow()).getTranslation().setY(t.getNewValue());
	            });
		
		waypointAngle.setCellValueFactory(new PropertyValueFactory<>("angle"));
		waypointAngle.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		waypointAngle.setOnEditCommit(
	            (CellEditEvent<TableWaypoint, Double> t) -> {
	                ((TableWaypoint) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAngle(t.getNewValue());
	                waypoints.get(t.getTablePosition().getRow()).getRotation().setHeading(t.getNewValue());
	            });
		
		waypointTable.setItems(waypointList);
	}
	
	public void addClicked() {
		Waypoint pos = Helper.waypointDialog();
		
		if (pos != null) {
			waypointList.add(new TableWaypoint(pos));
			waypoints.add(pos);
		}
	}
	
	public void updateXYChart() {
		 ObservableList<XYChart.Series<Double, Double>> posData = chartXY.getData();
		 posData.clear();
		 
		 if (!waypointList.isEmpty()) {
			 if (waypointList.size() > 1) {
				 //XYChart.Series<Double, Double>
                 //flSeries = SeriesFactory.buildPositionSeries(backend.getFrontLeftTrajectory()),
                 //frSeries = SeriesFactory.buildPositionSeries(backend.getFrontRightTrajectory());
			 }
		 }

	}
	
}
