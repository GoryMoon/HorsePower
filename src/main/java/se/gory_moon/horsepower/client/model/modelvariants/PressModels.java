package se.gory_moon.horsepower.client.model.modelvariants;

import net.minecraft.util.IStringSerializable;

public enum PressModels implements IStringSerializable {
    BASE,
    TOP;

    @Override
    public String getString() {
        return name().toLowerCase();
    }
}
