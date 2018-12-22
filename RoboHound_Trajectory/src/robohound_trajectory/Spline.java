package robohound_trajectory;

public class Spline {
	
	double x0, x1, dx, Ax, Bx, Cx, Dx;
	double y0, y1, dy, Ay, By, Cy, Dy;
	double theta0, theta1;
	
	public Spline(Waypoint pos1, Waypoint pos2) {
		 x0 = pos1.getTranslation().getX();
		 x1 = pos2.getTranslation().getX();
		
		 y0 = pos1.getTranslation().getY();
		 y1 = pos2.getTranslation().getY();
		
		 theta0 = RobotMath.deg_to_rad(pos1.getRotation().getHeading());
		 theta1 = RobotMath.deg_to_rad(pos2.getRotation().getHeading());
	
		 dx = 2 * Math.cos(theta0) * pos1.getDistance(pos2);
		 dy = 2 * Math.sin(theta1) * pos1.getDistance(pos2);
		
		 Ax = 2*x0-2*x1+dx+dx; // dx0, dx1?
		 Bx = -2*dx-dx-3*x0+3*x1;
		 Cx = dx;
		 Dx = x0;
		
		 Ay = 2*y0-2*y1+dy+dy; // dx0, dx1?
		 By = -2*dy-dy-3*y0+3*y1;
		 Cy = dy;
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

}
