package robohound_trajectory.generating;

import robohound_trajectory.other.RobotMath;
import robohound_trajectory.position.Waypoint;

/**
 * Defines the curvature the robot will follow between two points.<br>
 * A spline in motion profiling is a piecewise polynomial defined as a parametric equation<br>
 * x(t)=Ax*t^3+Bx*t^2+Cx*t+Dx<br>
 * y(t)=Ay*t^3+By*t^2+Cy*t+Dy<br>
 * pos(t)=(x(t),y(t))
 */
public class Spline {
	
	double x0, x1, dx0, dx1, Ax, Bx, Cx, Dx;
	double y0, y1, dy0, dy1, Ay, By, Cy, Dy;
	double theta0, theta1;
	
	/**
	 * Generates the curve between two waypoints
	 * @param pos1 the initial position
	 * @param pos2 the final position
	 */
	public Spline(Waypoint pos1, Waypoint pos2) {
		 x0 = pos1.getTranslation().getX();
		 x1 = pos2.getTranslation().getX();
		
		 y0 = pos1.getTranslation().getY();
		 y1 = pos2.getTranslation().getY();
		
		 theta0 = RobotMath.deg_to_rad(pos1.getRotation().getHeading());
		 theta1 = RobotMath.deg_to_rad(pos2.getRotation().getHeading());
	
		 dx0 = 2 * Math.cos(theta0) * pos1.getDistance(pos2);
		 dx1 = 2 * Math.cos(theta1) * pos1.getDistance(pos2);
		 dy0 = 2 * Math.sin(theta0) * pos1.getDistance(pos2);
		 dy1 = 2 * Math.sin(theta1) * pos1.getDistance(pos2);
		
		 Ax = 2*x0-2*x1+dx1+dx0; 
		 Bx = -2*dx0-dx1-3*x0+3*x1;
		 Cx = dx0;
		 Dx = x0;
		
		 Ay = 2*y0-2*y1+dy1+dy0;
		 By = -2*dy0-dy1-3*y0+3*y1;
		 Cy = dy0;
		 Dy = y0;
	}
	
	/**
	 * Returns an array of the x and y position at a given time
	 * @param t time
	 * @return double array in the form [x pos, y pos]
	 */
	public double[] getLocation(double t) {
		return new double[] {getX(t), getY(t)};
	}
	
	/**
	 * @param t time
	 * @return x position at a given time
	 */
	public double getX(double t) {
		return Ax*t*t*t+Bx*t*t+Cx*t+Dx;
	}

	public double getDx(double t) {
		return 3*Ax*t*t+2*Bx*t+Cx;
	}
	
	/**
	 * @param t time
	 * @return y position at a given time
	 */
	public double getY(double t) {
		return Ay*t*t*t+By*t*t+Cy*t+Dy;
	}

	public double getDy(double t) {
		return 3*Ay*t*t+2*By*t+Cy;
	}
	
	
	/**
	 * @param t time
	 * @return angle the wheels should be facing at a given time
	 */
	private double n = 500;
	public double getHeading(double t) {
		double headingX = getDx(t);
		double headingY = getDy(t);
		return Math.atan2(headingY, headingX);
	}
	
	
	/**
	 * Gets the length of the spline through a TSum approximation
	 * @param a
	 * @param b
	 * @return length of the spline
	 */
	public double getArcLength(double a, double b) {
		double deltaX = (b-a) / n;
		double xOf0 = getTrapezoidSum(a);
		double xOfn = getTrapezoidSum(a+100*deltaX);
		double sum = 0;
		for(int i = 0; i < n; i++) {
			sum += 2*(getTrapezoidSum(a+i*deltaX));
		}
		return deltaX/2 * (xOf0 + sum + xOfn);
	}
	
	/**
	 * Integrand that came from the equation for the arc length of a parametric
	 * @param t time
	 * @return f(t)
	 */
	private double getTrapezoidSum(double t) {
		return Math.sqrt(getDx(t)*getDx(t) + getDy(t)*getDy(t));
	}
	
	/**
	 * Gets the total length of multiple splines
	 * @param splines
	 * @return total distance that the robot should travel
	 */
	public static double getPathLength(Spline[] splines) {
		double sum = 0;
		for (Spline spline : splines) {
			sum += spline.getArcLength(0, 1);
		}
		return sum;
	}

}
