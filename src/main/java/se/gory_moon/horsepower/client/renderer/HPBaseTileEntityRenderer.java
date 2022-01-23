package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
import se.gory_moon.horsepower.util.Localization;

public abstract class HPBaseTileEntityRenderer<T extends HPBaseTileEntity> extends TileEntityRenderer<T> {

    protected final BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
    public static ITextComponent LEAD_LOOKUP = new TranslationTextComponent(Localization.INFO.ITEM_REVEAL.key()).setStyle(Style.EMPTY.setFormatting(TextFormatting.RED));

    public HPBaseTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    /*
    public static void drawCustomNameplate(TileEntityRendererDispatcher renderDispatcher, MatrixStack matrix, FontRenderer fontRenderer, ITextComponent str, IRenderTypeBuffer buffer, int packedLight, double y, int maxDistance, double offset) {
        ActiveRenderInfo renderInfo = renderDispatcher.renderInfo;
        Vector3d view = renderInfo.getProjectedView();
        double d0 = renderInfo.getProjectedView().squareDistanceTo(view.x, view.y, view.z);

        if (d0 <= (double) (maxDistance * maxDistance)) {
            RenderUtils.renderNameplate(matrix, fontRenderer, str, buffer, packedLight, y + offset, renderInfo.getRotation());
            //RenderUtils.renderNameplate(matrix, fontRenderer, str, (float) x + 0.5F, (float) y + 1.5F + offset, (float) z + 0.5F, 0, yaw, pitch, false);
        }
    }

    public void drawDisplayText(HPBaseTileEntity te, double x, double y, double z) {
        ITextComponent itextcomponent = te.getDisplayName();

        RayTraceResult raytraceresult = this.renderDispatcher.cameraHitResult;
        if (itextcomponent != null && raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK && te.getPos().equals(((BlockRayTraceResult) raytraceresult).getPos())) {
            this.setLightmapDisabled(true);
            drawCustomNameplate(renderDispatcher, getFontRenderer(), te, itextcomponent.getFormattedText(), x, y, z, 12, 0);
            drawCustomNameplate(renderDispatcher, getFontRenderer(), te, LEAD_LOOKUP.getFormattedText(), x, y, z, 12, -0.25F);
            this.setLightmapDisabled(false);
        }
    }
    */
    public void drawString(World world, BlockPos pos, Direction forward, String str, float x, float y, float z, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        if (!canShowAmount(pos, world))
            return;

        ActiveRenderInfo renderInfo = this.renderDispatcher.renderInfo;
        double d0 = renderInfo.getProjectedView().squareDistanceTo(pos.getX(), pos.getY(), pos.getZ());

        if (d0 <= (double) (14 * 14)) {
            FontRenderer fontRenderer = this.renderDispatcher.fontRenderer;
            matrix.push();

            float angle = forward.getOpposite().getHorizontalAngle();
            if (forward == Direction.EAST || forward == Direction.WEST)
                angle = forward.getHorizontalAngle();

            rotateAroundCenter(matrix, angle);

            matrix.translate(x, y, z);
            matrix.rotate(Vector3f.YP.rotationDegrees(-angle));
            matrix.rotate(renderInfo.getRotation());
            matrix.scale(-0.015F, -0.015F, 0.015F);

            fontRenderer.drawString(matrix, str, -fontRenderer.getStringWidth(str) / 2f, 0, -1);
            matrix.pop();
        }
    }

    public boolean canShowAmount(BlockPos pos, World world) {
        if (!Configs.CLIENT.renderItemAmount.get())
            return false;
        if (!Configs.CLIENT.mustLookAtBlock.get())
            return true;

        RayTraceResult traceResult = this.renderDispatcher.cameraHitResult;
        if (traceResult != null && traceResult.getType() == RayTraceResult.Type.BLOCK)
            return pos.equals(((BlockRayTraceResult) traceResult).getPos()) ||
                    (world.getBlockState(pos.up()).getBlock() instanceof FillerBlock && pos.up().equals(((BlockRayTraceResult) traceResult).getPos()));
        return false;
    }

    protected void renderStillItem(ItemStack stack, Vector3f pos, float scale, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        renderRotatingItem(stack, pos, scale, false, matrix, renderer, combinedLightIn, combinedOverlayIn);
    }

    protected void renderRotatingItem(ItemStack stack, Vector3f pos, float scale, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        renderRotatingItem(stack, pos, scale, true, matrix, renderer, combinedLightIn, combinedOverlayIn);
    }

    private void renderRotatingItem(ItemStack stack, Vector3f pos, float scale, boolean rotate, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        if (!stack.isEmpty()) {
            matrix.push();
            matrix.translate(pos.getX(), pos.getY(), pos.getZ());

            if (rotate)
                matrix.rotate(Vector3f.YP.rotationDegrees((float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL)));

            matrix.scale(scale, scale, scale);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrix, renderer);
            matrix.pop();
        }
    }

    protected void renderItemWithFacing(World world, BlockPos blockPos, Direction forward, ItemStack stack, Vector3f pos, float scale, MatrixStack matrix, IRenderTypeBuffer renderer, int combinedLightIn, int combinedOverlayIn) {
        if (stack.isEmpty())
            return;

        matrix.push();
        rotateAroundCenter(matrix, -forward.getHorizontalAngle() + 180);
        renderRotatingItem(stack, pos, scale, matrix, renderer, combinedLightIn, combinedOverlayIn);
        matrix.pop();

        drawString(world, blockPos, forward, String.valueOf(stack.getCount()), pos.getX(), pos.getY() + 0.3f, pos.getZ(), matrix, renderer, combinedLightIn, combinedOverlayIn);
    }

    protected void rotateAroundCenter(MatrixStack matrix, float degree) {
        matrix.translate(0.5, 0.5, 0.5);
        matrix.rotate(Vector3f.YP.rotationDegrees(degree));
        matrix.translate(-0.5, -0.5, -0.5);
    }

    protected void renderModel(MatrixStack matrix, IRenderTypeBuffer renderer, BlockState state, World world, BlockPos pos, int combinedOverlayIn) {
        IBakedModel centerModel = blockRenderer.getBlockModelShapes().getModel(state);
        IModelData data = centerModel.getModelData(world, pos, state, ModelDataManager.getModelData(world, pos));
        blockRenderer.getBlockModelRenderer().renderModel(world, centerModel, state, pos, matrix, renderer.getBuffer(Atlases.getCutoutBlockType()), false, world.rand, state.getPositionRandom(pos), combinedOverlayIn, data);
    }
}
