package robohound_trajectory;

import java.io.IOException;

import application.other.CSVWriter;
import robohound_trajectory.position.Waypoint;

/**
 * The core class of RoboHound_Trajectory
 */
public class RoboHound_Trajectory {
	
	/**
	 * Generates the trajectory that the robot will follow, based on the given information
	 * @param waypoints an array of the waypoints that the robot will follow
	 * @param timestep the elapsed time between segment on the trajectory
	 * @param vMax the maximum velocity that the robot can reach
	 * @param aMax the maximum acceleration that the robot can reach
	 * @return a trajectory containing the segments and motion profile
	 */
	public static Trajectory generateTrajectory(Waypoint[] waypoints, double timestep, double vMax, double aMax) {
//		double dist = waypoints[0].getDistance(waypoints[waypoints.length-1]);
//		
//		MotionProfile profile = new MotionProfile(dist, timestep, vMax, aMax);		
//		
//		int waypointLength = (int) Math.ceil(profile.getFinalTime() / timestep);
//		int segmentCount = (waypoints.length-1) * waypointLength;
//		
//		Trajectory.Segment[] segments = new Trajectory.Segment[segmentCount];
//		
//		for (int pt = 1; pt < waypoints.length; pt++) {
//			Spline spline = new Spline(waypoints[pt-1], waypoints[pt]);
//			
//			for (int seg = 0; seg < waypointLength; seg++) {
//				double t = timestep*seg;
//				double adjT = RobotMath.timeTransformation(t, profile.getFinalTime());
//				segments[(pt-1)*waypointLength+seg] = new Trajectory.Segment(timestep, spline.getX(adjT), spline.getY(adjT), spline.getHeading(adjT), profile.getVelocity(t), profile.getAcceleration(t));
//				System.out.println((pt-1)*waypointLength+seg);
//			}
//		}
//		return new Trajectory(segments, profile);
		
		Spline[] splines = new Spline[waypoints.length-1];
		for (int pt = 1; pt < waypoints.length; pt++) {
			splines[pt-1] = new Spline(waypoints[pt-1], waypoints[pt]);
		}
		double arcLength = Spline.getPathLength(splines);
		
		MotionProfile profile = new MotionProfile(arcLength, timestep, vMax, aMax);	
		
		int waypointLength = (int) Math.ceil(profile.getFinalTime() / timestep);
		int segmentCount = (waypoints.length-1) * waypointLength;
		
		Trajectory.Segment[] segments = new Trajectory.Segment[segmentCount];
		
		for (int pt = 1; pt < waypoints.length; pt++) {
			for (int seg = 0; seg < waypointLength; seg++) {
				double t = timestep*seg;
				double adjT = RobotMath.timeTransformation(t, profile.getFinalTime());
				segments[(pt-1)*waypointLength+seg] = new Trajectory.Segment(timestep, splines[pt-1].getX(adjT), splines[pt-1].getY(adjT), splines[pt-1].getHeading(adjT), profile.getVelocity(t), profile.getAcceleration(t));
			}
		}
		return new Trajectory(segments, profile);
	}
	
	public static void exportToCSV(Trajectory traj, String path) throws IOException {
		CSVWriter writer = new CSVWriter(path);
		
		writer.writeLine(new String[] {"dt", "x", "y", "heading", "velocity", "acceleration"});
		for (int i = 0; i < traj.length(); i++) {
			Trajectory.Segment seg = traj.get(i);
			String[] data = new String[] {String.valueOf(seg.dt), String.valueOf(seg.x), String.valueOf(seg.y), String.valueOf(RobotMath.rad_to_deg(seg.heading)), String.valueOf(seg.velocity), String.valueOf(seg.acceleration)};
			writer.writeLine(data);
		}
		writer.done();
	}
	
	public static void exportToCSV(Waypoint[] waypoints, double timestep, double vMax, double aMax, String path) throws IOException {
		Trajectory traj = generateTrajectory(waypoints, timestep, vMax, aMax);
		exportToCSV(traj, path);
	}

}
