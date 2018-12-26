package robohound_trajectory.generating;

/**
 * Represents the entire motion the robot will perform<br>
 * Made up of segments that are spaced by the elapsed time<br>
 */
public class Trajectory {
	
	/**
	 * Contains the needed information for each point that the robot will follow
	 */
	public static class Segment {
        public double dt, x, y, position, velocity, acceleration, heading;

        public Segment(double dt, double x, double y, double position, double heading, double velocity, double acceleration) {
            this.dt = dt;
            this.x = x;
            this.y = y;
            this.position = position;
            this.heading = heading;
            this.velocity = velocity;
            this.acceleration = acceleration;
        }
    }

    public Segment[] segments;
    public MotionProfile profile;
    
    /**
     * @param segments the needed information for each point that the robot will follow
     * @param profile the profile used to find the desired velocity and accelerations (the information is already filled in the segments but is still passed to the constructor so it can be used by the GUI later)
     */
    public Trajectory(Segment[] segments, MotionProfile profile) {
        this.segments = segments;
        this.profile = profile;
    }

    public Trajectory(int length) {
        this.segments = new Segment[length];
    }

    public Segment get(int index) {
        return segments[index];
    }
  
    public int length() {
        return segments.length;
    }
}
