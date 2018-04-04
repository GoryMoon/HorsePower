package se.gory_moon.horsepower.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.blocks.BlockGrindstone;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.client.model.modelvariants.GrindStoneModels;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;

public class TileEntityGrindstoneRender extends TileEntityHPBaseRenderer<TileEntityGrindstone> {

    @Override
    public void render(TileEntityGrindstone te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        if (!(blockState.getBlock() instanceof BlockHPBase)) return;
        ItemStack outputStack = te.getStackInSlot(1);
        ItemStack secondaryStack = te.getStackInSlot(2);
        if (outputStack.getCount() < secondaryStack.getCount())
            outputStack = secondaryStack;

        if (blockState.getValue(BlockGrindstone.FILLED)) {
            IBlockState filledState = blockState.withProperty(BlockGrindstone.PART, GrindStoneModels.FILLED);
            if (!(filledState.getBlock() instanceof BlockHPBase)) return;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

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
        if (!te.getStackInSlot(0).isEmpty()) {
            renderItem(te, te.getStackInSlot(0), 0.5F, 1F, 0.5F, 1F);
            if (getWorld().isAirBlock(te.getPos().up()))
                drawString(te, String.valueOf(te.getStackInSlot(0).getCount()), 0, 0.35, 0);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();

        drawDisplayText(te, x, y, z);

        if (!te.isValid())
            renderInvalidArea(te.getWorld(), te.getPos(), -1);
        GlStateManager.popMatrix();
    }
}
