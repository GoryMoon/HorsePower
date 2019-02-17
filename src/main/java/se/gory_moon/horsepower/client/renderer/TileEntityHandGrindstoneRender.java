package se.gory_moon.horsepower.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.blocks.BlockHandGrindstone;
import se.gory_moon.horsepower.client.model.modelvariants.HandGrindstoneModels;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;

public class TileEntityHandGrindstoneRender extends TileEntityHPBaseRenderer<TileEntityHandGrindstone> {

    @Override
    public void render(TileEntityHandGrindstone te, double x, double y, double z, float partialTicks, int destroyStage) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        if (!(blockState.getBlock() instanceof BlockHPBase)) return;
        IBlockState centerState = blockState.with(BlockHandGrindstone.PART, HandGrindstoneModels.CENTER);
        if (!(centerState.getBlock() instanceof BlockHPBase)) return;
        IBakedModel centerModel = dispatcher.getBlockModelShapes().getModel(centerState);

        preDestroyRender(destroyStage);
        setRenderSettings();

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
        // This makes the translations that follow much easier
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );

        if (destroyStage >= 0) {
            buffer.noColor();
            renderBlockDamage(centerState, te.getPos(), getDestroyBlockIcon(destroyStage), te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), centerModel, centerState, te.getPos(), buffer, false, getWorld().rand, blockState.getPositionRandom(te.getPos()));

        buffer.setTranslation( 0, 0, 0 );

        GlStateManager.pushMatrix();
        GlStateManager.translated( x, y, z );

        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
        GlStateManager.translated( 0.5, 0.5, 0.5 );
        FacingToRotation.get( te.getForward()).glRotateCurrentMat();
        float rotation = te.getVisibleRotation();
        GlStateManager.rotatef( rotation, 0, 1, 0 );
        GlStateManager.translated( -0.5, -0.5, -0.5 );

        tessellator.draw();
        GlStateManager.popMatrix();
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        postDestroyRender(destroyStage);
        RenderHelper.enableStandardItemLighting();

        renderItemWithFacing(te.getWorld(), te, te.getStackInSlot(0), x, y, z, 0.8F, 0.7F, 0.5F, 0.7F);
        renderItemWithFacing(te.getWorld(), te, te.getStackInSlot(1), x, y, z, 0.2F, 0.7F, 0.5F, 0.7F);
        renderItemWithFacing(te.getWorld(), te, te.getStackInSlot(2), x, y, z, 0.5F, 0.7F, 0.2F, 0.7F);
    }

}
