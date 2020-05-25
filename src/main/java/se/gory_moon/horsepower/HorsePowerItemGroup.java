package se.gory_moon.horsepower;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.util.Constants;

public class HorsePowerItemGroup extends ItemGroup {

    public HorsePowerItemGroup() {
        super(Constants.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Registration.MANUAL_MILLSTONE_BLOCK.get());
    }
}
