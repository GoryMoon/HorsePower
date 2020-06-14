package se.gory_moon.horsepower.util.color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;

/**
 * Copied and modified from JEI (JustEnoughItems)
 *
 * @author Mezz
 */
public final class ColorGetter {

    private static final Logger LOGGER = LogManager.getLogger();

    private ColorGetter() {}

    public static List<Integer> getColors(ItemStack itemStack, int colorCount) {
        try {
            return unsafeGetColors(itemStack, colorCount);
        } catch (RuntimeException | LinkageError e) {
            LOGGER.debug("Failed to get color for {}", itemStack);
            return Collections.emptyList();
        }
    }

    private static List<Integer> unsafeGetColors(ItemStack itemStack, int colorCount) {
        final Item item = itemStack.getItem();
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        } else if (item instanceof BlockItem) {
            final BlockItem itemBlock = (BlockItem) item;
            final Block block = itemBlock.getBlock();
            //noinspection ConstantConditions
            if (block == null) {
                return Collections.emptyList();
            }
            return getBlockColors(block, colorCount);
        } else {
            return getItemColors(itemStack, colorCount);
        }
    }

    private static List<Integer> getItemColors(ItemStack itemStack, int colorCount) {
        final ItemColors itemColors = Minecraft.getInstance().getItemColors();
        final int renderColor = itemColors.getColor(itemStack, 0);
        final TextureAtlasSprite textureAtlasSprite = getTextureAtlasSprite(itemStack);
        if (textureAtlasSprite == null) {
            return Collections.emptyList();
        }
        return getColors(textureAtlasSprite, renderColor, colorCount);
    }

    private static List<Integer> getBlockColors(Block block, int colorCount) {
        BlockState blockState = block.getDefaultState();
        final BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        final int renderColor = blockColors.getColor(blockState, null, null, 0);
        final TextureAtlasSprite textureAtlasSprite = getTextureAtlasSprite(blockState);
        if (textureAtlasSprite == null) {
            return Collections.emptyList();
        }
        return getColors(textureAtlasSprite, renderColor, colorCount);
    }

    public static List<Integer> getColors(TextureAtlasSprite textureAtlasSprite, int renderColor, int colorCount) {
        final NativeImage nativeImage = getNativeImage(textureAtlasSprite);
        if (nativeImage == null) {
            return Collections.emptyList();
        }
        final List<Integer> colors = new ArrayList<>(colorCount);
        final int[][] palette = ColorThief.getPalette(nativeImage, colorCount, 2, false);
        if (palette != null) {
            for (int[] colorInt : palette) {
                int red = (int) ((colorInt[0] - 1) * (float) (renderColor >> 16 & 255) / 255.0F);
                int green = (int) ((colorInt[1] - 1) * (float) (renderColor >> 8 & 255) / 255.0F);
                int blue = (int) ((colorInt[2] - 1) * (float) (renderColor & 255) / 255.0F);
                red = MathHelper.clamp(red, 0, 255);
                green = MathHelper.clamp(green, 0, 255);
                blue = MathHelper.clamp(blue, 0, 255);
                int color = ((0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF));
                colors.add(color);
            }
        }
        return colors;
    }

    @Nullable
    private static NativeImage getNativeImage(TextureAtlasSprite textureAtlasSprite) {
        final int iconWidth = textureAtlasSprite.getWidth();
        final int iconHeight = textureAtlasSprite.getHeight();
        final int frameCount = textureAtlasSprite.getFrameCount();
        if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
            return null;
        }
        NativeImage[] frames = textureAtlasSprite.frames;
        return frames[0];
    }

    @Nullable
    private static TextureAtlasSprite getTextureAtlasSprite(BlockState blockState) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
        BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
        TextureAtlasSprite textureAtlasSprite = blockModelShapes.getTexture(blockState);
        if (textureAtlasSprite instanceof MissingTextureSprite) {
            return null;
        }
        return textureAtlasSprite;
    }

    @Nullable
    private static TextureAtlasSprite getTextureAtlasSprite(ItemStack itemStack) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemModelMesher itemModelMesher = itemRenderer.getItemModelMesher();
        IBakedModel itemModel = itemModelMesher.getItemModel(itemStack);
        TextureAtlasSprite particleTexture = itemModel.getParticleTexture(EmptyModelData.INSTANCE);
        if (particleTexture instanceof MissingTextureSprite) {
            return null;
        }
        return particleTexture;
    }
}
