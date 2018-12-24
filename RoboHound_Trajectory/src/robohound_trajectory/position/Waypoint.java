package robohound_trajectory.position;

public class Waypoint {
	
	private Translation translation;
	private Rotation rotation;
	
	public Waypoint(double x, double y, double heading) {
		translation = new Translation(x, y);
		rotation = new Rotation(heading);
	}
	
	public Waypoint(Translation translation, Rotation rotation) {
		this.translation = translation;
		this.rotation = rotation;
	}
	
	public Translation getTranslation() {
		return translation;
	}
	
	public Rotation getRotation() {
		return rotation;
	}
	
	public double getDistance(Waypoint pos2) {
		double x0 = this.getTranslation().getX();
		double x1 = pos2.getTranslation().getX();
		double y0 = this.getTranslation().getY();
		double y1 = pos2.getTranslation().getY();
		return Math.sqrt(Math.pow((x1-x0), 2) + Math.pow((y1-y0), 2));
	}

}
