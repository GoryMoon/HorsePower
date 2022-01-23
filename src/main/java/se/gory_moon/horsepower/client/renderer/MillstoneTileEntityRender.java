package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import se.gory_moon.horsepower.blocks.HPBlock;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.client.model.modelvariants.MillstoneModels;
import se.gory_moon.horsepower.client.utils.LeadRendererHelper;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;

public class MillstoneTileEntityRender extends HPBaseTileEntityRenderer<MillstoneTileEntity> {

    public static final Vector3f IN_POS = new Vector3f(0.5F, 1F, 0.5F);

    public MillstoneTileEntityRender(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MillstoneTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.isAir() || !(state.getBlock() instanceof HPBlock))
            return;

        ItemStack outputStack = te.getStackInSlot(1);
        ItemStack secondaryStack = te.getStackInSlot(2);
        if (outputStack.getCount() < secondaryStack.getCount())
            outputStack = secondaryStack;

        if (state.get(MillstoneBlock.FILLED)) {
            BlockState filledState = state.with(MillstoneBlock.PART, MillstoneModels.FILLED);

            matrix.push();

            matrix.translate(0.5, 0.5, 0.5);
            float maxStackSize = outputStack.getMaxStackSize() > 0 ? outputStack.getMaxStackSize(): 1F;
            float fillState = 0.23F * (((float) outputStack.getCount()) / maxStackSize);
            matrix.translate(0, -0.187 + fillState, 0);
            matrix.translate(-0.5, -0.5, -0.5);

            renderModel(matrix, renderer, filledState, world, pos, combinedOverlayIn);

            matrix.pop();
        } else if (outputStack.isEmpty()) {
            te.renderStack = ItemStack.EMPTY;
            te.millColor = -1;
        }

        if (te.hasWorker())
            LeadRendererHelper.renderLeash(te.getWorker(), Vector3d.copyCentered(te.getPos()), partialTicks, matrix, renderer);

        matrix.push();
        if (!te.getStackInSlot(0).isEmpty()) {
            renderRotatingItem(te.getStackInSlot(0), IN_POS, 1F, matrix, renderer, combinedLightIn, combinedOverlayIn);
            if (world.isAirBlock(pos.up()))
                drawString(world, pos, Direction.NORTH, String.valueOf(te.getStackInSlot(0).getCount()), IN_POS.getX(), IN_POS.getY() + 0.5F, IN_POS.getZ(), matrix, renderer, combinedLightIn, combinedOverlayIn);
        }
        matrix.pop();
/*
        GlStateManager.pushMatrix();
        drawDisplayText(te, x, y, z);

        if (te.isInvalid())
            RenderUtils.renderInvalidArea(te.getWorld(), te.getPos(), -1);
        GlStateManager.popMatrix();
         */
    }
}
