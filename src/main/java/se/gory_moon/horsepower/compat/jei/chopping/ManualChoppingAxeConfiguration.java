package se.gory_moon.horsepower.compat.jei.chopping;

import net.minecraft.item.ItemStack;

public class ManualChoppingAxeConfiguration {

    ItemStack axeItem;
    Integer baseAmount;
    Integer additionalChance;

    public ManualChoppingAxeConfiguration(ItemStack stack, Integer base, Integer other) {
        this.axeItem = stack;
        this.baseAmount = base;
        this.additionalChance = other;
    }
    
    @Override
    public boolean equals(Object obj) {
        return axeItem.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return axeItem.hashCode();
    }
}
