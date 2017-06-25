package se.gory_moon.horsepower.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.blocks.BlockGrindstone;
import se.gory_moon.horsepower.client.renderer.modelvariants.GrindStoneModels;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;

public class TileEntityGrindstoneRender extends TileEntityHPBaseRenderer<TileEntityGrindstone> {

    @Override
    public void renderTileEntityAt(TileEntityGrindstone te, double x, double y, double z, float partialTicks, int destroyStage) {
        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        ItemStack outputStack = te.getStackInSlot(1);
        if (blockState.getValue(BlockGrindstone.FILLED)) {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            IBlockState filledState = blockState.withProperty(BlockGrindstone.PART, GrindStoneModels.FILLED);
            IBakedModel filledModel = dispatcher.getBlockModelShapes().getModelForState(filledState);

            setRenderSettings();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), filledModel, filledState, te.getPos(), buffer, false);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            GlStateManager.translate( 0.5, 0.5, 0.5 );
            float maxStackSize = outputStack.getMaxStackSize() > 0 ? outputStack.getMaxStackSize(): 1F;
            float fillState = 0.23F * (((float)outputStack.getCount()) / maxStackSize);
            GlStateManager.translate( 0, -0.187 + fillState, 0 );
            GlStateManager.translate( -0.5, -0.5, -0.5 );

            tessellator.draw();
            GlStateManager.popMatrix();
            buffer.setTranslation(0.0D, 0.0D, 0.0D);
            RenderHelper.enableStandardItemLighting();
        } else if (outputStack.isEmpty()) {
            te.renderStack = ItemStack.EMPTY;
            te.grindColor = null;
        }

        if (te.hasWorker())
            renderLeash(te.getWorker(), x, y, z, 0D, 0D, 0D, partialTicks, te.getPos());

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderItem(te.getWorld(), te.getStackInSlot(0), 0.5F, 1F, 0.5F, 1F);
        GlStateManager.popMatrix();

        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
    }
}
