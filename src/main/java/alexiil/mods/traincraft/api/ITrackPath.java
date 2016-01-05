package alexiil.mods.traincraft.api;

import net.minecraft.util.Vec3;

public interface ITrackPath {
    /** Gets a Vec3 position that has been interpolated between
     * 
     * @param position A position between (and including) 0 and 1. Other values will throw an
     *            {@link IllegalArgumentException} */
    Vec3 interpolate(double position);

    /** Get the direction of the track path at a particular interpolated point. */
    Vec3 direction(double position);

    default Vec3 start() {
        return interpolate(0);
    }

    default Vec3 end() {
        return interpolate(1);
    }

    /** Reverses the track path. Useful if the start is the opposite end of what you need. Should just invert the result
     * of {@link #interpolate(double)}. */
    default ITrackPath reverse() {
        return new TrackPathReversed(this);
    }

    /** @return The length of this path. Straight sections will be equal to the distance between {@link #start()} and
     *         {@link #end()}. Curved sections will be longer. */
    double length();

    /** Offsets this track path by the given amount. Useful for aligning the start to what the end should be. */
    ITrackPath offset(Vec3 by);

    public static void checkInterp(double position) {
        if (position < 0) throw new IllegalArgumentException("Position (" + position + ") was less than 0!");
        if (position > 1) throw new IllegalArgumentException("Position (" + position + ") was greater than 1!");
    }

    public static Vec3 interpolate(Vec3 start, Vec3 end, double position) {
        double x = start.xCoord * position + end.xCoord * (1 - position);
        double y = start.yCoord * position + end.yCoord * (1 - position);
        double z = start.zCoord * position + end.zCoord * (1 - position);
        return new Vec3(x, y, z);
    }
}
