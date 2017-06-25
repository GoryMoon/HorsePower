package se.gory_moon.horsepower.util.color;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copied and modified from JEI (JustEnoughItems)
 * @author Mezz
 */
@SideOnly(Side.CLIENT)
public final class ColorGetter {

    private ColorGetter() {

    }

    public static List<Color> getColors(ItemStack itemStack, int colorCount) {
        try {
            return unsafeGetColors(itemStack, colorCount);
        } catch (RuntimeException ignored) {
            return Collections.emptyList();
        } catch (LinkageError ignored) {
            return Collections.emptyList();
        }
    }

    private static List<Color> unsafeGetColors(ItemStack itemStack, int colorCount) {
        final Item item = itemStack.getItem();
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        } else if (item instanceof ItemBlock) {
            final ItemBlock itemBlock = (ItemBlock) item;
            final Block block = itemBlock.getBlock();
            //noinspection ConstantConditions
            if (block == null) {
                return Collections.emptyList();
            }
            return getBlockColors(itemStack, block, colorCount);
        } else {
            return getItemColors(itemStack, colorCount);
        }
    }

    private static List<Color> getItemColors(ItemStack itemStack, int colorCount) {
        final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
        final int renderColor = itemColors.getColorFromItemstack(itemStack, 0);
        final TextureAtlasSprite textureAtlasSprite = getTextureAtlasSprite(itemStack);
        if (textureAtlasSprite == null) {
            return Collections.emptyList();
        }
        return getColors(textureAtlasSprite, renderColor, colorCount);
    }

    private static List<Color> getBlockColors(ItemStack itemStack, Block block, int colorCount) {
        final int meta = itemStack.getMetadata();
        IBlockState blockState;
        try {
            blockState = block.getStateFromMeta(meta);
        } catch (RuntimeException ignored) {
            blockState = block.getDefaultState();
        } catch (LinkageError ignored) {
            blockState = block.getDefaultState();
        }

        final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        final int renderColor = blockColors.colorMultiplier(blockState, null, null, 0);
        final TextureAtlasSprite textureAtlasSprite = getTextureAtlasSprite(blockState);
        if (textureAtlasSprite == null) {
            return Collections.emptyList();
        }
        return getColors(textureAtlasSprite, renderColor, colorCount);
    }

    public static List<Color> getColors(TextureAtlasSprite textureAtlasSprite, int renderColor, int colorCount) {
        final BufferedImage bufferedImage = getBufferedImage(textureAtlasSprite);
        if (bufferedImage == null) {
            return Collections.emptyList();
        }
        final List<Color> colors = new ArrayList<Color>(colorCount);
        final int[][] palette = ColorThief.getPalette(bufferedImage, colorCount);
        if (palette != null) {
            for (int[] colorInt : palette) {
                int red = (int) ((colorInt[0] - 1) * (float) (renderColor >> 16 & 255) / 255.0F);
                int green = (int) ((colorInt[1] - 1) * (float) (renderColor >> 8 & 255) / 255.0F);
                int blue = (int) ((colorInt[2] - 1) * (float) (renderColor & 255) / 255.0F);
                red = MathHelper.clamp(red, 0 ,255);
                green = MathHelper.clamp(green, 0, 255);
                blue = MathHelper.clamp(blue, 0, 255);
                Color color = new Color(red, green, blue);
                colors.add(color);
            }
        }
        return colors;
    }

    @Nullable
    private static BufferedImage getBufferedImage(TextureAtlasSprite textureAtlasSprite) {
        final int iconWidth = textureAtlasSprite.getIconWidth();
        final int iconHeight = textureAtlasSprite.getIconHeight();
        final int frameCount = textureAtlasSprite.getFrameCount();
        if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight * frameCount, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < frameCount; i++) {
            int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
            int[] largestMipMapTextureData = frameTextureData[0];
            bufferedImage.setRGB(0, i * iconHeight, iconWidth, iconHeight, largestMipMapTextureData, 0, iconWidth);
        }

        return bufferedImage;
    }

    @Nullable
    private static TextureAtlasSprite getTextureAtlasSprite(IBlockState blockState) {
        Minecraft minecraft = Minecraft.getMinecraft();
        BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
        BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
        TextureAtlasSprite textureAtlasSprite = blockModelShapes.getTexture(blockState);
        if (textureAtlasSprite == minecraft.getTextureMapBlocks().getMissingSprite()) {
            return null;
        }
        return textureAtlasSprite;
    }

    @Nullable
    private static TextureAtlasSprite getTextureAtlasSprite(ItemStack itemStack) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        ItemModelMesher itemModelMesher = renderItem.getItemModelMesher();
        IBakedModel itemModel = itemModelMesher.getItemModel(itemStack);
        return itemModel.getParticleTexture();
    }
}
