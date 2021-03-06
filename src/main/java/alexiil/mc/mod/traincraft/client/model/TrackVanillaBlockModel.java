package alexiil.mc.mod.traincraft.client.model;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;

import alexiil.mc.mod.traincraft.api.track.model.RailGeneneratorParams;
import alexiil.mc.mod.traincraft.api.track.path.ITrackPath;
import alexiil.mc.mod.traincraft.compat.vanilla.BehaviourVanillaNative;

public abstract class TrackVanillaBlockModel extends TrackGenericBlockModel {
    public static final int CURVED_RAIL_GAP = 16;

    public static TrackVanillaBlockModel create(Block block) {
        if (block == Blocks.ACTIVATOR_RAIL) return new ActivatorModel();
        else if (block == Blocks.DETECTOR_RAIL) return new DetectorModel();
        else if (block == Blocks.GOLDEN_RAIL) return new GoldenModel();
        else return new NormalModel();
    }

    public static class NormalModel extends TrackVanillaBlockModel {
        public NormalModel() {}

        @Override
        public ITrackPath path(IBlockState state) {
            return BehaviourVanillaNative.Normal.INSTANCE.getDefaultPath(state);
        }

        @Override
        protected List<BakedQuad> generateRails(IBlockState state, ITrackPath path) {
            EnumRailDirection dir = state.getValue(BlockRail.SHAPE);
            TextureAtlasSprite sprite = CommonModelSpriteCache.INSTANCE.spriteVanillaRails(false);
            if (dir.isAscending() || dir.getMetadata() < 3) {
                RailGeneneratorParams args = new RailGeneneratorParams(sprite);
                return CommonModelSpriteCache.generateRails(path, args);
            }
            RailGeneneratorParams args = new RailGeneneratorParams(sprite);
            return CommonModelSpriteCache.generateRails(path, args.railGap(CURVED_RAIL_GAP));
        }
    }

    public static class DetectorModel extends TrackVanillaBlockModel {
        public DetectorModel() {}

        @Override
        public ITrackPath path(IBlockState state) {
            return BehaviourVanillaNative.Detector.INSTANCE.getDefaultPath(state);
        }

        @Override
        public void generateExtra(List<BakedQuad> quads, IBlockState state, ITrackPath path) {
            boolean powered = state.getValue(BlockRailDetector.POWERED);
            TextureAtlasSprite sprite = CommonModelSpriteCache.INSTANCE.spriteVanillaExtras();

            // Redstone Block
            RailGeneneratorParams args = new RailGeneneratorParams(sprite);
            args.width(2 / 16.0).radius(0).yOffset(-0.5 / 16.0).left(false);
            if (powered) args.uMin(6).uMax(8);
            else args.uMin(4).uMax(6);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));

            // Detecting block

        }
    }

    public static class ActivatorModel extends TrackVanillaBlockModel {
        public ActivatorModel() {}

        @Override
        public ITrackPath path(IBlockState state) {
            return BehaviourVanillaNative.Activator.INSTANCE.getDefaultPath(state);
        }

        @Override
        public void generateExtra(List<BakedQuad> quads, IBlockState state, ITrackPath path) {
            boolean powered = state.getValue(BlockRailPowered.POWERED);
            TextureAtlasSprite sprite = CommonModelSpriteCache.INSTANCE.spriteVanillaExtras();

            // Left rail
            RailGeneneratorParams args = new RailGeneneratorParams(sprite).width(1 / 16.0).radius(3.5 / 16.0).right(false);
            if (powered) args.uMin(6).uMax(7);
            else args.uMin(4).uMax(5);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));

            // Right rail
            args = new RailGeneneratorParams(sprite).width(1 / 16.0).radius(3.5 / 16.0).left(false);
            if (powered) args.uMin(7).uMax(8);
            else args.uMin(5).uMax(6);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));

            // Redstone Block
            args = new RailGeneneratorParams(sprite).width(2 / 16.0).radius(0).yOffset(-0.5 / 16.0).left(false);
            if (powered) args.uMin(6).uMax(8);
            else args.uMin(4).uMax(6);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));

        }
    }

    public static class GoldenModel extends TrackVanillaBlockModel {
        public GoldenModel() {}

        @Override
        public ITrackPath path(IBlockState state) {
            return BehaviourVanillaNative.Golden.INSTANCE.getDefaultPath(state);
        }

        @Override
        public void generateExtra(List<BakedQuad> quads, IBlockState state, ITrackPath path) {
            boolean powered = state.getValue(BlockRailPowered.POWERED);
            TextureAtlasSprite sprite = CommonModelSpriteCache.INSTANCE.spriteVanillaExtras();

            RailGeneneratorParams args = new RailGeneneratorParams(sprite).left(false).width(1 / 16.0).radius(3.5 / 16.0);
            if (powered) args.uMin(2).uMax(3);
            else args.uMin(0).uMax(1);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));

            args = new RailGeneneratorParams(sprite).right(false).width(1 / 16.0).radius(3.5 / 16.0);
            if (powered) args.uMin(3).uMax(4);
            else args.uMin(1).uMax(2);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));

            // Redstone Block
            args = new RailGeneneratorParams(sprite).width(2 / 16.0).radius(0).yOffset(-0.5 / 16.0).left(false);
            if (powered) args.uMin(6).uMax(8);
            else args.uMin(4).uMax(6);
            quads.addAll(CommonModelSpriteCache.generateRails(path, args));
        }
    }
}
