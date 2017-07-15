package se.gory_moon.horsepower.blocks.property;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyUnlistedDirection implements IUnlistedProperty<EnumFacing> {

    private final String name;

    public PropertyUnlistedDirection(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(EnumFacing value) {
        return value != null;
    }

    @Override
    public Class<EnumFacing> getType() {
        return EnumFacing.class;
    }

    @Override
    public String valueToString(EnumFacing value) {
        return value.getName();
    }
}
