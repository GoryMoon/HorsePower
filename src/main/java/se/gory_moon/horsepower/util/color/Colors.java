package se.gory_moon.horsepower.util.color;


public enum Colors {
    BLACK(0),
    BLUE(1),
    GREEN(2),
    CYAN(3),
    RED(4),
    PURPLE(5),
    ORANGE(6),
    LIGHTGRAY(7),
    GRAY(8),
    LIGHTBLUE(9),
    LIME(10),
    TURQUISE(11),
    PINK(12),
    MAGNETA(13),
    YELLOW(14),
    WHITE(15);

    private int number;
    private int rgb;

    Colors(int number) {
        this.number = number;
        rgb = getRGB(number);
    }

    /**
     * Alpha, Red, Green, Blue<br>
     * Example: 0xffffaa00, 4294945280
     */
    public int getARGB() {
        return 0xff << 24 | rgb;
    }

    /**
     * Alpha, Red, Green, Blue<br>
     * Example: 0xffaa00ff, 4289331455
     */
    @SuppressWarnings("unused")
    public int getRGBA() {
        return rgb << 8 | 0xff;
    }

    /**
     * Red, Green, Blue<br>
     * Example: 0xffaa00, 16755200
     */
    @SuppressWarnings("unused")
    public int getRGB() {
        return rgb;
    }

    private int getRGB(int i) {

        int j = (i >> 3 & 1) * 85;
        int k = (i >> 2 & 1) * 170 + j;
        int l = (i >> 1 & 1) * 170 + j;
        int i1 = (i & 1) * 170 + j;

        if (i == 6) {
            k += 85;
        }

        return (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
    }

    /**
     * Number in enum table<br>
     * Example: 6
     */
    public int getNumber() {
        return number;
    }

    /**
     * Minecraft text colors<br>
     * Example: &#167;6
     *
     * @return The colors in minecraft text style
     */
    @Override
    public String toString() {
        return (char) 167 + Integer.toHexString(number);
    }
}
