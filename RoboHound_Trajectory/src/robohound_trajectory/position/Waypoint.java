package robohound_trajectory.position;

/**
 * Defines the position and heading at a given location. These are used to represent points that the robot explicitly needs to reach
 */
public class Waypoint {
	
	private Translation translation;
	private Rotation rotation;
	
	/**
	 * Defines a waypoint using an x, y, and heading double
	 * @param x
	 * @param y
	 * @param heading angle the wheels should be facing at this point
	 */
	public Waypoint(double x, double y, double heading) {
		translation = new Translation(x, y);
		rotation = new Rotation(heading);
	}
	
	/**
	 * Defines a waypoint with a predefined Translation and Rotation
	 * @param translation
	 * @param rotation
	 */
	public Waypoint(Translation translation, Rotation rotation) {
		this.translation = translation;
		this.rotation = rotation;
	}
	
	/**
	 * @return a translation class containing the x and y position
	 */
	public Translation getTranslation() {
		return translation;
	}
	
	
	/**
	 * @return a rotation class containing the heading of the robot
	 */
	public Rotation getRotation() {
		return rotation;
	}
	
	/**
	 * Obtains the distance between this waypoint and another. Note that the end point is the waypoint that is passed into the function
	 * @param pos2 the waypoint containing the end point
	 * @return distance between two points
	 */
	public double getDistance(Waypoint pos2) {
		double x0 = this.getTranslation().getX();
		double x1 = pos2.getTranslation().getX();
		double y0 = this.getTranslation().getY();
		double y1 = pos2.getTranslation().getY();
		return Math.sqrt(Math.pow((x1-x0), 2) + Math.pow((y1-y0), 2));
	}

}
