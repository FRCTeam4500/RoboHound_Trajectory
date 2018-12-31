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
import robohound_trajectory.other.CSVWriter;
import robohound_trajectory.other.RobotMath;
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
		Spline[] splines = new Spline[waypoints.length-1];
		for (int pt = 1; pt < waypoints.length; pt++) {
			splines[pt-1] = new Spline(waypoints[pt-1], waypoints[pt]);
		}
		double arcLength = Spline.getPathLength(splines);
		
		MotionProfile profile = new MotionProfile(arcLength, timestep, vMax, aMax);	
		
		// The number of segments to split each spline into
		int waypointLength = (int) Math.ceil(profile.getFinalTime() / timestep);
		// The total number of segments
		int segmentCount = (waypoints.length-1) * waypointLength;
		
		Trajectory.Segment[] segments = new Trajectory.Segment[segmentCount];
		
		for (int pt = 1; pt < waypoints.length; pt++) { // pt is the index of the spline you are currently analyzing
			for (int seg = 0; seg < waypointLength; seg++) { // seg is index of the segment for the spline you are currently analyzing
				double t = timestep*seg;
				double adjT = RobotMath.timeTransformation(t, profile.getFinalTime());
				segments[(pt-1)*waypointLength+seg] = new Trajectory.Segment(timestep, splines[pt-1].getX(adjT), splines[pt-1].getY(adjT), splines[pt-1].getArcLength(0, adjT), splines[pt-1].getHeading(adjT), profile.getVelocity(t), profile.getAcceleration(t));
			}
		}
		return new Trajectory(segments, profile);
	}
	
	/**
	 * Exports a trajectory to the given path
	 * @param traj trajectory to export
	 * @param path location of the csv file to save to
	 * @throws IOException
	 */
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
	/**
	 * Converts a csv file back into a trajectory
	 * @param path location of the csv file to read from (this should be stored on the RoboRIO)
	 * @return trajectory containing the segments defined in the CSV file
	 * @throws IOException
	 */
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
			// Converts the ArrayList to an array
	        Trajectory.Segment[] segArray = new Trajectory.Segment[segments.size()]	;	
	        segArray = segments.toArray(segArray);
	        return new Trajectory(segArray, null);
		}
	}

}
