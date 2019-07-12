package se.gory_moon.horsepower;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.blocks.ModBlocks;

public class HorsePowerItemGroup extends ItemGroup {

    public HorsePowerItemGroup() {
        super("horsepower");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.BLOCK_HAND_MILLSTONE.orElse(null));
    }
}
