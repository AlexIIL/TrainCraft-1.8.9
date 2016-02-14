package alexiil.mods.traincraft.compat.vanilla;

import net.minecraft.util.AxisAlignedBB;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import alexiil.mods.traincraft.api.component.IComponentInner;
import alexiil.mods.traincraft.api.train.IRollingStock;
import alexiil.mods.traincraft.component.inner.InnerItemStorage;

public class InnerVanillaChest extends InnerItemStorage {
    public InnerVanillaChest() {
        super(0, new AxisAlignedBB(-0.45, 0.1, -0.45, 0.45, 0.6, 0.45), 27);
    }

    @Override
    public void tick() {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(IRollingStock stock, float partialTicks) {

    }

    @Override
    public IComponentInner createNew(IRollingStock stock) {
        return new InnerVanillaChest();
    }
}
