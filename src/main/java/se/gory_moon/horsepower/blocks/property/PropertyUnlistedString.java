package se.gory_moon.horsepower.blocks.property;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyUnlistedString implements IUnlistedProperty<String> {

    private final String name;

    public PropertyUnlistedString(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(String value) {
        return !value.isEmpty();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }
}
