package application.other;

import javafx.beans.property.SimpleDoubleProperty;
import robohound_trajectory.position.Waypoint;
import javafx.beans.property.DoubleProperty;

public class TableWaypoint {
	
	private final SimpleDoubleProperty x, y, angle;
	
	public TableWaypoint(Waypoint pos) {
		x = new SimpleDoubleProperty(pos.getTranslation().getX());
		y = new SimpleDoubleProperty(pos.getTranslation().getY());
		angle = new SimpleDoubleProperty(pos.getRotation().getHeading());
	}
	
	public Double getX() { return x.getValue(); }
    public DoubleProperty xProperty() { return x; }
    public void setX(double x) { this.x.set(x);}
    
    public Double getY() { return y.getValue(); }
    public DoubleProperty yProperty() { return y; }
    public void setY(double y) { this.y.set(y);}
    
    public Double getAngle() { return angle.getValue(); }
    public DoubleProperty angleProperty() { return angle; }
    public void setAngle(double angle) { this.angle.set(angle); }
    
    

}
