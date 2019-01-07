package se.gory_moon.horsepower;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.blocks.ModBlocks;

public class HorsePowerCreativeTab extends ItemGroup {

    public HorsePowerCreativeTab() {
        super("horsepower");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE);
    }
}
