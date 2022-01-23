package se.gory_moon.horsepower.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.tileentity.HPHorseBaseTileEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class RenderUtils {

    public static TextureAtlasSprite getTopTextureFromBlockstate(BlockState state) {
        BlockModelShapes modelShapes = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes();
        IBakedModel model = modelShapes.getModel(state);
        if (model != modelShapes.getModelManager().getMissingModel()) {
            List<BakedQuad> quads = model.getQuads(state, Direction.UP, Minecraft.getInstance().world.rand);
            return quads.size() >= 1 ? quads.get(0).getSprite(): modelShapes.getTexture(state);
        }
        return modelShapes.getTexture(state);
    }

    public static TextureAtlasSprite getTextureFromBlockstate(BlockState state) {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public static void renderNameplate(MatrixStack matrix, FontRenderer fontRenderer, ITextComponent name, IRenderTypeBuffer bufferIn, int packedLightIn, double y, Quaternion rotation) {
        double f = y + 0.5D;
        int i = 0;
        matrix.push();
        matrix.translate(0.0D, f, 0.0D);
        matrix.rotate(rotation);
        matrix.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
        int j = (int) (f1 * 255.0F) << 24;
        float f2 = (float) (-fontRenderer.getStringPropertyWidth(name) / 2);
        fontRenderer.func_243247_a(name, f2, (float) i, 553648127, false, matrix4f, bufferIn, true, j, packedLightIn);
        fontRenderer.func_243247_a(name, f2, (float) i, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
        matrix.pop();
    }

    //    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(IBakedModel model) {
    //        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
    //        for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
    //            TRSRTransformation transformation = new TRSRTransformation(model.handlePerspective(type).getRight());
    //            if (!transformation.equals(TRSRTransformation.identity())) {
    //                builder.put(type, TRSRTransformation.blockCenterToCorner(transformation));
    //            }
    //        }
    //        return builder.build();
//    }

    // Code based on code from The Betweenlands
    public static void renderInvalidArea(World world, BlockPos blockPos, int yOffset) {
        if (StreamSupport.stream(Minecraft.getInstance().player.getHeldEquipment().spliterator(), false).anyMatch(stack -> !stack.isEmpty() && stack.getItem() == Items.LEAD)) {
            renderUsedArea(world, blockPos, yOffset, 0.55F, 0.15F);
        }
    }

    private static void preBoundingBox() {
        RenderSystem.pushMatrix();

        RenderSystem.disableLighting();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0f);
        RenderSystem.color4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth(2F);

        //render
        RenderSystem.polygonOffset(-0.1F, -10.0F);
        RenderSystem.enablePolygonOffset();
    }

    private static void postBoundingBox() {
        RenderSystem.disablePolygonOffset();
        //render end

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableLighting();
        RenderSystem.popMatrix();
    }

    public static void renderSearchAreas(HPHorseBaseTileEntity te) {
        preBoundingBox();
        GlStateManager.color4f(1, 1, 1, 1);
        Vector3d vector3d = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double playerX = -vector3d.getX();
        double playerY = -vector3d.getY();
        double playerZ = -vector3d.getZ();
        Arrays.stream(te.searchAreas).filter(Objects::nonNull).map(aabb -> aabb.offset(playerX, playerY, playerZ)).forEach(RenderUtils::drawBoundingBox);
        postBoundingBox();
    }

    public static void renderUsedArea(World world, BlockPos blockPos, int yOffset, float invalidAplha, float validAplha) {
        preBoundingBox();
        Vector3d vector3d = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double playerX = -vector3d.getX();
        double playerY = -vector3d.getY();
        double playerZ = -vector3d.getZ();

        for (int xo = -3; xo <= 3; xo++) {
            for (int yo = yOffset; yo <= 1 + yOffset; yo++) {
                for (int zo = -3; zo <= 3; zo++) {
                    BlockPos pos = blockPos.add(xo, yo, zo);
                    if ((xo <= 1 && xo >= -1) && (zo <= 1 && zo >= -1))
                        continue;
                    if (pos.getY() >= 0) {
                        BlockState state = world.getBlockState(pos);
                        if (!state.getMaterial().isReplaceable()) {
                            GlStateManager.color4f(1, 0, 0, invalidAplha);
                            drawBoundingBoxOutline(new AxisAlignedBB(pos).offset(playerX, playerY, playerZ));
                            VoxelShape shape = state.getCollisionShape(world, pos).withOffset(pos.getX(), pos.getY(), pos.getZ()).withOffset(playerX, playerY, playerZ);
                            for (AxisAlignedBB aabb : shape.toBoundingBoxList()) {
                                drawBoundingBox(aabb);
                            }
                        } else {
                            GlStateManager.color4f(0, 1, 0, validAplha);
                            drawBoundingBoxOutline(new AxisAlignedBB(pos).offset(playerX, playerY, playerZ));
                        }
                    }
                }
            }
        }
        postBoundingBox();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawBoundingBox(AxisAlignedBB axisalignedbb) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        GL11.glEnd();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawBoundingBoxOutline(AxisAlignedBB par1AxisAlignedBB) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        GL11.glEnd();
    }
}
