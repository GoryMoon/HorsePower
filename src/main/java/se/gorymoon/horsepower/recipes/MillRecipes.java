package se.gorymoon.horsepower.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gorymoon.horsepower.Configs;
import se.gorymoon.horsepower.HorsePowerMod;

import java.util.List;
import java.util.Map;

public class MillRecipes {

    private static MillRecipes INSTANCE = new MillRecipes();

    private final Map<ItemStack, ItemStack> millList = Maps.newHashMap();
    private final Map<ItemStack, Integer> millTimeList = Maps.newHashMap();

    public static MillRecipes instance() {
        return INSTANCE;
    }

    private MillRecipes() {
        addMillRecipe(Items.WHEAT, new ItemStack(Items.BREAD), 16);
    }

    public void reloadRecipes() {
        millList.clear();
        millTimeList.clear();

        for (int i = 0; i < Configs.millRecipes.length; i++) {
            String[] comp = Configs.millRecipes[i].split("-");
            List<ItemStack> stacks = Lists.newArrayList();
            int time = -1;
            for (String item: comp) {
                if (item.contains(":")) {
                    String[] data = item.split(":");
                    int meta = data.length == 2 ? 0 : "*".equals(data[2]) ?  OreDictionary.WILDCARD_VALUE: Integer.getInteger(data[2]);
                    ItemStack stack = new ItemStack(Item.getByNameOrId(data[0] + ":" + data[1]), 1, meta);
                    stacks.add(stack);
                } else {
                    try {
                        time = Integer.valueOf(item);
                    } catch (NumberFormatException e) {
                        System.out.println("[HorsePower] Parse error with mill time '" + item + "' in config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                addMillRecipe(stacks.get(0), stacks.get(1), time);
            }
        }

        HorsePowerMod.tweakerPlugin.applyTweaker();
    }

    public void addMillRecipe(Block input, ItemStack stack, int time) {
        addMillRecipe(Item.getItemFromBlock(input), stack, time);
    }

    public void addMillRecipe(Item input, ItemStack stack, int time) {
        addMillRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), stack, time);
    }

    public void addMillRecipe(ItemStack input, ItemStack output, int time) {
        if (getMillResult(input) != ItemStack.EMPTY) return;
        millList.put(input, output);
        millTimeList.put(input, time);
    }

    public void removeMillRecipe(ItemStack input) {
        if (hasRecipe(input)) {
            for (ItemStack itemStack: millList.keySet()) {
                if (OreDictionary.itemMatches(itemStack, input, false)) {
                    millList.remove(input);
                    millTimeList.remove(input);
                }
            }
        }
    }

    public ItemStack getMillResult(ItemStack stack) {
        for (Map.Entry<ItemStack, ItemStack> entry : millList.entrySet()) {
            if (OreDictionary.itemMatches(entry.getKey(), stack, false)) {
                return entry.getValue();
            }
        }

        return ItemStack.EMPTY;
    }

    public boolean hasRecipe(ItemStack stack) {
        for (ItemStack itemStack: millList.keySet()) {
            if (OreDictionary.itemMatches(itemStack, stack, false)) {
                return true;
            }
        }
        return false;
    }

    public Map<ItemStack, ItemStack> getMillList()
    {
        return millList;
    }

    public Map<ItemStack, Integer> getMillTimeList() {
        return millTimeList;
    }

    public int getMillTime(ItemStack stack) {
        for (Map.Entry<ItemStack, Integer> entry : millTimeList.entrySet())
        {
            if (OreDictionary.itemMatches(entry.getKey(), stack, false))
            {
                return entry.getValue();
            }
        }

        return 16;
    }

}
