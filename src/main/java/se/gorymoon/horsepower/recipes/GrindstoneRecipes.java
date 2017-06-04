package se.gorymoon.horsepower.recipes;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import se.gorymoon.horsepower.Configs;
import se.gorymoon.horsepower.HorsePowerMod;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneRecipes {

    private static GrindstoneRecipes INSTANCE = new GrindstoneRecipes();

    private final ArrayList<GrindstoneRecipe> grindstoneRecipes = Lists.newArrayList();

    public static GrindstoneRecipes instance() {
        return INSTANCE;
    }

    private GrindstoneRecipes() {}

    public void reloadRecipes() {
        HorsePowerMod.jeiPlugin.removeRecipe();
        grindstoneRecipes.clear();

        for (int i = 0; i < Configs.grindstoneRecipes.length; i++) {
            String[] comp = Configs.grindstoneRecipes[i].split("-");
            List<Object> stacks = Lists.newArrayList();
            int time = -1;
            for (String item: comp) {
                if (item.contains(":")) {
                    String[] data = item.split("@");
                    int amount = data.length == 1 ? 1: Integer.parseInt(data[1]);
                    if (data.length == 2) {
                        item = item.substring(0, item.indexOf("@"));
                    }
                    data = item.split(":");
                    int meta = data.length == 2 ? 0 : "*".equals(data[2]) ?  OreDictionary.WILDCARD_VALUE: Integer.parseInt(data[2]);
                    if (item.startsWith("ore:")) {
                        NonNullList<ItemStack> items = OreDictionary.getOres(item.substring(4));
                        stacks.add(items);
                    } else {
                        Item item1 = Item.getByNameOrId(data[0] + ":" + data[1]);
                        if (item1 == null)
                            continue;
                        ItemStack stack = new ItemStack(item1, amount, meta);
                        stacks.add(stack);
                    }
                } else {
                    try {
                        time = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        System.out.println("[HorsePower] Parse error with grindstone time '" + item + "' in config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                if (stacks.get(0) instanceof List) {
                    for (Object stack: (List)stacks.get(0)) {
                        addGrindstoneRecipe((ItemStack) stack, (ItemStack) stacks.get(1), time);
                    }
                } else {
                    addGrindstoneRecipe((ItemStack) stacks.get(0), (ItemStack) stacks.get(1), time);
                }
            }
        }
        HorsePowerMod.jeiPlugin.addRecipes();
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
        grindstoneRecipes.add(new GrindstoneRecipe(input, output, time));
    }

    public void addGrindstoneRecipe(GrindstoneRecipe recipe) {
        if (getGrindstoneResult(recipe.getInput()) != ItemStack.EMPTY) return;
        grindstoneRecipes.add(recipe);
    }

    public void removeGrindstoneRecipe(GrindstoneRecipe recipe) {
        if (hasRecipe(recipe.getInput()))
            grindstoneRecipes.remove(recipe);
    }

    public void removeGrindstoneRecipe(ItemStack input) {
        if (hasRecipe(input)) {
            for (GrindstoneRecipe recipe: grindstoneRecipes) {
                if (OreDictionary.itemMatches(recipe.getInput(), input, false)) {
                    grindstoneRecipes.remove(recipe);
                }
            }
        }
    }

    public ItemStack getGrindstoneResult(ItemStack stack) {
        for (GrindstoneRecipe recipe : grindstoneRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getOutput();
            }
        }

        return ItemStack.EMPTY;
    }

    public boolean hasRecipe(ItemStack stack) {
        for (GrindstoneRecipe recipe: grindstoneRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<GrindstoneRecipe> getGrindstoneRecipes() {
        return grindstoneRecipes;
    }

    public int getGrindstoneTime(ItemStack stack) {
        for (GrindstoneRecipe recipe : grindstoneRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getTime();
            }
        }

        return 16;
    }

}
