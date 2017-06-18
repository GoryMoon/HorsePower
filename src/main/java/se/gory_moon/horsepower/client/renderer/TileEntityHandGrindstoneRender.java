package se.gory_moon.horsepower.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;

public class TileEntityHandGrindstoneRender extends TileEntityHPBaseRenderer<TileEntityHandGrindstone> {

    private ModelResourceLocation centerModelLocation = new ModelResourceLocation("horsepower:hand_grindstone", "part=center");

    @Override
    public void renderTileEntityAt(TileEntityHandGrindstone te, double x, double y, double z, float partialTicks, int destroyStage) {

        // Most of this is blatantly copied from FastTESR
        Tessellator tessellator = Tessellator.getInstance();
        this.bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if( Minecraft.isAmbientOcclusionEnabled() )
        {
            GlStateManager.shadeModel( GL11.GL_SMOOTH );
        }
        else
        {
            GlStateManager.shadeModel( GL11.GL_FLAT );
        }

        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState( blockState );
        IBakedModel centerModel = dispatcher.getBlockModelShapes().getModelManager().getModel(centerModelLocation);

        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );
        dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), model, blockState, te.getPos(), buffer, false );
        buffer.setTranslation( 0, 0, 0 );
        GlStateManager.pushMatrix();
        GlStateManager.translate( x, y, z );
        tessellator.draw();
        GlStateManager.popMatrix();

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
        // This makes the translations that follow much easier
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );
        dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), centerModel, blockState, te.getPos(), buffer, false );
        buffer.setTranslation( 0, 0, 0 );

        GlStateManager.pushMatrix();
        GlStateManager.translate( x, y, z );

        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
        GlStateManager.translate( 0.5, 0.5, 0.5 );
        FacingToRotation.get( te.getForward()).glRotateCurrentMat();
        float rotation = te.getVisibleRotation();
        GlStateManager.rotate( rotation, 0, 1, 0 );
        GlStateManager.translate( -0.5, -0.5, -0.5 );

        tessellator.draw();

        GlStateManager.popMatrix();

        RenderHelper.enableStandardItemLighting();

        renderItemWithFacing(te.getWorld(), te, te.getStackInSlot(0), x, y, z, 0.5F, 0.7F, 0.8F, 0.7F);
        renderItemWithFacing(te.getWorld(), te, te.getStackInSlot(1), x, y, z, 0.5F, 0.7F, 0.2F, 0.7F);

        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
    }

}
