package se.gory_moon.horsepower.client.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;

public class LeadRendererHelper {

    protected static <T extends MobEntity> int getBlockLight(T entity, BlockPos pos) {
        return entity.isBurning() ? 15: entity.world.getLightFor(LightType.BLOCK, pos);
    }

    /*
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

     */
    public static void renderLeash(CreatureEntity entity, Vector3d sourcePos, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
        if (entity == null)
            return;

        matrixStackIn.push();
        double d0 = (double) (MathHelper.lerp(partialTicks, entity.renderYawOffset, entity.prevRenderYawOffset) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
        Vector3d startPos = entity.getLeashStartPosition();
        double d1 = Math.cos(d0) * startPos.z + Math.sin(d0) * startPos.x;
        double d2 = Math.sin(d0) * startPos.z - Math.cos(d0) * startPos.x;
        double d3 = MathHelper.lerp(partialTicks, entity.prevPosX, entity.getPosX()) + d1;
        double d4 = MathHelper.lerp(partialTicks, entity.prevPosY, entity.getPosY()) + startPos.y;
        double d5 = MathHelper.lerp(partialTicks, entity.prevPosZ, entity.getPosZ()) + d2;
        matrixStackIn.translate(0.5, startPos.y - 1, 0.5);

        float f = (float) (d3 - sourcePos.x);
        float f1 = (float) (d4 - sourcePos.y);
        float f2 = (float) (d5 - sourcePos.z);

        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLeash());
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;

        BlockPos entityBlockPos = new BlockPos(entity.getEyePosition(partialTicks));
        BlockPos sourceBlockPos = new BlockPos(sourcePos);
        int entityBlockLight = getBlockLight(entity, entityBlockPos);
        int sourceBlockLight = entity.world.getLightFor(LightType.BLOCK, sourceBlockPos);
        int entitySkyLight = entity.world.getLightFor(LightType.SKY, entityBlockPos);
        int sourceSkyLight = entity.world.getLightFor(LightType.SKY, sourceBlockPos);
        MobRenderer.renderSide(ivertexbuilder, matrix4f, f, f1, f2, entityBlockLight, sourceBlockLight, entitySkyLight, sourceSkyLight, 0.025F, 0.025F, f5, f6);
        MobRenderer.renderSide(ivertexbuilder, matrix4f, f, f1, f2, entityBlockLight, sourceBlockLight, entitySkyLight, sourceSkyLight, 0.025F, 0.0F, f5, f6);
        matrixStackIn.pop();
    }

/*
    protected void renderLeach(double x1, double y1, double z1, double ox, double oy, double oz, double x2, double y2, double z2) {
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
*/

}
