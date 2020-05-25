package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;

import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;

public class TileEntityChoppingBlockRender extends TileEntityHPBaseRenderer<ManualChopperTileEntity> {

    @Override
    public void render(ManualChopperTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        if (!te.getStackInSlot(0).isEmpty())
            renderStillItem(te, te.getStackInSlot(0), 0.5F, 0.63F, 0.5F, 2F);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        if (!te.getStackInSlot(1).isEmpty())
            renderStillItem(te, te.getStackInSlot(1), 0.5F, 0.63F, 0.5F, 2F);
        GlStateManager.popMatrix();

        super.render(te, x, y + 1, z, partialTicks, destroyStage);
    }

}
