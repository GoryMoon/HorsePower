package se.gory_moon.horsepower.client.renderer;

import net.minecraft.util.IStringSerializable;

public enum HandGrindstoneModels implements IStringSerializable {
    BASE,
    CENTER;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
