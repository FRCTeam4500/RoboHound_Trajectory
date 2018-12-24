package robohound_trajectory;

import robohound_trajectory.position.Waypoint;

public class Spline {
	
	double x0, x1, dx0, dx1, Ax, Bx, Cx, Dx;
	double y0, y1, dy0, dy1, Ay, By, Cy, Dy;
	double theta0, theta1;
	
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
		
		 Ax = 2*x0-2*x1+dx1+dx0; // dx0, dx1?
		 Bx = -2*dx0-dx1-3*x0+3*x1;
		 Cx = dx0;
		 Dx = x0;
		
		 Ay = 2*y0-2*y1+dy1+dy0; // dx0, dx1?
		 By = -2*dy0-dy1-3*y0+3*y1;
		 Cy = dy0;
		 Dy = y0;
	}
	
	public double[] getLocation(double t) {
		return new double[] {getX(t), getY(t)};
	}
	
	public double getX(double t) {
		return Ax*t*t*t+Bx*t*t+Cx*t+Dx;
	}
	
	public double getY(double t) {
		return Ay*t*t*t+By*t*t+Cy*t+Dy;
	}
	
	public double getHeading(double t) {
		double headingX = 3*Ax*t*t+2*Bx*t+Cx;
		double headingY = 3*Ay*t*t+2*Bx*t+Cx;
		return Math.atan2(headingY, headingX);
	}

}
