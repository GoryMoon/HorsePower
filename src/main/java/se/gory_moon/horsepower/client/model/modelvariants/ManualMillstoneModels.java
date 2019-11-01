package se.gory_moon.horsepower.client.model.modelvariants;

import net.minecraft.util.IStringSerializable;

public enum ManualMillstoneModels implements IStringSerializable {
    BASE,
    CENTER;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
