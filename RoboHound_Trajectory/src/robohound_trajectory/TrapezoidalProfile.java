package robohound_trajectory;

public class TrapezoidalProfile {
	
	private double t0 = 0, t1, t2, t3, d1, d2, d3;
	private double distance, vMax, aMax;
	
	//TODO: TIME IS SLIGHTLY LONGER DUE TO ROUNDOFF ERROR, BOUNDARY PAST T3...?
	public TrapezoidalProfile(double distance, double timestep, double vMax, double aMax) {
		this.distance = distance;
		this.vMax = vMax;
		this.aMax = aMax;
		t0 = 0;
		t1 = vMax / aMax;
		d1 = vMax*t1*(.5);
		d3 = (vMax*vMax) / (2*aMax);
		d2 = distance - d1 - d3;
		t2 = (d2 / vMax) + t1;
		t3 = (-vMax / -aMax) + t2;
	}
	
	public double getVelocity(double t) {
		double v = 0;
		if (t >= t0 && t < t1) { // accelerating phase
			v = aMax*t;
		} else if (t >= t1 && t <= t2) { // cruise phase
			v = vMax;
		} else if (t > t2 && t <= t3) { // deccelerating phase
			return vMax - aMax*(t-t2);
		}
		return distance >= 0.0 ? v : (distance < 0.0 ? -v : 0);
	}
	
	public double getAcceleration(double t) {
		double a = 0;
		if (t >= t0 && t < t1) { // accelerating phase
			return aMax;
		} else if (t >= t1 && t <= t2) { // cruise phase
			return 0;
		} else if (t > t2 && t <= t3) { // deccelerating phase
			return -aMax;
		}
		return 0;
	}
	
	public double getTFinal() {
		return t3;
	}

}
