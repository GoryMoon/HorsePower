package se.gorymoon.horsepower.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gorymoon.horsepower.tileentity.TileEntityGrindstone;

public class TileEntityGrindstoneRender extends TileEntitySpecialRenderer<TileEntityGrindstone> {

    @Override
    public void renderTileEntityAt(TileEntityGrindstone te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.hasWorker())
            renderLeash(te.getWorker(), x, y, z, 0, partialTicks, te.getPos());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderItem(te.getWorld(), te.getStackInSlot(0), partialTicks);
        GlStateManager.popMatrix();

        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
    }


    private void renderItem(World world, ItemStack stack, float partialTicks)
    {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        if (stack != null) {
            GlStateManager.translate(0.5, 1, 0.5);
            EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, stack);
            entityitem.getEntityItem().setCount(1);
            entityitem.hoverStart = 0.0F;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();

            float rotation = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

            GlStateManager.rotate(rotation, 0.0F, 1.0F, 0);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(entityitem.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Gets the value between start and end according to pct
     */
    private double interpolateValue(double start, double end, double pct)
    {
        return start + (end - start) * pct;
    }

    protected void renderLeash(AbstractHorse entity, double x, double y, double z, float entityYaw, float partialTicks, BlockPos pos) {
        if (entity != null)
        {
            y = y - (1.6D - 0.2D) * 0.5D;
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            double d2 = 0.0D;
            double d3 = 0.0D;
            double d4 = -1.0D;

            double d9 = this.interpolateValue(entity.prevRenderYawOffset, entity.renderYawOffset, (double)partialTicks) * 0.01745329238474369D + (Math.PI / 2D);
            d2 = Math.cos(d9) * (double)entity.width * 0.4D;
            d3 = Math.sin(d9) * (double)entity.width * 0.4D;
            double d6 = (this.interpolateValue(entity.prevPosX, entity.posX, (double)partialTicks)) + d2;
            double d7 = this.interpolateValue(entity.prevPosY + entity.getEyeHeight() * 1.1D, entity.posY + entity.getEyeHeight() * 1.1D, (double)partialTicks) - d4 * 0.5D - 0.25D;
            double d8 = (this.interpolateValue(entity.prevPosZ, entity.posZ, (double)partialTicks)) + d3;
            d2 = 0.5D;
            d3 = 0.5D;
            double d10 = pos.getX() + d2;
            double d11 = pos.getY();
            double d12 = pos.getZ() + d3;
            x = x + d2;
            z = z + d3;
            double d13 = (double)((float)(d6 - d10));
            double d14 = (double)((float)(d7 - d11));
            double d15 = (double)((float)(d8 - d12));
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
                vertexbuffer.pos(x + d13 * (double)f3 + 0.0D, y + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F), z + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
                vertexbuffer.pos(x + d13 * (double)f3 + 0.025D, y + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F) + 0.025D, z + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
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
                vertexbuffer.pos(x + d13 * (double)f7 + 0.0D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F) + 0.025D, z + d15 * (double)f7).color(f4, f5, f6, 1.0F).endVertex();
                vertexbuffer.pos(x + d13 * (double)f7 + 0.025D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F), z + d15 * (double)f7 + 0.025D).color(f4, f5, f6, 1.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
        }
    }
}
