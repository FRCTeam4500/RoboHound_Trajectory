package robohound_trajectory.other;

public class RobotMath {
	
	public static double deg_to_rad(double deg) {
		return deg*(Math.PI/180);
	}
	
	public static double rad_to_deg(double rad) {
		return rad*(180/Math.PI);
	}
	
//	public static int floorInt(double num) {
//		return (int) Math.floor(num);
//	}
	
	 /**
     * Rounds the specified value to the nearest specified multiple
     *
     * @param val    the number to round
     * @param multiple the multiple to round val to
     * @return number rounded to nearest multiple
     */
	public static double roundApprox(double val, double multiple) {
        return Math.round(val / multiple) * multiple;
    }
	
	public static double roundTruncate(double val, double n) {
		int trimmedInt = (int) (val*Math.pow(10, n));
		double truncated = trimmedInt / Math.pow(10, n);
		return truncated;
	}
	
	public static String truncatedToString(double num) {
		return String.valueOf(roundTruncate(num, 5));
	}
	
	/**
	 * Converts a time (t) on the range [0, tFinal] to [0, 1] 
	 * @param t time at the given interval
	 * @param dt change in time
	 * @return
	 */
	public static double timeTransformation(double t, double tFinal) {
		return t / tFinal;
	}

}
