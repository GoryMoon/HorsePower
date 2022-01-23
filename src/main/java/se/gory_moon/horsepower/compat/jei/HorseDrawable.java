package se.gory_moon.horsepower.compat.jei;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HorseDrawable implements IDrawableAnimated {

    private final IDrawableStatic horse1;
    private final IDrawableStatic horse2;
    private final IDrawableStatic horse3;
    private final IDrawableStatic horse4;

    private final ITickTimer animTimer;
    private final ITickTimer pathTimer;

    private final boolean grinding;
    private boolean reverse;
    private int location;
    private int x;
    private int y;
    private final String hovering;

    public HorseDrawable(IDrawableStatic horse1, IDrawableStatic horse2, IDrawableStatic horse3, IDrawableStatic horse4, ITickTimer animTimer, ITickTimer pathTimer, boolean grinding, String hovering) {
        this.horse1 = horse1;
        this.horse2 = horse2;
        this.horse3 = horse3;
        this.horse4 = horse4;
        this.grinding = grinding;
        this.animTimer = animTimer;
        this.pathTimer = pathTimer;
        this.hovering = hovering;
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
    public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
        reverse = false;
        location = pathTimer.getValue();
        setXYPos();

        IDrawableStatic draw;
        if (animTimer.getValue() == 0) {
            draw = reverse ? horse3: horse1;
        } else {
            draw = reverse ? horse4: horse2;
        }

        draw.draw(matrixStack, xOffset + x, yOffset + y, 0, 0, 0, 0);
    }

    private void setXYPos() {
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
    }

    private boolean isHovering(double mx, double my) {
        return mx >= x && mx <= x + horse1.getWidth() && my >= y && my <= y + horse1.getHeight();
    }

    public List<ITextComponent> getTooltipStrings(double mouseX, double mouseY) {
        setXYPos();
        return isHovering(mouseX, mouseY) && hovering != null ? Lists.newArrayList(Splitter.on('\n').split(hovering)).stream().map(StringTextComponent::new).collect(Collectors.toList()): new ArrayList<>();
    }
}
