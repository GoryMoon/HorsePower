package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

public class TileEntityFillerRender extends TileEntityRenderer<TileEntityFiller> {


    @Override
    public void render(TileEntityFiller te, double x, double y, double z, float partialTicks, int destroyStage) {
        drawDisplayText(te, x, y, z);
        super.render(te, x, y, z, partialTicks, destroyStage);
    }

    public void drawDisplayText(TileEntityFiller te, double x, double y, double z) {
        ITextComponent itextcomponent = te.getDisplayName();

        if (itextcomponent != null && this.rendererDispatcher.cameraHitResult != null && te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos()))
        {
            this.setLightmapDisabled(true);
            this.drawCustomNameplate(te, itextcomponent.getFormattedText(), x, y, z, 12, 0);
            this.drawCustomNameplate(te, TileEntityHPBaseRenderer.LEAD_LOOKUP.getFormattedText(), x, y, z, 12, -0.25F);
            this.setLightmapDisabled(false);
        }
    }

    protected void drawCustomNameplate(TileEntity te, String str, double x, double y, double z, int maxDistance, float offset)
    {
        Entity entity = this.rendererDispatcher.entity;
        double d0 = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);

        if (d0 <= (double)(maxDistance * maxDistance))
        {
            float f = this.rendererDispatcher.entityYaw;
            float f1 = this.rendererDispatcher.entityPitch;
            GameRenderer.drawNameplate(this.getFontRenderer(), str, (float)x + 0.5F, (float)y + 1.5F + offset, (float)z + 0.5F, 0, f, f1, false, false);
        }
    }
}
