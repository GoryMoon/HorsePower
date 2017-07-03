package se.gory_moon.horsepower.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;

import java.util.Arrays;

public abstract class TileEntityHPBaseRenderer<T extends TileEntityHPBase> extends TileEntitySpecialRenderer<T> {

    private static TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];

    protected void renderStillItem(TileEntityHPBase te, ItemStack stack, float x, float y, float z, float scale) {
        renderItem(te, stack, x, y, z, scale, false, false);
    }

    protected void renderItem(TileEntityHPBase te, ItemStack stack, float x, float y, float z, float scale) {
        renderItem(te, stack, x, y, z, scale, true, true);
    }

    private void renderItem(TileEntityHPBase te, ItemStack stack, float x, float y, float z, float scale, boolean rotate, boolean displayAmount) {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        if (stack != null) {
            GlStateManager.translate(x, y, z);
            EntityItem entityitem = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, stack.copy());
            entityitem.getEntityItem().setCount(1);
            entityitem.hoverStart = 0.0F;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            float rotation = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

            if (rotate)
                GlStateManager.rotate(rotation, 0.0F, 1.0F, 0);
            GlStateManager.scale(0.5F * scale, 0.5F * scale, 0.5F * scale);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(entityitem.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public void drawString(TileEntityHPBase te, String str, double x, double y, double z) {
        if (!canShowAmount(te))
            return;
        setLightmapDisabled(true);
        Entity entity = this.rendererDispatcher.entity;
        double d0 = te.getDistanceSq(entity.posX, entity.posY, entity.posZ);

        if (d0 <= (double)(14 * 14)) {
            float f = this.rendererDispatcher.entityYaw;
            float f1 = this.rendererDispatcher.entityPitch;
            FontRenderer fontRenderer = getFontRenderer();
            GlStateManager.pushMatrix();

            GlStateManager.translate(x, y, z);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            if (te.getForward() == EnumFacing.EAST || te.getForward() == EnumFacing.WEST)
                FacingToRotation.get( te.getForward().getOpposite()).glRotateCurrentMat();
            else
                FacingToRotation.get( te.getForward()).glRotateCurrentMat();


            GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-0.015F, -0.015F, 0.015F);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();

            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, -1);

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
        setLightmapDisabled(false);
    }

    public boolean canShowAmount(TileEntityHPBase te) {
        return Configs.renderItemAmount && (!Configs.mustLookAtBlock || this.rendererDispatcher.cameraHitResult != null && te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos()));
    }

    protected void renderItemWithFacing(World world, TileEntityHPBase tile, ItemStack stack, double ox, double oy, double oz, float x, float y, float z, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(ox, oy, oz);
        GlStateManager.translate( 0.5, 0.5, 0.5 );
        FacingToRotation.get( tile.getForward()).glRotateCurrentMat();
        GlStateManager.translate( -0.5, -0.5, -0.5 );
        renderItem(tile, stack, x, y, z, scale);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(ox , oy, oz);
        GlStateManager.translate( 0.5, 0.5, 0.5 );
        FacingToRotation.get( tile.getForward()).glRotateCurrentMat();
        GlStateManager.translate( -0.5, -0.5, -0.5 );

        if (!stack.isEmpty())
            drawString(tile, String.valueOf(stack.getCount()), x, y + 0.3,  z);
        GlStateManager.popMatrix();
    }

    protected void renderBaseModel(TileEntityHPBase te, Tessellator tessellator, VertexBuffer buffer, double x, double y, double z) {
        // Most of this is blatantly copied from FastTESR
        setRenderSettings();

        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState( blockState );

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );
        dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), model, blockState, te.getPos(), buffer, false );
        buffer.setTranslation( 0, 0, 0 );
        GlStateManager.pushMatrix();
        GlStateManager.translate( x, y, z );
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    protected void renderBaseModelWithFacing(TileEntityHPBase te, IBlockState blockState, Tessellator tessellator, VertexBuffer buffer, double x, double y, double z, int destroyStage) {
        // Most of this is blatantly copied from FastTESR
        preDestroyRender(destroyStage);
        setRenderSettings();

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState( blockState );

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ() );

        if (destroyStage >= 0) {
            buffer.noColor();
            renderBlockDamage(blockState, te.getPos(), getDestroyBlockIcon(destroyStage), te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), model, blockState, te.getPos(), buffer, false );

        buffer.setTranslation( 0, 0, 0 );
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate( x, y, z );

        GlStateManager.translate( 0.5, 0.5, 0.5 );
        FacingToRotation.get( te.getForward()).glRotateCurrentMat();
        GlStateManager.translate( -0.5, -0.5, -0.5 );

        tessellator.draw();
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        postDestroyRender(destroyStage);
        RenderHelper.enableStandardItemLighting();
    }

    protected void setRenderSettings() {
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
    }

    protected void preDestroyRender(int destroyStage) {
        if (destroyStage >= 0) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
        }
    }

    protected void postDestroyRender(int destroyStage) {
        if (destroyStage >= 0) {

            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();

            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            GlStateManager.disableBlend();
        }
    }

    /**
     * Gets the value between start and end according to pct
     */
    private double interpolateValue(double start, double end, double pct)
    {
        return start + (end - start) * pct;
    }

    protected void renderLeash(EntityCreature entity, double ox, double oy, double oz, double x, double y, double z, float partialTicks, BlockPos pos) {
        if (entity != null) {
            oy = oy - 0.7D;
            double d2 = 0.0D;
            double d3 = 0.0D;
            double d4 = -1.0D;

            double d9 = this.interpolateValue(entity.prevRenderYawOffset, entity.renderYawOffset, (double)partialTicks) * 0.01745329238474369D + (Math.PI / 2D);
            d2 = Math.cos(d9) * (double)entity.width * 0.4D;
            d3 = Math.sin(d9) * (double)entity.width * 0.4D;
            double d6 = (this.interpolateValue(entity.prevPosX, entity.posX, (double)partialTicks)) + d2;
            double d7 = this.interpolateValue(entity.prevPosY + entity.getEyeHeight() * 1.1D, entity.posY + entity.getEyeHeight() * 1.1D, (double)partialTicks) - d4 * 0.5D - 0.25D - y;
            double d8 = (this.interpolateValue(entity.prevPosZ, entity.posZ, (double)partialTicks)) + d3;

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

    protected void renderLeach(double x1, double y1, double z1, double ox, double oy, double oz, double x2, double y2, double z2) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

        double d13 = (double)((float)(x1 - x2));
        double d14 = (double)((float)(y1 - y2));
        double d15 = (double)((float)(z1 - z2));

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (int j = 0; j <= 24; ++j)
        {
            float f = 0.5F;
            float f1 = 0.4F;
            float f2 = 0.3F;

            if (j % 2 == 0)
            {
                f *= 0.7F;
                f1 *= 0.7F;
                f2 *= 0.7F;
            }

            float f3 = (float)j / 24.0F;
            vertexbuffer.pos(ox + d13 * (double)f3 + 0.0D, oy + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F), oz + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
            vertexbuffer.pos(ox + d13 * (double)f3 + 0.025D, oy + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F) + 0.025D, oz + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
        }

        tessellator.draw();
        vertexbuffer.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (int k = 0; k <= 24; ++k)
        {
            float f4 = 0.5F;
            float f5 = 0.4F;
            float f6 = 0.3F;

            if (k % 2 == 0)
            {
                f4 *= 0.7F;
                f5 *= 0.7F;
                f6 *= 0.7F;
            }

            float f7 = (float)k / 24.0F;
            vertexbuffer.pos(ox + d13 * (double)f7 + 0.0D, oy + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F) + 0.025D, oz + d15 * (double)f7).color(f4, f5, f6, 1.0F).endVertex();
            vertexbuffer.pos(ox + d13 * (double)f7 + 0.025D, oy + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F), oz + d15 * (double)f7 + 0.025D).color(f4, f5, f6, 1.0F).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
    }

    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess)
    {
        state = state.getActualState(blockAccess, pos);
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
        IBakedModel ibakedmodel1 = net.minecraftforge.client.ForgeHooksClient.getDamageModel(ibakedmodel, texture, state, blockAccess, pos);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(blockAccess, ibakedmodel1, state, pos, Tessellator.getInstance().getBuffer(), true);
    }

    public static TextureAtlasSprite getDestroyBlockIcon(int destroyState) {
        if (destroyBlockIcons[destroyState] == null) {
            destroyBlockIcons = ObfuscationReflectionHelper.getPrivateValue(RenderGlobal.class, Minecraft.getMinecraft().renderGlobal, "destroyBlockIcons", "field_94141_F");
        }
        return destroyBlockIcons[destroyState];
    }

    public static void clearDestroyStageicons() {
        Arrays.stream(destroyBlockIcons).forEach(textureAtlasSprite -> textureAtlasSprite = null);
    }

}
