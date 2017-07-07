package se.gory_moon.horsepower.recipes;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class HPRecipes {

    private static HPRecipes INSTANCE = new HPRecipes();

    private final ArrayList<GrindstoneRecipe> grindstoneRecipes = Lists.newArrayList();
    private final ArrayList<ChoppingBlockRecipe> choppingBlockRecipes = Lists.newArrayList();
    public static ArrayList<String> ERRORS = Lists.newArrayList();

    public static HPRecipes instance() {
        return INSTANCE;
    }

    private HPRecipes() {}

    public void reloadRecipes() {
        HorsePowerMod.jeiPlugin.removeRecipe();
        grindstoneRecipes.clear();
        choppingBlockRecipes.clear();

        //TODO sync on server world load
        //TODO sync changes from OP to server
        createRecipes(GrindstoneRecipe.class, Configs.grindstoneRecipes).forEach(this::addGrindstoneRecipe);
        createRecipes(ChoppingBlockRecipe.class, Configs.choppingRecipes).forEach(this::addChoppingRecipe);

        HorsePowerMod.jeiPlugin.addRecipes();
        HorsePowerMod.tweakerPlugin.applyTweaker();
    }

    private <T extends HPRecipeBase> List<T> createRecipes(Class<T> clazz, String[] data) {
        List<T> recipes = new ArrayList<>();
        for (String aData : data) {
            String[] comp = aData.split("-");
            if (aData.isEmpty()) continue;

            List<Object> stacks = Lists.newArrayList();
            int time = -1;
            for (String item : comp) {
                if (item.contains(":")) {
                    Object stack;
                    try {
                        stack = parseItemStack(item);
                    } catch (Exception e) {
                        errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe item '" + item + "' from config" + (stacks.size() > 0 ? " with item" + stacks.get(0): "") + ".");
                        break;
                    }
                    if ((stack instanceof ItemStack && !((ItemStack) stack).isEmpty()) || (!(stack instanceof ItemStack) && stack != null))
                        stacks.add(stack);
                } else if (stacks.size() == 2) {
                    try {
                        time = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe time '" + item + "' from config for input " + stacks.get(0) + " and output " + stacks.get(1) + ".");
                        time = -1;
                    }
                }
            }
            if (stacks.size() == 2 && time > -1) {
                try {
                    if (stacks.get(0) instanceof List) {
                        for (Object stack : (List) stacks.get(0)) {
                            ItemStack in = ((ItemStack) stack);
                            in.setCount(1);
                            recipes.add(clazz.getConstructor(ItemStack.class, ItemStack.class, int.class).newInstance(in, stacks.get(1), time));
                        }
                    } else {
                        ItemStack in = ((ItemStack) stacks.get(0));
                        in.setCount(1);
                        recipes.add(clazz.getConstructor(ItemStack.class, ItemStack.class, int.class).newInstance(in, stacks.get(1), time));
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            } else {
                errorMessage("Couldn't load " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe (" + Joiner.on("-").join(comp) + ")");
            }
        }
        return recipes;
    }

    private void errorMessage(String message) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (FMLClientHandler.instance().getClientPlayerEntity() != null)
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + message));
            else
                ERRORS.add(message);
        }
        HorsePowerMod.logger.warn(message);
    }

    private Object parseItemStack(String item) throws Exception {
        String[] data = item.split("\\$");
        NBTTagCompound nbt = data.length == 1 ? null: JsonToNBT.getTagFromJson(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("$"));

        data = item.split("@");
        int amount = data.length == 1 ? 1: Integer.parseInt(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("@"));

        data = item.split(":");
        int meta = data.length == 2 ? 0 : "*".equals(data[2]) ? OreDictionary.WILDCARD_VALUE: Integer.parseInt(data[2]);

        if (item.startsWith("ore:")) {
            return OreDictionary.getOres(item.substring(4));
        } else {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("id", data[0] + ":" + data[1]);
            compound.setByte("Count", (byte) amount);
            compound.setShort("Damage", (short) meta);
            if (nbt != null)
                compound.setTag("tag", nbt);
            ItemStack stack = new ItemStack(compound);
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
        choppingBlockRecipes.add(new ChoppingBlockRecipe(input, output, time));
    }

    public void addChoppingRecipe(ChoppingBlockRecipe recipe) {
        if (getChopperResult(recipe.getInput()) != ItemStack.EMPTY) return;
        choppingBlockRecipes.add(recipe);
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

    public void removeChoppingRecipe(ChoppingBlockRecipe recipe) {
        if (hasChopperRecipe(recipe.getInput())) {
            choppingBlockRecipes.remove(recipe);
        }
    }

    public void removeChoppingRecipe(ItemStack input) {
        if (hasChopperRecipe(input)) {
            for (ChoppingBlockRecipe recipe: choppingBlockRecipes) {
                if (OreDictionary.itemMatches(recipe.getInput(), input, false)) {
                    choppingBlockRecipes.remove(recipe);
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
        for (ChoppingBlockRecipe recipe : choppingBlockRecipes) {
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
        for (ChoppingBlockRecipe recipe: choppingBlockRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<GrindstoneRecipe> getGrindstoneRecipes() {
        return grindstoneRecipes;
    }

    public ArrayList<ChoppingBlockRecipe> getChoppingRecipes() {
        return choppingBlockRecipes;
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
        for (ChoppingBlockRecipe recipe : choppingBlockRecipes) {
            if (OreDictionary.itemMatches(recipe.getInput(), stack, false)) {
                return recipe.getTime();
            }
        }

        return 1;
    }

}
