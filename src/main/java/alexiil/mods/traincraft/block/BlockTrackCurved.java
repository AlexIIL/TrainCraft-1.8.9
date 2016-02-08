package alexiil.mods.traincraft.block;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import alexiil.mods.traincraft.TrainCraft;
import alexiil.mods.traincraft.api.ITrackPath;
import alexiil.mods.traincraft.api.TrackPath2DArc;
import alexiil.mods.traincraft.api.TrackPathComposite;
import alexiil.mods.traincraft.api.TrackPathStraight;
import alexiil.mods.traincraft.lib.MathUtil;

public class BlockTrackCurved extends BlockTrackSeperated {
    /** Designates whether this track goes in a positive direction after this or a negative direction: if
     * {@link #PROPERTY_FACING} was {@link EnumFacing#NORTH} (-Z) then if this was true this would curve into positive X
     * values. (So it would got NORTH_TO_NORTH_EAST */
    public static final PropertyBool PROPERTY_DIRECTION = PropertyBool.create("direction");

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, TRACK_HEIGHT, 1);

    // private final Table<EnumFacing, Boolean, TrackPathComposite<TrackPath2DArc, TrackPathStraight>> trackPaths;
    private final Table<EnumFacing, Boolean, ITrackPath> trackPaths;

    public BlockTrackCurved(int width) {
        super(PROPERTY_FACING, PROPERTY_DIRECTION);
        if (width < 2) throw new IllegalArgumentException("Must be at least 2 wide!");
        trackPaths = HashBasedTable.create();
        TrainCraft.trainCraftLog.info("Curved track with a width of " + width);

        BlockPos creator = new BlockPos(0, 0, 0);
        for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {

            Axis axis = horizontal.getAxis();
            int thing = (int) (horizontal.getAxisDirection().getOffset() * -0.5 + 0.5);
            Vec3 startPoint = new Vec3(axis == Axis.Z ? 0.5 : thing, TRACK_HEIGHT, axis == Axis.X ? 0.5 : thing);
            double length = width * 2 - 0.5;// TODO: test values!

            for (boolean positive : new boolean[] { false, true }) {
                Vec3 A = startPoint;
                Vec3 aDir = new Vec3(horizontal.getFrontOffsetX(), 0, horizontal.getFrontOffsetZ());

                double offset = width - 0.5;
                if (!positive) offset *= -1;
                Vec3 B = A.addVector(//
                        axis == Axis.X ? horizontal.getFrontOffsetX() * length : offset,//
                        0,// Never change the Y
                        axis == Axis.Z ? horizontal.getFrontOffsetZ() * length : offset //
                );
                double pOffset = positive ? 1 : -1;
                Vec3 bDir = aDir.addVector(//
                        axis == Axis.X ? 0 : pOffset,//
                        0,//
                        axis == Axis.Z ? 0 : pOffset //
                );

                TrainCraft.trainCraftLog.info(width + "##" + horizontal + ", " + positive + " bDir = " + bDir);

                /* Calculate a path from A to B, where A is the start point heading in ^A, and B is the end point
                 * heading in ^B.
                 * 
                 * Calculate C as the join of the lines ^A from A and ^B from B.
                 * 
                 * Calculate distance d from the minumum of (AC, BC)
                 * 
                 * Calculate D (on AC) as d distance from C towards A
                 * 
                 * Calculate E (on BC) as d distance from C towards B
                 * 
                 * Calculate F as the join of the lines perpendicular from ^A from D and perpendicular from ^B from E
                 * 
                 * Calculate r as the distance FD */
                Vec3 C = MathUtil.findCommonPoint(A, aDir, B, bDir);

                double ac = A.distanceTo(C);
                double bc = B.distanceTo(C);
                double d = Math.min(ac, bc);

                Vec3 D = d == ac ? A : C.add(scale(A.subtract(C), d / ac));

                Vec3 E = d == bc ? B : C.add(scale(B.subtract(C), d / bc));

                Vec3 F = MathUtil.findCommonPoint(D, perp(aDir), E, perp(bDir));

                double r = F.distanceTo(D);

                int angD = 0;
                int angE = 45;

                TrackPathStraight pathAD = new TrackPathStraight(A, D, creator);
                TrackPath2DArc pathDE = TrackPath2DArc.createDegrees(creator, F, r, angD, angE);
                // TrackPathStraight pathDE = new TrackPathStraight(D, E, creator);
                TrackPathStraight pathEB = new TrackPathStraight(E, B, creator);

                TrackPathComposite<TrackPathStraight, TrackPath2DArc, TrackPathStraight> composite;
                composite = new TrackPathComposite<>(creator, pathAD, pathDE, pathEB);
                trackPaths.put(horizontal, positive, composite);
            }
        }
    }

    private static Vec3 perp(Vec3 vec) {
        return new Vec3(-vec.zCoord, vec.yCoord, vec.xCoord);
    }

    private static Vec3 scale(Vec3 vec, double scale) {
        return new Vec3(vec.xCoord * scale, vec.yCoord * scale, vec.zCoord * scale);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return BOUNDING_BOX.offset(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return BOUNDING_BOX.offset(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        setBlockBounds(0, 0, 0, 1, TRACK_HEIGHT, 1);
    }

    @Override
    public ITrackPath[] paths(IBlockAccess access, BlockPos pos, IBlockState state) {
        boolean positive = state.getValue(PROPERTY_DIRECTION);
        EnumFacing mainDirection = state.getValue(PROPERTY_FACING);
        return new ITrackPath[] { path(positive, mainDirection).offset(pos) };
    }

    public /* TrackPathComposite<TrackPath2DArc, TrackPathStraight> */ITrackPath path(boolean positive, EnumFacing mainDirection) {
        return trackPaths.get(mainDirection, positive);
    }

    @Override
    public boolean isSlave(IBlockAccess access, BlockPos masterPos, IBlockState masterState, BlockPos slavePos, IBlockState slaveState) {
        /* FIXME: Do a better check to actually make sure that the given block really is included in the path! */
        return true;
    }
}
