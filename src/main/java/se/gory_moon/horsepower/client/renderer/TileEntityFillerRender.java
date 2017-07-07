package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

public class TileEntityFillerRender extends TileEntitySpecialRenderer<TileEntityFiller> {

    @Override
    public void render(TileEntityFiller te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
