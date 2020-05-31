package se.gory_moon.horsepower.compat.jei.chopping;

import net.minecraft.item.ItemStack;

public class ManualChoppingAxeConfiguration {

    public ItemStack axeItem;
    public Integer baseChance;
    public Integer otherChance;

    public ManualChoppingAxeConfiguration(ItemStack stack, Integer base, Integer other) {
        this.axeItem = stack;
        this.baseChance = base;
        this.otherChance = other;
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
