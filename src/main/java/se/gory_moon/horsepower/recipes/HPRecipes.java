package se.gory_moon.horsepower.recipes;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.items.ModItems;
import se.gory_moon.horsepower.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class HPRecipes {

    private static HPRecipes INSTANCE = new HPRecipes();

    private final Map<ComparableItemStack, MillstoneRecipe> millstoneRecipes = Maps.newHashMap();
    private final Map<ComparableItemStack, MillstoneRecipe> handMillstoneRecipes = Maps.newHashMap();
    private final Map<ComparableItemStack, ChoppingBlockRecipe> choppingBlockRecipes = Maps.newHashMap();
    private final Map<ComparableItemStack, ChoppingBlockRecipe> manualChoppingBlockRecipes = Maps.newHashMap();
    private final Map<ComparableItemStack, PressRecipe> pressRecipes = Maps.newHashMap();
    public static ArrayList<String> ERRORS = Lists.newArrayList();
    public static boolean serverSyncedRecipes = false;

    public static HPRecipes instance() {
        return INSTANCE;
    }

    private HPRecipes() {}
    public void reloadRecipes() {
        if (!serverSyncedRecipes)
            reloadRecipes(Arrays.asList(Configs.recipes.millstoneRecipes), Arrays.asList(Configs.recipes.handMillstoneRecipes),
                    Arrays.asList(Configs.recipes.choppingRecipes), Arrays.asList(Configs.recipes.manualChoppingRecipes),
                    Arrays.asList(Configs.recipes.pressRecipes));
    }

    public void reloadRecipes(List<String> millstone, List<String> handMillstone, List<String> chopping, List<String> manualChopping, List<String> press) {
        ERRORS.clear();
        millstoneRecipes.clear();
        handMillstoneRecipes.clear();
        choppingBlockRecipes.clear();
        manualChoppingBlockRecipes.clear();
        pressRecipes.clear();

        createRecipes(MillstoneRecipe.class, millstone, true).forEach(this::addMillstoneRecipe);
        createRecipes(HandMillstoneRecipe.class, handMillstone, true).forEach(this::addHandMillstoneRecipe);
        createRecipes(ChoppingBlockRecipe.class, chopping, true).forEach(this::addChoppingRecipe);
        createRecipes(ManualChoppingBlockRecipe.class, manualChopping, true).forEach(this::addManualChoppingRecipe);
        createRecipes(PressRecipe.class, press, false).forEach(this::addPressRecipe);

        addMillstoneRecipe(Items.BONE, new ItemStack(Items.BONE_MEAL, 3), 12, false);
        addMillstoneRecipe(Items.WHEAT, new ItemStack(ModItems.FLOUR, 1), 12, false);
        //HorsePowerMod.tweakerPlugin.applyTweaker();
    }

    private <T extends HPRecipeBase> List<T> createRecipes(Class<T> clazz, List<String> data, boolean requireTime) {
        List<T> recipes = new ArrayList<>();
        int index = 0;
        for (String aData : data) {
            String[] comp = aData.split("-");
            if (aData.isEmpty()) continue;

            List<Object> stacks = Lists.newArrayList();
            int time = -1;
            int secondaryChance = 0;
            for (String item : comp) {
                if (item.contains(":")) {
                    Object stack;
                    try {
                        stack = Utils.parseItemStack(item, true, true);
                    } catch (Exception e) {
                        Utils.errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe item '" + item + "' from config" + (stacks.size() > 0 ? " with item " + stacks.get(0): "") + ", index: " + index, false);
                        break;
                    }
                    if ((stack instanceof ItemStack && !((ItemStack) stack).isEmpty()) || (!(stack instanceof ItemStack) && stack != null))
                        stacks.add(stack);
                } else if (stacks.size() == 2) {
                    try {
                        time = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        Utils.errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe time '" + item + "' from config for input " + stacks.get(0) + " and output " + stacks.get(1) + ", index: " + index, false);
                        time = -1;
                    }
                } else if (stacks.size() == 3) {
                    try {
                        secondaryChance = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        Utils.errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe secondary chance '" + secondaryChance + "' from config for input " + stacks.get(0) + ", output " + stacks.get(1) + " and secondary " + stacks.get(2) + ", index: " + index, false);
                    }
                }
            }
            boolean flag = false;
            if (stacks.size() >= 2 && ((requireTime && time > -1) || (!requireTime && time == -1))) {
                if (!(stacks.size() == 3 && secondaryChance == 0)) {
                    try {
                        List<ItemStack> items;
                        if (stacks.get(0) instanceof List) {
                            items = (List<ItemStack>) stacks.get(0);
                        } else {
                            items = Collections.singletonList((ItemStack) stacks.get(0));
                        }
                        for (ItemStack stack : items) {
                            if (stacks.get(1) instanceof FluidStack) {
                                FluidStack fluid = (FluidStack) stacks.get(1);
                                recipes.add(clazz.getConstructor(ItemStack.class, FluidStack.class).newInstance(stack, fluid));
                            } else {
                                ItemStack secondary = stacks.size() == 3 ? (ItemStack) stacks.get(2) : ItemStack.EMPTY;
                                recipes.add(clazz.getConstructor(ItemStack.class, ItemStack.class, ItemStack.class, int.class, int.class).newInstance(stack, stacks.get(1), secondary, secondaryChance, time));
                            }
                        }
                        flag = true;
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        Utils.errorMessage("This recipe don't accept any fluids", false);
                    }
                }
            }
            if (!flag) {
                Utils.errorMessage("Couldn't load " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe (" + Joiner.on("-").join(comp) + "), index: " + index, false);
            }
            index++;
        }
        return recipes;
    }

    public void addMillstoneRecipe(Block input, ItemStack output, int time, boolean hand) {
        addMillstoneRecipe(Item.getItemFromBlock(input), output, time, hand);
    }

    public void addMillstoneRecipe(Item input, ItemStack output, int time, boolean hand) {
        addMillstoneRecipe(new ItemStack(input, 1), output, time, hand);
    }

    public void addMillstoneRecipe(ItemStack input, ItemStack output, int time, boolean hand) {
        if (getMillstoneResult(input, hand) != ItemStack.EMPTY) return;
        addMillstoneRecipe(input, output, ItemStack.EMPTY, 0, time, hand);
    }

    public void addMillstoneRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time, boolean hand) {
        if (getMillstoneResult(input, hand) != ItemStack.EMPTY) return;
        addMillstoneRecipe(new MillstoneRecipe(input, output, secondary, secondaryChance, time), hand);
    }

    public void addMillstoneRecipe(MillstoneRecipe recipe, boolean hand) {
        if (getMillstoneResult(recipe.getInput(), hand) != ItemStack.EMPTY) return;
        if (hand && Configs.recipes.useSeperateMillstoneRecipes)
            addHandMillstoneRecipe(recipe);
        else
            addMillstoneRecipe(recipe);
    }

    private void addMillstoneRecipe(MillstoneRecipe recipe) {
        millstoneRecipes.put(new ComparableItemStack(recipe.getInput()), recipe);
    }

    private void addHandMillstoneRecipe(MillstoneRecipe recipe) {
        handMillstoneRecipes.put(new ComparableItemStack(recipe.getInput()), recipe);
    }

    public void addChoppingRecipe(Block input, ItemStack output, int time, boolean hand) {
        addChoppingRecipe(Item.getItemFromBlock(input), output, time, hand);
    }

    public void addChoppingRecipe(Item input, ItemStack output, int time, boolean hand) {
        //addChoppingRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, time, hand);
    }

    public void addChoppingRecipe(ItemStack input, ItemStack output, int time, boolean hand) {
        addChoppingRecipe(input, output, ItemStack.EMPTY, 0, time, hand);
    }

    public void addChoppingRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time, boolean hand) {
        addChoppingRecipe(new ChoppingBlockRecipe(input, output, ItemStack.EMPTY, secondaryChance, time), hand);
    }

    public void addChoppingRecipe(ChoppingBlockRecipe recipe, boolean hand) {
        if (getChopperResult(recipe.getInput(), hand) != ItemStack.EMPTY) return;
        if (hand && Configs.recipes.useSeperateChoppingRecipes)
            addManualChoppingRecipe(recipe);
        else
            addChoppingRecipe(recipe);
    }

    public void addChoppingRecipe(ChoppingBlockRecipe recipe) {
        choppingBlockRecipes.put(new ComparableItemStack(recipe.getInput()), recipe);
    }

    public void addManualChoppingRecipe(ChoppingBlockRecipe recipe) {
        manualChoppingBlockRecipes.put(new ComparableItemStack(recipe.getInput()), recipe);
    }

    public void addPressRecipe(Block input, ItemStack output) {
        addPressRecipe(Item.getItemFromBlock(input), output);
    }

    public void addPressRecipe(Item input, ItemStack output) {
        //addPressRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output);
    }

    public void addPressRecipe(ItemStack input, ItemStack output) {
        addPressRecipe(new PressRecipe(input, output, ItemStack.EMPTY, 0, 0));
    }

    public void addPressRecipe(Block input, FluidStack output) {
        addPressRecipe(Item.getItemFromBlock(input), output);
    }

    public void addPressRecipe(Item input, FluidStack output) {
        //addPressRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output);
    }

    public void addPressRecipe(ItemStack input, FluidStack output) {
        addPressRecipe(new PressRecipe(input, output));
    }

    public void addPressRecipe(PressRecipe recipe) {
        if (getPressResult(recipe.getInput()) != ItemStack.EMPTY) return;
        pressRecipes.put(new ComparableItemStack(recipe.getInput()), recipe);
    }

    public void removeMillstoneRecipe(MillstoneRecipe recipe, boolean hand) {
        removeMillstoneRecipe(recipe.getInput(), hand);
    }

    public void removeMillstoneRecipe(ItemStack input, boolean hand) {
        if (hand && Configs.recipes.useSeperateMillstoneRecipes)
            handMillstoneRecipes.remove(new ComparableItemStack(input));
        else
            millstoneRecipes.remove(new ComparableItemStack(input));
    }

    public void removeChoppingRecipe(ChoppingBlockRecipe recipe, boolean hand) {
        removeChoppingRecipe(recipe.getInput(), hand);
    }

    public void removeChoppingRecipe(ItemStack input, boolean hand) {
        if (hand && Configs.recipes.useSeperateChoppingRecipes)
            manualChoppingBlockRecipes.remove(new ComparableItemStack(input));
        else
            choppingBlockRecipes.remove(new ComparableItemStack(input));
    }

    public void removePressRecipe(PressRecipe recipe) {
        removePressRecipe(recipe.getInput());
    }

    public void removePressRecipe(ItemStack input) {
        pressRecipes.remove(new ComparableItemStack(input));
    }

    public MillstoneRecipe getMillstoneRecipe(ItemStack stack, boolean hand) {
        if (stack.isEmpty())
            return null;
        return hand && Configs.recipes.useSeperateMillstoneRecipes ? handMillstoneRecipes.get(new ComparableItemStack(stack)): millstoneRecipes.get(new ComparableItemStack(stack));
    }

    public ChoppingBlockRecipe getChoppingBlockRecipe(ItemStack stack, boolean hand) {
        if (stack.isEmpty())
            return null;
        return hand && Configs.recipes.useSeperateChoppingRecipes ? manualChoppingBlockRecipes.get(new ComparableItemStack(stack)): choppingBlockRecipes.get(new ComparableItemStack(stack));
    }

    public PressRecipe getPressRecipe(ItemStack stack) {
        if (stack.isEmpty())
            return null;
        return pressRecipes.get(new ComparableItemStack(stack));
    }

    public ItemStack getMillstoneResult(ItemStack stack, boolean hand) {
        MillstoneRecipe recipe = getMillstoneRecipe(stack, hand);
        return recipe != null ? recipe.getOutput(): ItemStack.EMPTY;
    }

    public ItemStack getMillstoneSecondary(ItemStack stack, boolean hand) {
        MillstoneRecipe recipe = getMillstoneRecipe(stack, hand);
        return recipe != null ? recipe.getSecondary(): ItemStack.EMPTY;
    }

    public ItemStack getChopperResult(ItemStack stack, boolean hand) {
        ChoppingBlockRecipe recipe = getChoppingBlockRecipe(stack, hand);
        return recipe != null ? recipe.getOutput(): ItemStack.EMPTY;
    }

    public ItemStack getPressResult(ItemStack stack) {
        PressRecipe recipe = getPressRecipe(stack);
        return recipe != null ? recipe.getOutput(): ItemStack.EMPTY;
    }

    public boolean hasMillstoneRecipe(ItemStack stack, boolean hand) {
        return getMillstoneRecipe(stack, hand) != null;
    }

    public boolean hasChopperRecipe(ItemStack stack, boolean hand) {
        return getChoppingBlockRecipe(stack, hand) != null;
    }

    public boolean hasPressRecipe(ItemStack stack) {
        return getPressRecipe(stack) != null;
    }

    public Collection<MillstoneRecipe> getMillstoneRecipes() {
        return millstoneRecipes.values();
    }

    public Collection<MillstoneRecipe> getHandMillstoneRecipes() {
        return handMillstoneRecipes.values();
    }

    public Collection<ChoppingBlockRecipe> getChoppingRecipes() {
        return choppingBlockRecipes.values();
    }

    public Collection<ChoppingBlockRecipe> getManualChoppingRecipes() {
        return manualChoppingBlockRecipes.values();
    }

    public Collection<PressRecipe> getPressRecipes() {
        return pressRecipes.values();
    }

    public int getMillstoneTime(ItemStack stack, boolean hand) {
        MillstoneRecipe recipe = getMillstoneRecipe(stack, hand);
        return recipe != null ? recipe.getTime(): 16;
    }

    public int getChoppingTime(ItemStack stack, boolean hand) {
        int mult = Configs.recipes.useSeperateChoppingRecipes ? 1: (hand ? Configs.general.choppMultiplier: 1);
        ChoppingBlockRecipe recipe = getChoppingBlockRecipe(stack, hand);
        return mult * (recipe != null ? recipe.getTime(): 1);
    }
}
