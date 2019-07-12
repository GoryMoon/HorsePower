package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

public class TileEntityFillerRender extends TileEntityRenderer<TileEntityFiller> {

    @Override
    public void render(TileEntityFiller te, double x, double y, double z, float partialTicks, int destroyStage) {
        drawDisplayText(te, x, y, z);
    }

    public void drawDisplayText(TileEntityFiller te, double x, double y, double z) {
        ITextComponent itextcomponent = te.getDisplayName();

        RayTraceResult raytraceresult = this.rendererDispatcher.cameraHitResult;
        if (itextcomponent != null && raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK && te.getPos().equals(((BlockRayTraceResult)raytraceresult).getPos())) {
            this.setLightmapDisabled(true);
            TileEntityHPBaseRenderer.drawCustomNameplate(rendererDispatcher, getFontRenderer(), te, itextcomponent.getFormattedText(), x, y, z, 12, 0);
            TileEntityHPBaseRenderer.drawCustomNameplate(rendererDispatcher, getFontRenderer(), te, TileEntityHPBaseRenderer.LEAD_LOOKUP.getFormattedText(), x, y, z, 12, -0.25F);
            this.setLightmapDisabled(false);
        }
    }
}
