package se.gory_moon.horsepower.util.color;

/*
 * Java Color Thief
 * by Sven Woltmann, Fonpit AG
 *
 * http://www.androidpit.com
 * http://www.androidpit.de
 *
 * License
 * -------
 * Creative Commons Attribution 2.5 License:
 * http://creativecommons.org/licenses/by/2.5/
 *
 * Thanks
 * ------
 * Lokesh Dhakar - for the original Color Thief JavaScript version
 * available at http://lokeshdhakar.com/projects/color-thief/
 */

import net.minecraft.client.renderer.texture.NativeImage;

import javax.annotation.Nullable;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class ColorThief {

    /**
     * Use the median cut algorithm to cluster similar colors.
     *
     * @param sourceImage the source image
     * @param colorCount  the size of the palette; the number of colors returned
     * @param quality     0 is the highest quality settings. 10 is the default. There is
     *                    a trade-off between quality and speed. The bigger the number,
     *                    the faster the palette generation but the greater the
     *                    likelihood that colors will be missed.
     * @param ignoreWhite if <code>true</code>, white pixels are ignored
     * @return the palette as array of RGB arrays
     */
    @Nullable
    public static int[][] getPalette(NativeImage sourceImage, int colorCount, int quality, boolean ignoreWhite) {
        MMCQ.CMap cmap = getColorMap(sourceImage, colorCount, quality, ignoreWhite);
        if (cmap == null) {
            return null;
        }
        return cmap.palette();
    }

    /**
     * Use the median cut algorithm to cluster similar colors.
     *
     * @param sourceImage the source image
     * @param colorCount  the size of the palette; the number of colors returned
     * @param quality     0 is the highest quality settings. 10 is the default. There is
     *                    a trade-off between quality and speed. The bigger the number,
     *                    the faster the palette generation but the greater the
     *                    likelihood that colors will be missed.
     * @param ignoreWhite if <code>true</code>, white pixels are ignored
     * @return the color map
     */
    @Nullable
    public static MMCQ.CMap getColorMap(NativeImage sourceImage, int colorCount, int quality, boolean ignoreWhite) {
        if (sourceImage.getFormat() == NativeImage.PixelFormat.RGBA) {
            int[][] pixelArray = getPixels(sourceImage, quality, ignoreWhite);

            // Send array to quantize function which clusters values using median
            // cut algorithm
            return MMCQ.quantize(pixelArray, colorCount);
        }
        return null;
    }

    /**
     * Gets the image's pixels via NativeImage.getPixelRGBA(..).
     *
     * @param sourceImage the source image
     * @param quality     0 is the highest quality settings. 10 is the default. There is
     *                    a trade-off between quality and speed. The bigger the number,
     *                    the faster the palette generation but the greater the
     *                    likelihood that colors will be missed.
     * @param ignoreWhite if <code>true</code>, white pixels are ignored
     * @return an array of pixels (each an RGB int array)
     */
    private static int[][] getPixels(NativeImage sourceImage, int quality, boolean ignoreWhite) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int pixelCount = width * height;

        // numRegardedPixels must be rounded up to avoid an
        // ArrayIndexOutOfBoundsException if all pixels are good.
        int numRegardedPixels = (pixelCount + quality - 1) / quality;

        int numUsedPixels = 0;
        int[][] pixelArray = new int[numRegardedPixels][];
        int a, r, g, b;

        int i = 0;
        while (i < pixelCount) {
            int row = i / width;
            int col = i % width;
            int rgba = sourceImage.getPixelRGBA(col, row);
            a = (rgba >> 24) & 0xFF;
            b = (rgba >> 16) & 0xFF;
            g = (rgba >> 8) & 0xFF;
            r = (rgba) & 0xFF;

            // If pixel is mostly opaque and not white
            if (a > 125 && !(ignoreWhite && r > 250 && r > 250 && r > 250)) {
                pixelArray[numUsedPixels] = new int[] { r, g, b };
                numUsedPixels++;
                i += quality;
            } else {
                i++;
            }
        }

        return Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
    }

}