package robohound_trajectory;

public class Trajectory {
	
	public static class Segment {
        public double dt, x, y, position, velocity, acceleration, jerk, heading;

        // dt is timestep
        // position is distance of x and y from start
        public Segment(double dt, double x, double y, double heading, double velocity, double acceleration) {
            this.dt = dt;
            this.x = x;
            this.y = y;
//            this.position = position;
            this.velocity = velocity;
            this.acceleration = acceleration;
//            this.jerk = jerk;
            this.heading = heading;
        }

//        public Segment copy() {
//            return new Segment(dt, x, y, position, velocity, acceleration, jerk, heading);
//        }

        public boolean equals(Segment seg) {
            return  seg.dt == dt && seg.x == x && seg.y == y &&
                    seg.position == position && seg.velocity == velocity &&
                    seg.acceleration == acceleration && seg.jerk == jerk && seg.heading == heading;
        }

        public boolean fuzzyEquals(Segment seg) {
            return  ae(seg.dt, dt) && ae(seg.x, x) && ae(seg.y, y) && ae(seg.position, position) &&
                    ae(seg.velocity, velocity) && ae(seg.acceleration, acceleration) && ae(seg.jerk, jerk) &&
                    ae(seg.heading, heading);
        }

        private boolean ae(double one, double two) {
            return Math.abs(one - two) < 0.0001;        // Really small value
        }
    }

    /**
     * The Fit Method defines the function by which the trajectory path is generated. By default, the HERMITE_CUBIC method
     * is used.
     */
    public static enum FitMethod {
        HERMITE_CUBIC, HERMITE_QUINTIC;
    }

    public Segment[] segments;
    public TrapezoidalProfile profile;

    public Trajectory(Segment[] segments, TrapezoidalProfile profile) {
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

//    public Trajectory copy() {
//        Trajectory toCopy = new Trajectory(length());
//        for (int i = 0; i < length(); i++) {
//            toCopy.segments[i] = get(i).copy();
//        }
//        return toCopy;
//    }
}
