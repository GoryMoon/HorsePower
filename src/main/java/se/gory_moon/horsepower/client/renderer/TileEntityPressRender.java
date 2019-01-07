package se.gory_moon.horsepower.client.renderer;
/*
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.lwjgl.opengl.GL11;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.blocks.BlockPress;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.tileentity.TileEntityPress;
import se.gory_moon.horsepower.util.RenderUtils;

public class TileEntityPressRender extends TileEntityHPBaseRenderer<TileEntityPress> {

    @Override
    public void render(TileEntityPress te, double x, double y, double z, float partialTicks, int destroyStage) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBlockState blockState = te.getWorld().getBlockState( te.getPos() );
        if (!(blockState.getBlock() instanceof BlockHPBase)) return;
        IBlockState topState = blockState.with(BlockPress.PART, PressModels.TOP);
        if (!(topState.getBlock() instanceof BlockHPBase)) return;
        IBakedModel pressModel = dispatcher.getBlockModelShapes().getModel(topState);

        preDestroyRender(destroyStage);
        setRenderSettings();

        buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
        // This makes the translations that follow much easier
        buffer.setTranslation( -te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        if (destroyStage >= 0) {
            buffer.noColor();
            renderBlockDamage(topState, te.getPos(), getDestroyBlockIcon(destroyStage), te.getWorld());
        } else
            dispatcher.getBlockModelRenderer().renderModel( te.getWorld(), pressModel, blockState, te.getPos(), buffer, false, getWorld().rand, blockState.getPositionRandom(te.getPos()));

        buffer.setTranslation( 0, 0, 0 );

        GlStateManager.pushMatrix();
        GlStateManager.translated( x, y, z );

        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
        float move = (te.getField(0) / (float)(Configs.general.pointsForPress > 0 ? Configs.general.pointsForPress: 1));
        GlStateManager.translated( 0.5, 0.5, 0.5 );
        GlStateManager.translated( 0, -( 0.58 * move), 0 );
        GlStateManager.translated( -0.5, -0.5, -0.5 );

        tessellator.draw();
        GlStateManager.popMatrix();
        postDestroyRender(destroyStage);
        RenderHelper.enableStandardItemLighting();

        if (!(blockState.getBlock() instanceof BlockHPBase)) return;

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
            drawString(te, String.valueOf(te.getStackInSlot(1).getCount()), 0, 0.35,  0);
        }
        GlStateManager.popMatrix();


        IFluidTankProperties tankProperties = te.getTankFluidStack()[0];
        FluidStack stack = tankProperties.getContents();
        if (stack != null && move <= 0.25) {
            float amount = (0.75F / ((float) tankProperties.getCapacity())) * stack.amount;
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getAtlasSprite(stack.getFluid().getStill().toString());
            int fluidColor = stack.getFluid().getColor(stack);

            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.translated(x, y + 0.07, z);
            Minecraft.getInstance().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            float red = (fluidColor >> 16 & 0xFF) / 255.0F;
            float green = (fluidColor >> 8 & 0xFF) / 255.0F;
            float blue = (fluidColor & 0xFF) / 255.0F;
            GlStateManager.color4f(red, green, blue, 1.0F);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            float xMax = 0.9f;
            float zMax = 0.9f;
            float xMin = 0.1f;
            float zMin = 0.1f;
            double uMin = (double) sprite.getMinU();
            double uMax = (double) sprite.getMaxU();
            double vMin = (double) sprite.getMinV();
            double vMax = (double) sprite.getMaxV();

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

        if (!te.isValid())
            RenderUtils.renderInvalidArea(te.getWorld(), te.getPos(), 0);
        GlStateManager.popMatrix();
    }
}
*/