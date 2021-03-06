package alexiil.mc.mod.traincraft.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.common.property.IExtendedBlockState;

import alexiil.mc.mod.traincraft.api.track.model.*;
import alexiil.mc.mod.traincraft.api.track.model.ITrackModel.IModelComponent;
import alexiil.mc.mod.traincraft.api.track.model.ITrackModel.ISleeperGen;
import alexiil.mc.mod.traincraft.api.track.path.ITrackPath;

public class TrackGenericBlockModel_NEW_ extends PerspAwareModelBase {
    public static final TrackGenericBlockModel_NEW_ INSTANCE = new TrackGenericBlockModel_NEW_();

    private TrackGenericBlockModel_NEW_() {
        super(null, null, null);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) return ImmutableList.of();
        return handleBlockState(state);
    }

    public static List<BakedQuad> handleBlockState(IBlockState state) {
        if (!(state instanceof IExtendedBlockState)) return missingModel();
        IExtendedBlockState extended = (IExtendedBlockState) state;
        TrackModelWrapper[] wrappers = extended.getValue(TrackModelProperty.INSTANCE);
        if (!TrackModelProperty.INSTANCE.isValid(wrappers)) return missingModel();
        return makeModel(wrappers);
    }

    public static List<BakedQuad> makeModel(TrackModelWrapper[] wrappers) {
        // For now we will just bake each model as it comes.
        List<BakedQuad> allQuads = new ArrayList<>();

        double yOffset = 0;
        RailGeneneratorParams r = new RailGeneneratorParams(null);
        for (TrackModelWrapper wrap : wrappers) {
            ITrackPath path = wrap.path;
            ITrackModel model = wrap.model;
            if (model == null) {
                model = DefaultTrackModel.INSTANCE;
            }

            // Add rails
            for (RailGeneneratorParams rail : model.getRailGen()) {
                r.railSprite(rail.railSprite());
                r.railGap(rail.railGap());
                r.uMin(rail.uMin());
                r.uMax(rail.uMax());
                r.left(rail.left());
                r.right(rail.right());
                r.width(rail.width());
                r.radius(rail.radius());
                r.yOffset(rail.yOffset() + yOffset);
                allQuads.addAll(CommonModelSpriteCache.generateRails(path, r));
            }
            List<BakedQuad> sleepers = CommonModelSpriteCache.generateSleepers(path, CommonModelSpriteCache.INSTANCE.loadSleepers(), false);
            for (BakedQuad q : sleepers) {
                allQuads.add(ModelUtil.multiplyMatrix(q, MatrixUtil.translation(new Vec3d(0, yOffset, 0))));
            }
            for (ISleeperGen sleeper : model.getSleeperGen()) {

            }

            for (IModelComponent extra : model.getExtraComponents()) {
                allQuads.addAll(extra.generate(path));
            }
            yOffset += 0.001;
        }
        return allQuads;
    }
}
