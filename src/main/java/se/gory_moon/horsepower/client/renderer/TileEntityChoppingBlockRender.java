package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import se.gory_moon.horsepower.tileentity.TileEntityManualChopper;

public class TileEntityChoppingBlockRender extends TileEntityHPBaseRenderer<TileEntityManualChopper> {

    @Override
    public void render(TileEntityManualChopper te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderStillItem(te, te.getStackInSlot(0), 0.5F, 0.63F, 0.5F, 2F);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderStillItem(te, te.getStackInSlot(1), 0.5F, 0.63F, 0.5F, 2F);
        GlStateManager.popMatrix();

        super.render(te, x, y + 1, z, partialTicks, destroyStage, alpha);
    }

}
