package se.gory_moon.horsepower.recipes;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;

import java.util.ArrayList;
import java.util.List;

public class HPRecipes {

    private static HPRecipes INSTANCE = new HPRecipes();

    private final ArrayList<GrindstoneRecipe> grindstoneRecipes = Lists.newArrayList();
    private final ArrayList<ChopperRecipe> chopperRecipes = Lists.newArrayList();

    public static HPRecipes instance() {
        return INSTANCE;
    }

    private HPRecipes() {}

    public void reloadRecipes() {
        HorsePowerMod.jeiPlugin.removeRecipe();
        grindstoneRecipes.clear();
        chopperRecipes.clear();

        for (int i = 0; i < Configs.grindstoneRecipes.length; i++) {
            String[] comp = Configs.grindstoneRecipes[i].split("-");
            List<Object> stacks = Lists.newArrayList();
            int time = -1;
            for (String item: comp) {
                if (item.contains(":")) {
                    Object stack = parseItemStack(item);
                    if ((stack instanceof ItemStack && !((ItemStack) stack).isEmpty()) || (!(stack instanceof ItemStack) && stack != null))
                        stacks.add(stack);
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
        for (int i = 0; i < Configs.choppingRecipes.length; i++) {
            String[] comp = Configs.choppingRecipes[i].split("-");
            List<Object> stacks = Lists.newArrayList();
            int time = -1;
            for (String item: comp) {
                if (item.contains(":")) {
                    Object stack = parseItemStack(item);
                    if ((stack instanceof ItemStack && !((ItemStack) stack).isEmpty()) || (!(stack instanceof ItemStack) && stack != null))
                        stacks.add(stack);
                } else {
                    try {
                        time = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        System.out.println("[HorsePower] Parse error with chopping time '" + item + "' in config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                if (stacks.get(0) instanceof List) {
                    for (Object stack: (List)stacks.get(0)) {
                        addChopperRecipe((ItemStack) stack, (ItemStack) stacks.get(1), time);
                    }
                } else {
                    addChopperRecipe((ItemStack) stacks.get(0), (ItemStack) stacks.get(1), time);
                }
            }
        }
        HorsePowerMod.jeiPlugin.addRecipes();
        HorsePowerMod.tweakerPlugin.applyTweaker();
    }

    private Object parseItemStack(String item) {
        String[] data = item.split("@");
        int amount = data.length == 1 ? 1: Integer.parseInt(data[1]);
        if (data.length == 2) {
            item = item.substring(0, item.indexOf("@"));
        }
        data = item.split(":");
        int meta = data.length == 2 ? 0 : "*".equals(data[2]) ?  OreDictionary.WILDCARD_VALUE: Integer.parseInt(data[2]);
        if (item.startsWith("ore:")) {
            NonNullList<ItemStack> items = OreDictionary.getOres(item.substring(4));
            return items;
        } else {
            Item item1 = Item.getByNameOrId(data[0] + ":" + data[1]);
            if (item1 == null)
                return ItemStack.EMPTY;
            ItemStack stack = new ItemStack(item1, amount, meta);
            return stack;
        }
    }

    public void addGrindstoneRecipe(Block input, ItemStack output, int time) {
        addGrindstoneRecipe(Item.getItemFromBlock(input), output, time);
    }

    public void addGrindstoneRecipe(Item input, ItemStack output, int time) {
        addGrindstoneRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, time);
    }

    public void addGrindstoneRecipe(ItemStack input, ItemStack output, int time) {
        if (getGrindstoneResult(input) != ItemStack.EMPTY) return;
        grindstoneRecipes.add(new GrindstoneRecipe(input, output, time));
    }

    public void addGrindstoneRecipe(GrindstoneRecipe recipe) {
        if (getGrindstoneResult(recipe.getInput()) != ItemStack.EMPTY) return;
        grindstoneRecipes.add(recipe);
    }

    public void addChopperRecipe(Block input, ItemStack output, int time) {
        addChopperRecipe(Item.getItemFromBlock(input), output, time);
    }

    public void addChopperRecipe(Item input, ItemStack output, int time) {
        addChopperRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, time);
    }

    public void addChopperRecipe(ItemStack input, ItemStack output, int time) {
        if (getChopperResult(input) != ItemStack.EMPTY) return;
        chopperRecipes.add(new ChopperRecipe(input, output, time));
    }

    public void addChopperRecipe(ChopperRecipe recipe) {
        if (getChopperResult(recipe.getInput()) != ItemStack.EMPTY) return;
        chopperRecipes.add(recipe);
    }

    public void removeGrindstoneRecipe(GrindstoneRecipe recipe) {
        if (hasGrindstoneRecipe(recipe.getInput()))
            grindstoneRecipes.remove(recipe);
    }

    public void removeGrindstoneRecipe(ItemStack input) {
        if (hasGrindstoneRecipe(input)) {
            for (GrindstoneRecipe recipe: grindstoneRecipes) {
                if (OreDictionary.itemMatches(recipe.getInput(), input, false)) {
                    grindstoneRecipes.remove(recipe);
                }
            }
        }
    }

    public void removeChopperRecipe(ItemStack input) {
        if (hasChopperRecipe(input)) {
            for (ChopperRecipe recipe: chopperRecipes) {
                if (OreDictionary.itemMatches(recipe.getInput(), input, false)) {
                    chopperRecipes.remove(recipe);
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

    public ItemStack getChopperResult(ItemStack stack) {
        for (ChopperRecipe recipe : chopperRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getOutput();
            }
        }

        return ItemStack.EMPTY;
    }

    public boolean hasGrindstoneRecipe(ItemStack stack) {
        for (GrindstoneRecipe recipe: grindstoneRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChopperRecipe(ItemStack stack) {
        for (ChopperRecipe recipe: chopperRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<GrindstoneRecipe> getGrindstoneRecipes() {
        return grindstoneRecipes;
    }

    public ArrayList<ChopperRecipe> getChopperRecipes() {
        return chopperRecipes;
    }

    public int getGrindstoneTime(ItemStack stack) {
        for (GrindstoneRecipe recipe : grindstoneRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getTime();
            }
        }

        return 16;
    }

    public int getChopperTime(ItemStack stack) {
        for (ChopperRecipe recipe : chopperRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getTime();
            }
        }

        return 16;
    }

}
