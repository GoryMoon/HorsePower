package se.gory_moon.horsepower.recipes;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
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

    public void reloadRecipes(ICommandSender sender) {
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
                } else if (stacks.size() == 2) {
                    try {
                        time = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        HorsePowerMod.logger.error("Parse error with grindstone time '" + item + "' in config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                if (stacks.get(0) instanceof List) {
                    for (Object stack: (List)stacks.get(0)) {
                        ItemStack in = ((ItemStack) stack);
                        in.setCount(1);
                        addGrindstoneRecipe(in, (ItemStack) stacks.get(1), time);
                    }
                } else {
                    ItemStack in = ((ItemStack) stacks.get(0));
                    in.setCount(1);
                    addGrindstoneRecipe(in, (ItemStack) stacks.get(1), time);
                }
            } else {
                String text = "Couldn't load grindstone recipe (" + Joiner.on("-").join(comp) + ")";
                HorsePowerMod.logger.warn(text);
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
                } else if (stacks.size() == 2){
                    try {
                        time = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        HorsePowerMod.logger.error("Parse error with chopping time '" + item + "' in config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                if (stacks.get(0) instanceof List) {
                    for (Object stack: (List)stacks.get(0)) {
                        ItemStack in = ((ItemStack) stack);
                        in.setCount(1);
                        addChoppingRecipe(in, (ItemStack) stacks.get(1), time);
                    }
                } else {
                    ItemStack in = ((ItemStack) stacks.get(0));
                    in.setCount(1);
                    addChoppingRecipe(in, (ItemStack) stacks.get(1), time);
                }
            } else {
                String text = "Couldn't load chopping recipe (" + Joiner.on("-").join(comp) + ")";
                HorsePowerMod.logger.warn(text);
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

    public void addChoppingRecipe(Block input, ItemStack output, int time) {
        addChoppingRecipe(Item.getItemFromBlock(input), output, time);
    }

    public void addChoppingRecipe(Item input, ItemStack output, int time) {
        addChoppingRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, time);
    }

    public void addChoppingRecipe(ItemStack input, ItemStack output, int time) {
        if (getChopperResult(input) != ItemStack.EMPTY) return;
        chopperRecipes.add(new ChopperRecipe(input, output, time));
    }

    public void addChoppingRecipe(ChopperRecipe recipe) {
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

    public void removeChoppingRecipe(ChopperRecipe recipe) {
        if (hasChopperRecipe(recipe.getInput())) {
            chopperRecipes.remove(recipe);
        }
    }

    public void removeChoppingRecipe(ItemStack input) {
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

    public ArrayList<ChopperRecipe> getChoppingRecipes() {
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

    public int getChoppingTime(ItemStack stack) {
        for (ChopperRecipe recipe : chopperRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getTime();
            }
        }

        return 1;
    }

}
