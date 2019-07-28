package se.gory_moon.horsepower.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInventoryHP extends IInventory {

    void setSlotContent(int index, ItemStack stack);

}
