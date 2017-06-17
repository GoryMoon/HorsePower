package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import se.gory_moon.horsepower.tileentity.TileEntityChopper;

public class TileEntityChopperRender extends TileEntityHPBaseRenderer<TileEntityChopper> {

    @Override
    public void renderTileEntityAt(TileEntityChopper te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.hasWorker())
            renderLeash(te.getWorker(), x, y, z, 0, partialTicks, te.getPos());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderItem(te.getWorld(), te.getStackInSlot(0), partialTicks);
        GlStateManager.popMatrix();

        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
    }

}
