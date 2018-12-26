package robohound_trajectory;

/**
 * Generates the trapezoidal (or triangular) motion profile and allows you to find the velocity and acceleration at a given time.
 * If the robot can reach can reach it's maximum velocity within the given time, a trapezoidal profile is used. If there isn't enough time,
 * a triangular profile is used instead and vMax will change accordingly
 */
public class MotionProfile {
	
	private double t0 = 0, t1, t2, t3, d1, d2, d3;
	private double distance, vMax, aMax;
	private ProfileType type;
	
	public enum ProfileType {
		Trapezoidal, Triangular
	};
	
	//TODO: TIME IS SLIGHTLY LONGER DUE TO ROUNDOFF ERROR, BOUNDARY PAST T3...?
	/**
	 * Determines if a triangular or trapezoidal profile should be used and finds the desired information
	 * @param distance the distance from the initial and final waypoint 
	 * @param timestep the time between each point on the spline
	 * @param vMax the robot's maximum velocity
	 * @param aMax the robot's maximum acceleration
	 */
	public MotionProfile(double distance, double timestep, double vMax, double aMax) {
		this.distance = distance;
		this.vMax = vMax;
		this.aMax = aMax;
		
		double maxPosition = (vMax*vMax) / aMax;
		if (maxPosition >= distance) {
			type = ProfileType.Triangular;
			t1 = Math.sqrt(distance / aMax);
			t2 = 2*t1; // correct?
			this.vMax = aMax*t1;
		} else {
			type = ProfileType.Trapezoidal;
			t1 = vMax / aMax;
			d1 = t1*vMax*0.5;
			d3 = d2; 
			d2 = distance - d1 - d3;
			t2 = (d2 / vMax) + t1;
			t3 = 2*t1+t2; 
		}
		
	}
	
	/**
	 * Finds the velocity at a given time.<br><Br>
	 * During the accelerating phase, the velocity can be found by the formula v=v0+at<br>
	 * During the cruise phase, velocity is constant at vMax<br>
	 * During the decelerating phase, the velocity can be found by the formula v=v0-at
	 * @param t
	 * @return velocity at t
	 */
	public double getVelocity(double t) {
		double v = 0;
		if (type == ProfileType.Trapezoidal) {
			if (t >= t0 && t < t1) { // accelerating phase
				v = aMax*t;
			} else if (t >= t1 && t <= t2) { // cruise phase
				v = vMax;
			} else if (t > t2 && t <= t3) { // decelerating phase
				return vMax - aMax*(t-t2);
			}
			return distance >= 0.0 ? v : (distance < 0.0 ? -v : 0);
		} else if (type == ProfileType.Triangular) {
			if (t <= t1) { // accelerating phase
				return aMax*t;
			} else if (t > t1 && t <= t2) { // decelerating phase
				return vMax - aMax*(t-t1); 
			}
			return 0;
		}
		return 0;
	}
	
	/**
	 * Finds the acceleration at a given time.<br><br>
	 * During the accelerating phase, the acceleration is aMax<br>
	 * During the cruise phase, the acceleration is 0<br>
	 * During the decelerating phase, the acceleration is -aMax
	 * @param t
	 * @return acceleration at t
	 */
	public double getAcceleration(double t) {
		double a = 0;
		if (type == ProfileType.Trapezoidal) {
			if (t >= t0 && t < t1) { // accelerating phase
				return aMax;
			} else if (t >= t1 && t <= t2) { // cruise phase
				return 0;
			} else if (t > t2 && t <= t3) { // deccelerating phase
				return -aMax;
			}
			return 0;
		} else if (type == ProfileType.Triangular) {
			if (t <= t1) { // acceleration phase
				return aMax;
			} else if (t > t1 && t <= t2) { // decelerating phase
				return -aMax;
			}
			return 0;
		}
		return 0;
	}
	
	/**
	 * Returns the elapsed time for the profile. 
	 * (A trapezoidal profile has 3 phases and 3 time intervals, a triangular profile has 2 phases and 2 time intervals)
	 * @return t final
	 */
	public double getFinalTime() {
		return (type == ProfileType.Trapezoidal) ? t3 : t2;
	}

}
