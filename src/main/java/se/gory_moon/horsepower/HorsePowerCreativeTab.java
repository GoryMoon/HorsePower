package se.gory_moon.horsepower;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.blocks.ModBlocks;

public class HorsePowerCreativeTab extends CreativeTabs {

    public HorsePowerCreativeTab() {
        super("horsepower");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.BLOCK_HAND_GRINSTONE);
    }
}
