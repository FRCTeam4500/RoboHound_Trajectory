package robohound_trajectory;

public class RoboHound_Trajectory {
	
	public static Trajectory generateTrajectory(Waypoint[] waypoints, double timestep) {
		int splineLength = RobotMath.floorInt(1/timestep);
		int segmentLength = (waypoints.length-1)*splineLength;
		Trajectory.Segment[] segments = new Trajectory.Segment[segmentLength];
		for (int i = 1; i < waypoints.length; i++) {
			Spline spline = new Spline(waypoints[i-1], waypoints[i]);
			for (int j = 0; j < splineLength; j++) {
				double dt = timestep*(j+1);
				System.out.println("dt: " + dt);
				System.out.println(spline.getHeading(dt));
				segments[(i-1)*splineLength+j] = new Trajectory.Segment(dt, spline.getX(dt), spline.getY(dt), spline.getHeading(dt));
			}
			//segments[i-1] = new Trajectory.Segment(dt, spline.getX(dt), spline.getY(dt), waypoints[i-1].getDistance(waypoints[i]), velocity, acceleration, jerk, heading)
		}
		return new Trajectory(segments);
	}

}
