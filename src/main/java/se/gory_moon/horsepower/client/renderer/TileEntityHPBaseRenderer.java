package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
import se.gory_moon.horsepower.util.Localization;

public abstract class TileEntityHPBaseRenderer<T extends HPBaseTileEntity> extends TileEntityRenderer<T> {

    public static ITextComponent LEAD_LOOKUP = new TranslationTextComponent(Localization.INFO.ITEM_REVEAL.key()).setStyle(new Style().setColor(TextFormatting.RED));

    public static void drawCustomNameplate(TileEntityRendererDispatcher rendererDispatcher, FontRenderer fontRenderer, TileEntity te, String str, double x, double y, double z, int maxDistance, float offset) {
        ActiveRenderInfo renderInfo = rendererDispatcher.renderInfo;
        Vec3d view = renderInfo.getProjectedView();
        double d0 = te.getDistanceSq(view.x, view.y, view.z);

        if (d0 <= (double) (maxDistance * maxDistance)) {
            float yaw = renderInfo.getYaw();
            float pitch = renderInfo.getPitch();
            GameRenderer.drawNameplate(fontRenderer, str, (float) x + 0.5F, (float) y + 1.5F + offset, (float) z + 0.5F, 0, yaw, pitch, false);
        }
    }

    protected void renderStillItem(HPBaseTileEntity te, ItemStack stack, float x, float y, float z, float scale) {
        renderItem(te, stack, x, y, z, scale, false);
    }

    protected void renderItem(HPBaseTileEntity te, ItemStack stack, float x, float y, float z, float scale) {
        renderItem(te, stack, x, y, z, scale, true);
    }

    private void renderItem(HPBaseTileEntity te, ItemStack stack, float x, float y, float z, float scale, boolean rotate) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if (stack != null) {
            GlStateManager.translated(x, y, z);
            ItemEntity entityitem = new ItemEntity(te.getWorld(), 0.0D, 0.0D, 0.0D, stack.copy());
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            float rotation = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

            if (rotate)
                GlStateManager.rotatef(rotation, 0.0F, 1.0F, 0);
            GlStateManager.scalef(0.5F * scale, 0.5F * scale, 0.5F * scale);
            GlStateManager.pushLightingAttributes();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttributes();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public void drawString(HPBaseTileEntity te, String str, double x, double y, double z) {
        if (!canShowAmount(te))
            return;
        setLightmapDisabled(true);
        Entity entity = this.rendererDispatcher.renderInfo.getRenderViewEntity();
        double d0 = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);

        if (d0 <= (double) (14 * 14)) {
            float f = this.rendererDispatcher.renderInfo.getYaw();
            float f1 = this.rendererDispatcher.renderInfo.getPitch();
            FontRenderer fontRenderer = getFontRenderer();
            GlStateManager.pushMatrix();

            GlStateManager.translated(x, y, z);
            GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
            if (te.getForward() == Direction.EAST || te.getForward() == Direction.WEST)
                FacingToRotation.get(te.getForward().getOpposite()).glRotateCurrentMat();
            else
                FacingToRotation.get(te.getForward()).glRotateCurrentMat();

            GlStateManager.rotatef(-f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.scalef(-0.015F, -0.015F, 0.015F);
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.enableBlend();

            GlStateManager.enableDepthTest();
            GlStateManager.depthMask(true);
            fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, -1);

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
        setLightmapDisabled(false);
    }

    public boolean canShowAmount(HPBaseTileEntity te) {
        RayTraceResult traceResult = this.rendererDispatcher.cameraHitResult;
        return Configs.CLIENT.renderItemAmount.get() &&
                (!Configs.CLIENT.mustLookAtBlock.get() ||
                        traceResult != null &&
                                traceResult.getType() == RayTraceResult.Type.BLOCK &&
                                (te.getPos().equals(((BlockRayTraceResult) traceResult).getPos()) ||
                                        (te.getWorld().getBlockState(te.getPos().up()).getBlock() instanceof FillerBlock && te.getPos().up().equals(((BlockRayTraceResult) traceResult).getPos()))
                                )
                );
    }

    protected void renderItemWithFacing(World world, HPBaseTileEntity tile, ItemStack stack, double ox, double oy, double oz, float x, float y, float z, float scale) {
        if (stack.isEmpty())
            return;
        GlStateManager.pushMatrix();
        GlStateManager.translated(ox, oy, oz);
        GlStateManager.translated(0.5, 0.5, 0.5);
        FacingToRotation.get(tile.getForward()).glRotateCurrentMat();
        GlStateManager.translated(-0.5, -0.5, -0.5);
        renderItem(tile, stack, x, y, z, scale);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translated(ox, oy, oz);
        GlStateManager.translated(0.5, 0.5, 0.5);
        FacingToRotation.get(tile.getForward()).glRotateCurrentMat();
        GlStateManager.translated(-0.5, -0.5, -0.5);

        drawString(tile, String.valueOf(stack.getCount()), x, y + 0.3, z);
        GlStateManager.popMatrix();
    }

    protected void renderBaseModel(HPBaseTileEntity te, Tessellator tessellator, BufferBuilder buffer, double x, double y, double z) {
        // Most of this is blatantly copied from FastTESR
        setRenderSettings();

        BlockState blockState = te.getWorld().getBlockState(te.getPos());
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(blockState);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
        dispatcher.getBlockModelRenderer().renderModel(getWorld(), model, blockState, te.getPos(), buffer, false, getWorld().rand, blockState.getPositionRandom(te.getPos()));
        buffer.setTranslation(0, 0, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    protected void renderBaseModelWithFacing(HPBaseTileEntity te, BlockState blockState, Tessellator tessellator, BufferBuilder buffer, double x, double y, double z, int destroyStage) {
        // Most of this is blatantly copied from FastTESR
        preDestroyRender(destroyStage);
        setRenderSettings();

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(blockState);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        if (destroyStage >= 0) {
            buffer.noColor();
            renderBlockDamage(blockState, te.getPos(), destroyStage, te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, blockState, te.getPos(), buffer, false, getWorld().rand, blockState.getPositionRandom(te.getPos()));

        buffer.setTranslation(0, 0, 0);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        GlStateManager.translated(0.5, 0.5, 0.5);
        FacingToRotation.get(te.getForward()).glRotateCurrentMat();
        GlStateManager.translated(-0.5, -0.5, -0.5);

        tessellator.draw();
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        postDestroyRender(destroyStage);
        RenderHelper.enableStandardItemLighting();
    }

    protected void setRenderSettings() {
        this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
    }

    protected void preDestroyRender(int destroyStage) {
        if (destroyStage >= 0) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.enableBlend();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
            GlStateManager.polygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableAlphaTest();
            GlStateManager.pushMatrix();
        }
    }

    protected void postDestroyRender(int destroyStage) {
        if (destroyStage >= 0) {
            GlStateManager.disableAlphaTest();
            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlphaTest();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();

            Minecraft.getInstance().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            GlStateManager.disableBlend();
        }
    }

    protected void renderLeash(CreatureEntity entity, double ox, double oy, double oz, double x, double y, double z, float partialTicks, BlockPos pos) {
        if (entity != null) {
            oy = oy - 0.7D;
            double d2;
            double d3;
            double d4 = -1.0D;

            double d9 = (double) (MathHelper.lerp(partialTicks, entity.renderYawOffset, entity.prevRenderYawOffset) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
            d2 = Math.cos(d9) * (double) entity.getWidth() * 0.4D;
            d3 = Math.sin(d9) * (double) entity.getWidth() * 0.4D;
            double d6 = MathHelper.lerp(partialTicks, entity.prevPosX, entity.posX) + d2;
            double d7 = MathHelper.lerp(partialTicks, entity.prevPosY + entity.getEyeHeight() * 1.1D, entity.posY + entity.getEyeHeight() * 1.1D) - d4 * 0.5D - 0.25D - y;
            double d8 = MathHelper.lerp(partialTicks, entity.prevPosZ, entity.posZ) + d3;


            d2 = 0.5D;
            d3 = 0.5D;
            double d10 = pos.getX() + d2;
            double d11 = pos.getY();
            double d12 = pos.getZ() + d3;
            ox += d2 + x;
            oz += d3 + z;
            oy += y;

            renderLeach(d6, d7, d8, ox, oy, oz, d10, d11, d12);
        }
    }

    private void renderLeach(double x1, double y1, double z1, double ox, double oy, double oz, double x2, double y2, double z2) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        double d13 = (float) (x1 - x2);
        double d14 = (float) (y1 - y2);
        double d15 = (float) (z1 - z2);

        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (int j = 0; j <= 24; ++j) {
            float f = 0.5F;
            float f1 = 0.4F;
            float f2 = 0.3F;

            if (j % 2 == 0) {
                f *= 0.7F;
                f1 *= 0.7F;
                f2 *= 0.7F;
            }

            float f3 = (float) j / 24.0F;
            vertexbuffer.pos(ox + d13 * (double) f3 + 0.0D, oy + d14 * (double) (f3 * f3 + f3) * 0.5D + (double) ((24.0F - (float) j) / 18.0F + 0.125F), oz + d15 * (double) f3).color(f, f1, f2, 1.0F).endVertex();
            vertexbuffer.pos(ox + d13 * (double) f3 + 0.025D, oy + d14 * (double) (f3 * f3 + f3) * 0.5D + (double) ((24.0F - (float) j) / 18.0F + 0.125F) + 0.025D, oz + d15 * (double) f3).color(f, f1, f2, 1.0F).endVertex();
        }

        tessellator.draw();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (int k = 0; k <= 24; ++k) {
            float f4 = 0.5F;
            float f5 = 0.4F;
            float f6 = 0.3F;

            if (k % 2 == 0) {
                f4 *= 0.7F;
                f5 *= 0.7F;
                f6 *= 0.7F;
            }

            float f7 = (float) k / 24.0F;
            vertexbuffer.pos(ox + d13 * (double) f7 + 0.0D, oy + d14 * (double) (f7 * f7 + f7) * 0.5D + (double) ((24.0F - (float) k) / 18.0F + 0.125F) + 0.025D, oz + d15 * (double) f7).color(f4, f5, f6, 1.0F).endVertex();
            vertexbuffer.pos(ox + d13 * (double) f7 + 0.025D, oy + d14 * (double) (f7 * f7 + f7) * 0.5D + (double) ((24.0F - (float) k) / 18.0F + 0.125F), oz + d15 * (double) f7 + 0.025D).color(f4, f5, f6, 1.0F).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }

    public void renderBlockDamage(BlockState state, BlockPos pos, int destroyState, World world) {
        IBakedModel ibakedmodel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state);
        long i = state.getPositionRandom(pos);
        IBakedModel ibakedmodel1 = net.minecraftforge.client.ForgeHooksClient.getDamageModel(ibakedmodel, Minecraft.getInstance().worldRenderer.destroyBlockIcons[destroyState], state, world, pos, i);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ibakedmodel1, state, pos, Tessellator.getInstance().getBuffer(), true, world.rand, state.getPositionRandom(pos));
    }

    public void drawDisplayText(HPBaseTileEntity te, double x, double y, double z) {
        ITextComponent itextcomponent = te.getDisplayName();

        RayTraceResult raytraceresult = this.rendererDispatcher.cameraHitResult;
        if (itextcomponent != null && raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK && te.getPos().equals(((BlockRayTraceResult) raytraceresult).getPos())) {
            this.setLightmapDisabled(true);
            drawCustomNameplate(rendererDispatcher, getFontRenderer(), te, itextcomponent.getFormattedText(), x, y, z, 12, 0);
            drawCustomNameplate(rendererDispatcher, getFontRenderer(), te, LEAD_LOOKUP.getFormattedText(), x, y, z, 12, -0.25F);
            this.setLightmapDisabled(false);
        }
    }
}
