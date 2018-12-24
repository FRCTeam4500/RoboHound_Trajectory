package application.other;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import robohound_trajectory.position.Waypoint;

public class Helper {
	
	public static Waypoint waypointDialog() {
		Dialog<Waypoint> dialog = new Dialog<>();
		dialog.setTitle("Add Waypoint");
		dialog.setHeaderText("Add a new Waypoint");	
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField x = new TextField();
		x.setPromptText("X");
		TextField y = new TextField();
		y.setPromptText("Y");
		TextField angle = new TextField();
		angle.setPromptText("Angle");
	
		grid.add(new Label("X:"), 0, 0);
		grid.add(x, 1, 0);
		grid.add(new Label("Y:"), 0, 1);
		grid.add(y, 1, 1);
		grid.add(new Label("Angle:"), 0, 2);
		grid.add(angle, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == ButtonType.OK) {
		        return new Waypoint(textToDouble(x), textToDouble(y), textToDouble(angle));
		    }
		    return null;
		});
		
		Optional<Waypoint> result = dialog.showAndWait();

		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}
	
	public static double textToDouble(TextField field) throws NumberFormatException {
		return Double.parseDouble(field.getText());
	}
}
