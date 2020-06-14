package se.gory_moon.horsepower.client.renderer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.HPBaseBlock;
import se.gory_moon.horsepower.blocks.PressBlock;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.RenderUtils;

public class TileEntityPressRender extends TileEntityHPBaseRenderer<PressTileEntity> {

    @Override
    public void render(PressTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        BlockState blockState = te.getWorld().getBlockState(te.getPos());
        if (!(blockState.getBlock() instanceof HPBaseBlock))
            return;
        BlockState topState = blockState.with(PressBlock.PART, PressModels.TOP);
        if (!(topState.getBlock() instanceof HPBaseBlock))
            return;
        IBakedModel pressModel = dispatcher.getBlockModelShapes().getModel(topState);

        preDestroyRender(destroyStage);
        setRenderSettings();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
        // This makes the translations that follow much easier
        buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        if (destroyStage >= 0) {
            buffer.noColor();
            renderBlockDamage(topState, te.getPos(), destroyStage, te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), pressModel, blockState, te.getPos(), buffer, false, getWorld().rand, blockState.getPositionRandom(te.getPos()));

        buffer.setTranslation(0, 0, 0);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        float move = (te.getCurrentPressStatus() / (float) (Configs.SERVER.pointsPerPress.get() > 0 ? Configs.SERVER.pointsPerPress.get(): 1));
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.translated(0, -(0.58 * move), 0);
        GlStateManager.translated(-0.5, -0.5, -0.5);

        tessellator.draw();
        GlStateManager.popMatrix();
        postDestroyRender(destroyStage);
        RenderHelper.enableStandardItemLighting();

        if (!(blockState.getBlock() instanceof HPBaseBlock))
            return;

        if (te.hasWorker())
            renderLeash(te.getWorker(), x, y, z, 0D, 0.4D, 0D, partialTicks, te.getPos());

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        if (!te.getStackInSlot(0).isEmpty() && move <= 0.25) {
            renderItem(te, te.getStackInSlot(0), 0.5F, 0.5F, 0.5F, 1F);
            drawString(te, String.valueOf(te.getStackInSlot(0).getCount()), 0, 0.35, 0);
        }

        if (!te.getStackInSlot(1).isEmpty() && move <= 0.25) {
            renderItem(te, te.getStackInSlot(1), 0.5F, 0.5F, 0.5F, 1F);
            drawString(te, String.valueOf(te.getStackInSlot(1).getCount()), 0, 0.35, 0);
        }
        GlStateManager.popMatrix();


        FluidTank tank = te.getTank();
        FluidStack stack = tank.getFluid();
        if (!stack.isEmpty() && move <= 0.25) {
            float amount = (0.75F / tank.getCapacity()) * stack.getAmount();
            FluidAttributes attributes = stack.getFluid().getAttributes();
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(attributes.getStillTexture());
            int fluidColor = attributes.getColor(stack);


            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.translated(x, y + 0.07, z);
            Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            float red = (fluidColor >> 16 & 0xFF) / 255.0F;
            float green = (fluidColor >> 8 & 0xFF) / 255.0F;
            float blue = (fluidColor & 0xFF) / 255.0F;
            GlStateManager.color4f(red, green, blue, 1.0F);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            float xMax = 0.9f;
            float zMax = 0.9f;
            float xMin = 0.1f;
            float zMin = 0.1f;
            double uMin = sprite.getMinU();
            double uMax = sprite.getMaxU();
            double vMin = sprite.getMinV();
            double vMax = sprite.getMaxV();

            buffer.pos(xMax, amount, zMax).tex(uMax, vMin).endVertex();
            buffer.pos(xMax, amount, zMin).tex(uMin, vMax).endVertex();
            buffer.pos(xMin, amount, zMin).tex(uMin, vMax).endVertex();
            buffer.pos(xMin, amount, zMax).tex(uMax, vMin).endVertex();

            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.enableLighting();

        }

        GlStateManager.pushMatrix();
        drawDisplayText(te, x, y + 1, z);

        if (te.isInvalid())
            RenderUtils.renderInvalidArea(te.getWorld(), te.getPos(), 0);
        GlStateManager.popMatrix();
    }
}
