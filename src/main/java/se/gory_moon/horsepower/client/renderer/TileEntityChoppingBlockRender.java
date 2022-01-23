package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;

public class TileEntityChoppingBlockRender extends TileEntityHPBaseRenderer<ManualChopperTileEntity> {

    public TileEntityChoppingBlockRender(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ManualChopperTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        /*BlockPos pos = te.getPos();
        matrix.push();
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());
        if (!te.getStackInSlot(0).isEmpty())
            renderStillItem(te, te.getStackInSlot(0), 0.5F, 0.63F, 0.5F, 2F);
        matrix.pop();

        matrix.push();
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());
        if (!te.getStackInSlot(1).isEmpty())
            renderStillItem(te, te.getStackInSlot(1), 0.5F, 0.63F, 0.5F, 2F);
        matrix.pop();*/
    }
}
