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

public class GrindstoneRecipes {

    private static GrindstoneRecipes INSTANCE = new GrindstoneRecipes();

    private final Map<ItemStack, ItemStack> grindstoneList = Maps.newHashMap();
    private final Map<ItemStack, Integer> grindstoneTimeList = Maps.newHashMap();

    public static GrindstoneRecipes instance() {
        return INSTANCE;
    }

    private GrindstoneRecipes() {
        addGrindstoneRecipe(Items.WHEAT, new ItemStack(Items.BREAD), 16);
    }

    public void reloadRecipes() {
        grindstoneList.clear();
        grindstoneTimeList.clear();

        for (int i = 0; i < Configs.grindstoneRecipes.length; i++) {
            String[] comp = Configs.grindstoneRecipes[i].split("-");
            List<ItemStack> stacks = Lists.newArrayList();
            int time = -1;
            for (String item: comp) {
                if (item.contains(":")) {
                    String[] data = item.split(":");
                    int meta = data.length == 2 ? 0 : "*".equals(data[2]) ?  OreDictionary.WILDCARD_VALUE: Integer.getInteger(data[2]);
                    Item item1 = Item.getByNameOrId(data[0] + ":" + data[1]);
                    if (item1 == null)
                        continue;
                    ItemStack stack = new ItemStack(item1, 1, meta);
                    stacks.add(stack);
                } else {
                    try {
                        time = Integer.valueOf(item);
                    } catch (NumberFormatException e) {
                        System.out.println("[HorsePower] Parse error with grindstone time '" + item + "' in config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                addGrindstoneRecipe(stacks.get(0), stacks.get(1), time);
            }
        }

        HorsePowerMod.tweakerPlugin.applyTweaker();
    }

    public void addGrindstoneRecipe(Block input, ItemStack stack, int time) {
        addGrindstoneRecipe(Item.getItemFromBlock(input), stack, time);
    }

    public void addGrindstoneRecipe(Item input, ItemStack stack, int time) {
        addGrindstoneRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), stack, time);
    }

    public void addGrindstoneRecipe(ItemStack input, ItemStack output, int time) {
        if (getGrindstoneResult(input) != ItemStack.EMPTY) return;
        grindstoneList.put(input, output);
        grindstoneTimeList.put(input, time);
    }

    public void removeGrindstoneRecipe(ItemStack input) {
        if (hasRecipe(input)) {
            for (ItemStack itemStack: grindstoneList.keySet()) {
                if (OreDictionary.itemMatches(itemStack, input, false)) {
                    grindstoneList.remove(input);
                    grindstoneTimeList.remove(input);
                }
            }
        }
    }

    public ItemStack getGrindstoneResult(ItemStack stack) {
        for (Map.Entry<ItemStack, ItemStack> entry : grindstoneList.entrySet()) {
            if (OreDictionary.itemMatches(entry.getKey(), stack, false)) {
                return entry.getValue();
            }
        }

        return ItemStack.EMPTY;
    }

    public boolean hasRecipe(ItemStack stack) {
        for (ItemStack itemStack: grindstoneList.keySet()) {
            if (OreDictionary.itemMatches(itemStack, stack, false)) {
                return true;
            }
        }
        return false;
    }

    public Map<ItemStack, ItemStack> getGrindstoneList()
    {
        return grindstoneList;
    }

    public Map<ItemStack, Integer> getGrindstoneTimeList() {
        return grindstoneTimeList;
    }

    public int getGrindstoneTime(ItemStack stack) {
        for (Map.Entry<ItemStack, Integer> entry : grindstoneTimeList.entrySet())
        {
            if (OreDictionary.itemMatches(entry.getKey(), stack, false))
            {
                return entry.getValue();
            }
        }

        return 16;
    }

}
