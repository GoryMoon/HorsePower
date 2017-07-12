package se.gory_moon.horsepower.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.ITickTimer;
import net.minecraft.client.Minecraft;

public class HorseDrawable implements IDrawableAnimated {

    private final IDrawableStatic horse1;
    private final IDrawableStatic horse2;
    private final IDrawableStatic horse3;
    private final IDrawableStatic horse4;

    private final ITickTimer animTimer;
    private final ITickTimer pathTimer;

    private final boolean grinding;

    public HorseDrawable(IGuiHelper guiHelper, IDrawableStatic horse1, IDrawableStatic horse2, IDrawableStatic horse3, IDrawableStatic horse4, boolean grinding) {
        this.horse1 = horse1;
        this.horse2 = horse2;
        this.horse3 = horse3;
        this.horse4 = horse4;
        this.grinding = grinding;

        animTimer = guiHelper.createTickTimer(20, 1, false);
        pathTimer = guiHelper.createTickTimer(100, grinding ? 352: 324, false);
    }

    @Override
    public int getWidth() {
        return 30;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void draw(Minecraft minecraft) {
        draw(minecraft, 0, 0);
    }

    @Override
    public void draw(Minecraft minecraft, int xOffset, int yOffset) {
        int x = 0;
        int y = 0;
        boolean reverse = false;
        int location = pathTimer.getValue();

        if (grinding) {
            if (location <= 112) {
                x = location;
                y = 0;
            } else if (location <= 176) {
                x = 112;
                y = location - 112;
                reverse = true;
            } else if (location <= 288) {
                x = 112 - (location - 174);
                y = 64;
                reverse = true;
            } else {
                x = 0;
                y = 64 - (location - 288);
            }
        } else {
            if (location <= 112) {
                x = location;
                y = 0;
            } else if (location <= 162) {
                x = 112;
                y = location - 112;
                reverse = true;
            } else if (location <= 274) {
                x = 112 - (location - 160);
                y = 50;
                reverse = true;
            } else {
                x = 0;
                y = 50 - (location - 274);
            }
        }

        IDrawableStatic draw;
        if (animTimer.getValue() == 0) {
            draw = reverse ? horse3: horse1;
        } else {
            draw = reverse ? horse4: horse2;
        }

        draw.draw(minecraft, xOffset + x, yOffset + y, 0, 0, 0, 0);
    }
}
