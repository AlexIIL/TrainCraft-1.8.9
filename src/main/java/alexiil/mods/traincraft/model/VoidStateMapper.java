package alexiil.mods.traincraft.model;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class VoidStateMapper extends StateMapperBase {
    public static final VoidStateMapper INSTANCE = new VoidStateMapper();

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock()), "normal");
    }
}
