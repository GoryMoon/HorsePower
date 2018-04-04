package se.gory_moon.horsepower.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.blocks.BlockPress;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.tileentity.TileEntityPress;

public class TileEntityPressRender extends TileEntityHPBaseRenderer<TileEntityPress> {

    @Override
    public void render(TileEntityPress te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        if (!(blockState.getBlock() instanceof BlockHPBase)) return;
        IBlockState topState = blockState.withProperty(BlockPress.PART, PressModels.TOP);
        if (!(topState.getBlock() instanceof BlockHPBase)) return;
        IBakedModel pressModel = dispatcher.getBlockModelShapes().getModelForState(topState);

        preDestroyRender(destroyStage);
        setRenderSettings();

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
        // This makes the translations that follow much easier
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );

        if (destroyStage >= 0) {
            buffer.noColor();
            renderBlockDamage(topState, te.getPos(), getDestroyBlockIcon(destroyStage), te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), pressModel, blockState, te.getPos(), buffer, false );

        buffer.setTranslation( 0, 0, 0 );

        GlStateManager.pushMatrix();
        GlStateManager.translate( x, y, z );

        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
        float move = (te.getField(0) / (float)(Configs.general.pointsForPress > 0 ? Configs.general.pointsForPress: 1));
        GlStateManager.translate( 0.5, 0.5, 0.5 );
        GlStateManager.translate( 0, -( 0.58 * move), 0 );
        GlStateManager.translate( -0.5, -0.5, -0.5 );

        tessellator.draw();
        GlStateManager.popMatrix();
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        postDestroyRender(destroyStage);
        RenderHelper.enableStandardItemLighting();

        if (!(blockState.getBlock() instanceof BlockHPBase)) return;

        if (te.hasWorker())
            renderLeash(te.getWorker(), x, y, z, 0D, 0.4D, 0D, partialTicks, te.getPos());

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        if (!te.getStackInSlot(0).isEmpty() && move <= 0.25) {
            renderItem(te, te.getStackInSlot(0), 0.5F, 0.5F, 0.5F, 1F);
            drawString(te, String.valueOf(te.getStackInSlot(0).getCount()), 0, 0.35, 0);
        }

        if (!te.getStackInSlot(1).isEmpty() && move <= 0.25) {
            renderItem(te, te.getStackInSlot(1), 0.5F, 0.5F, 0.5F, 1F);
            drawString(te, String.valueOf(te.getStackInSlot(1).getCount()), 0, 0.35,  0);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        drawDisplayText(te, x, y + 1, z);

        if (!te.isValid())
            renderInvalidArea(te.getWorld(), te.getPos(), -1);
        GlStateManager.popMatrix();
        }
}
