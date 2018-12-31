package robohound_trajectory.followers;

import robohound_trajectory.generating.Trajectory;

public class EncoderFollower {
	
	private double kp, ki, kd, kv, ka;
	
	private double encoderOffset, encoderTickCount;
	private double wheelCircumference;
	
	double lastErr, heading;
	
	private Trajectory traj;
	private int segmentIndex;
	
	public EncoderFollower(Trajectory traj) {
		this.traj = traj;
	}
	
	/**
     * Configure the PID/VA Variables for the Follower
     * @param kp The proportional term. This is usually quite high (0.8 - 1.0 are common values)
     * @param ki The integral term. Currently unused.
     * @param kd The derivative term. Adjust this if you are unhappy with the tracking of the follower. 0.0 is the default
     * @param kv The velocity ratio. This should be 1 over your maximum velocity @ 100% throttle.
     *           This converts m/s given by the algorithm to a scale of -1..1 to be used by your
     *           motor controllers
     * @param ka The acceleration term. Adjust this if you want to reach higher or lower speeds faster. 0.0 is the default
     */
	public void configurePIDVA(double kp, double ki, double kd, double kv, double ka) {
		this.kp = kp; this.ki = ki; this.kd = kd;
		this.kv = kv; this.ka = ka;
	}
	
	/**
     * Configure the Encoders being used in the follower.
     * @param initialPos      The initial 'offset' of your encoder. This should be set to the encoder value just
     *                              before you start to track
     * @param ticksPerRevolution  How many ticks per revolution the encoder has
     * @param wheelDiameter        The diameter of your wheels (or pulleys for track systems) in meters
     */
	public void configureEncoder(int initialPos, int ticksPerRev, double wheelDiameter) {
		encoderOffset = initialPos;
        encoderTickCount = ticksPerRev;
        wheelCircumference = Math.PI * wheelDiameter;
	}
	
	/**
     * Reset the follower to start again. Encoders must be reconfigured.
     */
    public void reset() {
        lastErr = 0; segmentIndex = 0;
    }
	
    /**
     * Calculate the desired output for the motors, based on the amount of ticks the encoder has gone through.
     * This does not account for heading of the robot. To account for heading, add some extra terms in your control
     * loop for realignment based on gyroscope input and the desired heading given by this object.
     * @param encoderTick The amount of ticks the encoder has currently measured.
     * @return             The desired output for your motor controller
     */
	public double calculate(int encoderTick) {
		double distanceCovered = ((encoderTick - encoderOffset) / encoderTickCount) * wheelCircumference;
		
		if (segmentIndex < traj.length()) {
			Trajectory.Segment seg = traj.get(segmentIndex);
			double err = seg.position - distanceCovered;
			double calculatedValue = 
					(kv*seg.velocity + ka*seg.acceleration) +
					kp * err +
					kd * ((err - lastErr) / seg.dt);
			lastErr = err;
			heading = seg.heading;
			segmentIndex++;
			
			return calculatedValue;
		}
		return 0;
	}
	
	/**
	 * 
	 * @return heading the robot should be facing with no adjustments made
	 */
	public double getHeading() {
        return heading;
    }
	
	/**
	 * 
	 * @param gyro the reading of the gyro in DEGREES
	 * @return heading the robot should be facing with adjustments made
	 */
	public double getHeading(double gyro) {
        return heading-gyro;
    }
	
	/**
	 * 
	 * @return segment currently being analyzed
	 */
	public Trajectory.Segment getSegment() {
		return traj.get(segmentIndex);
	}
	 
	/**
	 * Returns true if the follower is done following the path
	 * @return if the follower has finished following the path
	 */
	public boolean isFinished() {
		return segmentIndex >= traj.length();
	}

}
