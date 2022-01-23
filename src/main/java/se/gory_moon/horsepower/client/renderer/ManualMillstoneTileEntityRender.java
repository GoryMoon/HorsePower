package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import se.gory_moon.horsepower.blocks.HorizontalHPBlock;
import se.gory_moon.horsepower.blocks.ManualMillstoneBlock;
import se.gory_moon.horsepower.client.model.modelvariants.ManualMillstoneModels;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;

public class ManualMillstoneTileEntityRender extends HPBaseTileEntityRenderer<ManualMillstoneTileEntity> {

    public static final Vector3f IN_POS = new Vector3f(0.8F, 0.7F, 0.5F);
    public static final Vector3f OUT_POS = new Vector3f(0.2F, 0.7F, 0.5F);
    public static final Vector3f BONUS_POS = new Vector3f(0.5F, 0.7F, 0.2F);

    public ManualMillstoneTileEntityRender(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ManualMillstoneTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.isAir() || !(state.getBlock() instanceof HorizontalHPBlock))
            return;

        BlockState centerState = state.with(ManualMillstoneBlock.PART, ManualMillstoneModels.CENTER);

        matrix.push();
        rotateAroundCenter(matrix, te.getVisibleRotation());
        renderModel(matrix, renderer, centerState, world, pos, combinedOverlayIn);
        matrix.pop();

        Direction forward = te.getForward();
        renderItemWithFacing(world, pos, forward, te.getStackInSlot(0), IN_POS, 0.7F, matrix, renderer, combinedLightIn, combinedOverlayIn);
        renderItemWithFacing(world, pos, forward, te.getStackInSlot(1), OUT_POS, 0.7F, matrix, renderer, combinedLightIn, combinedOverlayIn);
        renderItemWithFacing(world, pos, forward, te.getStackInSlot(2), BONUS_POS, 0.7F, matrix, renderer, combinedLightIn, combinedOverlayIn);
    }
}
