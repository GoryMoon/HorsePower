package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.blocks.HPBlock;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;

public class TileEntityMillstoneRender extends TileEntityHPBaseRenderer<MillstoneTileEntity> {

    public TileEntityMillstoneRender(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MillstoneTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockState = te.getWorld().getBlockState(te.getPos());
        if (!(blockState.getBlock() instanceof HPBlock))
            return;

        ItemStack outputStack = te.getStackInSlot(1);
        ItemStack secondaryStack = te.getStackInSlot(2);
        if (outputStack.getCount() < secondaryStack.getCount())
            outputStack = secondaryStack;

        if (false/*blockState.get(MillstoneBlock.FILLED)*/) {
            /*BlockState filledState = blockState.with(MillstoneBlock.PART, MillstoneModels.FILLED);
            if (!(filledState.getBlock() instanceof HPBaseBlock))
                return;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            IBakedModel filledModel = dispatcher.getBlockModelShapes().getModel(filledState);

            setRenderSettings();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), filledModel, filledState, te.getPos(), buffer, false, te.getWorld().rand, blockState.getPositionRandom(te.getPos()));

            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);

            GlStateManager.translated(0.5, 0.5, 0.5);
            float maxStackSize = outputStack.getMaxStackSize() > 0 ? outputStack.getMaxStackSize(): 1F;
            float fillState = 0.23F * (((float) outputStack.getCount()) / maxStackSize);
            GlStateManager.translated(0, -0.187 + fillState, 0);
            GlStateManager.translated(-0.5, -0.5, -0.5);

            tessellator.draw();
            GlStateManager.popMatrix();
            buffer.setTranslation(0.0D, 0.0D, 0.0D);
            RenderHelper.enableStandardItemLighting();*/
        } else if (outputStack.isEmpty()) {
            te.renderStack = ItemStack.EMPTY;
            te.millColor = -1;
        }

        //if (te.hasWorker())
        //    renderLeash(te.getWorker(), x, y, z, 0D, 0D, 0D, partialTicks, te.getPos());

        /*GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        if (!te.getStackInSlot(0).isEmpty()) {
            renderItem(te, te.getStackInSlot(0), 0.5F, 1F, 0.5F, 1F);
            if (getWorld().isAirBlock(te.getPos().up()))
                drawString(te, String.valueOf(te.getStackInSlot(0).getCount()), 0, 0.35, 0);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        drawDisplayText(te, x, y, z);

        if (te.isInvalid())
            RenderUtils.renderInvalidArea(te.getWorld(), te.getPos(), -1);
        GlStateManager.popMatrix();
         */
    }
}
