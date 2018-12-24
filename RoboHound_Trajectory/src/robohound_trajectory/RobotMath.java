package robohound_trajectory;

public class RobotMath {
	
	public static double deg_to_rad(double deg) {
		return deg*(Math.PI/180);
	}
	
	public static double rad_to_deg(double rad) {
		return rad*(180/Math.PI);
	}
	
	public static int floorInt(double num) {
		return (int) Math.floor(num);
	}
	
	 /**
     * Rounds the specified value to the nearest specified multiple
     *
     * @param val    the number to round
     * @param multiple the multiple to round val to
     * @return number rounded to nearest multiple
     */
	public static double round(double val, double multiple) {
        return Math.round(val / multiple) * multiple;
    }

}
