package robohound_trajectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import robohound_trajectory.generating.MotionProfile;
import robohound_trajectory.generating.Spline;
import robohound_trajectory.generating.Trajectory;
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
		System.out.println(arcLength);
		
		MotionProfile profile = new MotionProfile(arcLength, timestep, vMax, aMax);	
		
		int waypointLength = (int) Math.ceil(profile.getFinalTime() / timestep);
		int segmentCount = (waypoints.length-1) * waypointLength;
		
		Trajectory.Segment[] segments = new Trajectory.Segment[segmentCount];
		
		for (int pt = 1; pt < waypoints.length; pt++) {
			for (int seg = 0; seg < waypointLength; seg++) {
				double t = timestep*seg;
				double adjT = RobotMath.timeTransformation(t, profile.getFinalTime());
				segments[(pt-1)*waypointLength+seg] = new Trajectory.Segment(timestep, splines[pt-1].getX(adjT), splines[pt-1].getY(adjT), splines[pt-1].getArcLength(0, adjT), splines[pt-1].getHeading(adjT), profile.getVelocity(t), profile.getAcceleration(t));
			}
		}
		return new Trajectory(segments, profile);
	}
	
	public static void exportToCSV(Trajectory traj, String path) throws IOException {
		CSVWriter writer = new CSVWriter(path);
		
		writer.writeLine(new String[] {"dt", "x", "y", "position", "heading", "velocity", "acceleration"});
		for (int i = 0; i < traj.length(); i++) {
			Trajectory.Segment seg = traj.get(i);
			String[] data = new String[] {RobotMath.truncatedToString(seg.dt), RobotMath.truncatedToString(seg.x), RobotMath.truncatedToString(seg.y), RobotMath.truncatedToString(seg.position), RobotMath.truncatedToString(RobotMath.rad_to_deg(seg.heading)), RobotMath.truncatedToString(seg.velocity), RobotMath.truncatedToString(seg.acceleration)};
			writer.writeLine(data);
		}
		writer.done();
	}
	
	public static void exportToCSV(Waypoint[] waypoints, double timestep, double vMax, double aMax, String path) throws IOException {
		Trajectory traj = generateTrajectory(waypoints, timestep, vMax, aMax);
		exportToCSV(traj, path);
	}
	
	private static boolean firstLine;
	public static Trajectory readFromCSV(String path) throws IOException {
		firstLine = true;
		ArrayList<Trajectory.Segment> segments = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			stream.forEach((line) -> {
	        	if (!firstLine) {
	        		String[] parameters = line.split(",");
	        		double[] nums = Arrays.stream(parameters)
	                        .mapToDouble(Double::parseDouble)
	                        .toArray();
	        		segments.add(new Trajectory.Segment(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5], nums[6]));
	        		
	        	}
	        	firstLine = false;
	        });
	        Trajectory.Segment[] segArray = new Trajectory.Segment[segments.size()]	;	
	        segArray = segments.toArray(segArray);
	        return new Trajectory(segArray, null);
		}
	}

}
