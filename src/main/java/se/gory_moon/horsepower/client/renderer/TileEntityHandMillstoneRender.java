package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.blocks.BlockHandMillstone;
import se.gory_moon.horsepower.client.model.modelvariants.HandMillstoneModels;
import se.gory_moon.horsepower.tileentity.TileEntityHandMillstone;

public class TileEntityHandMillstoneRender extends TileEntityHPBaseRenderer<TileEntityHandMillstone> {

    @Override
    public void render(TileEntityHandMillstone te, double x, double y, double z, float partialTicks, int destroyStage) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        BlockState blockState = te.getWorld().getBlockState( te.getPos() );
        if (!(blockState.getBlock() instanceof BlockHPBase)) return;
        BlockState centerState = blockState.with(BlockHandMillstone.PART, HandMillstoneModels.CENTER);
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
            renderBlockDamage(centerState, te.getPos(), destroyStage, te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), centerModel, centerState, te.getPos(), buffer, false, getWorld().rand, blockState.getPositionRandom(te.getPos()));

        buffer.setTranslation( 0, 0, 0 );

        GlStateManager.pushMatrix();
        GlStateManager.translated( x, y, z );

        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
        GlStateManager.translated( 0.5, 0.5, 0.5 );
        FacingToRotation.get(te.getForward()).glRotateCurrentMat();
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
