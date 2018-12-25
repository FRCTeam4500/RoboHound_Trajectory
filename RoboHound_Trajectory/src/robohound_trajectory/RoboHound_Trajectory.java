package robohound_trajectory;

import robohound_trajectory.position.Waypoint;

public class RoboHound_Trajectory {
	
	public static Trajectory generateTrajectory(Waypoint[] waypoints, double timestep, double vMax, double aMax) {
		double dist = waypoints[0].getDistance(waypoints[waypoints.length-1]);
		
		TrapezoidalProfile profile = new TrapezoidalProfile(dist, timestep, vMax, aMax);		
		
		int waypointLength = (int) Math.ceil(profile.getTFinal() / timestep);
		int segmentCount = (waypoints.length-1) * waypointLength;
		
		System.out.println("-------------------------------");
		Trajectory.Segment[] segments = new Trajectory.Segment[segmentCount];
		System.out.println("Total Size: " + (segmentCount));
		System.out.println("Waypoint Size: " + (waypointLength));
		
		for (int pt = 1; pt < waypoints.length; pt++) {
			Spline spline = new Spline(waypoints[pt-1], waypoints[pt]);
			
			for (int seg = 0; seg < waypointLength; seg++) {
				double t = timestep*seg;
				double adjT = RobotMath.timeTransformation(t, profile.getTFinal());
				segments[(pt-1)*waypointLength+seg] = new Trajectory.Segment(timestep, spline.getX(adjT), spline.getY(adjT), spline.getHeading(adjT), profile.getVelocity(t), profile.getAcceleration(t));
				System.out.println((pt-1)*waypointLength+seg);
			}
		}
		
//		int splineLength = RobotMath.floorInt(1/timestep);
//		int segmentLength = (waypoints.length-1)*splineLength;
//		Trajectory.Segment[] segments = new Trajectory.Segment[segmentLength];
//		for (int i = 1; i < waypoints.length; i++) {
//			Spline spline = new Spline(waypoints[i-1], waypoints[i]);
//			for (int j = 0; j < splineLength; j++) {
//				double dt = timestep*(j+1);
//				//System.out.println("dt: " + dt);
//				//System.out.println(spline.getHeading(dt));
//				segments[(i-1)*splineLength+j] = new Trajectory.Segment(dt, spline.getX(dt), spline.getY(dt), spline.getHeading(dt));
//			}
//			//segments[i-1] = new Trajectory.Segment(dt, spline.getX(dt), spline.getY(dt), waypoints[i-1].getDistance(waypoints[i]), velocity, acceleration, jerk, heading)
//		}
		return new Trajectory(segments, profile);
	}

}
