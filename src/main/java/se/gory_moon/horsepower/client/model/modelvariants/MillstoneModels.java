package se.gory_moon.horsepower.client.model.modelvariants;

import net.minecraft.util.IStringSerializable;

public enum MillstoneModels implements IStringSerializable {
    BASE,
    FILLED;

    @Override
    public String getString() {
        return name().toLowerCase();
    }
}
