package robohound_trajectory;

public class RoboHound_Trajectory {
	
	public static Trajectory.Segment[] generateTrajectory(Waypoint[] waypoints) {
		Trajectory.Segment[] segments = new Trajectory.Segment[waypoints.length-1];
		double timestep = 0.05;
		for (int i = 1; i < waypoints.length; i++) {
			double dt = timestep*(i-1);
			Spline spline = new Spline(waypoints[i-1], waypoints[i]);
			//segments[i-1] = new Trajectory.Segment(dt, spline.getX(dt), spline.getY(dt), waypoints[i-1].getDistance(waypoints[i]), velocity, acceleration, jerk, heading)
		}
		return null;
	}

}
