package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import se.gory_moon.horsepower.tileentity.FillerTileEntity;

public class FillerTileEntityRender extends TileEntityRenderer<FillerTileEntity> {

    public FillerTileEntityRender(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
        super(tileEntityRendererDispatcher);
    }

    @Override
    public void render(FillerTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLight, int combinedOverlayIn) {
        ITextComponent itextcomponent = te.getDisplayName();

        RayTraceResult raytraceresult = this.renderDispatcher.cameraHitResult;
        if (itextcomponent != null && raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK && te.getPos().equals(((BlockRayTraceResult) raytraceresult).getPos())) {
            //TileEntityHPBaseRenderer.drawCustomNameplate(renderDispatcher, matrixStackIn, renderDispatcher.getFontRenderer(), itextcomponent, bufferIn, packedLight, te.getPos().getY(), 12, 0);
            //TileEntityHPBaseRenderer.drawCustomNameplate(renderDispatcher, matrixStackIn, renderDispatcher.getFontRenderer(), TileEntityHPBaseRenderer.LEAD_LOOKUP, bufferIn, packedLight, te.getPos().getY(), 12, -0.25D);
        }
    }
}
