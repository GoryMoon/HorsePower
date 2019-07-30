package se.gory_moon.horsepower.recipes;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class HPRecipes {

    private static HPRecipes INSTANCE = new HPRecipes();

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
            reloadRecipes(Arrays.asList(Configs.recipes.choppingRecipes), Arrays.asList(Configs.recipes.manualChoppingRecipes),
                    Arrays.asList(Configs.recipes.pressRecipes));
    }

    public void reloadRecipes(List<String> chopping, List<String> manualChopping, List<String> press) {
        ERRORS.clear();
        choppingBlockRecipes.clear();
        manualChoppingBlockRecipes.clear();
        pressRecipes.clear();

        createRecipes(ChoppingBlockRecipe.class, chopping, true).forEach(this::addChoppingRecipe);
        createRecipes(ManualChoppingBlockRecipe.class, manualChopping, true).forEach(this::addManualChoppingRecipe);
        createRecipes(PressRecipe.class, press, false).forEach(this::addPressRecipe);

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
                        stack = Utils.parseItemStack(item, true);
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
                        Utils.errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe time '" + item + "' from config for input " + stacks.get(0) + " and result " + stacks.get(1) + ", index: " + index, false);
                        time = -1;
                    }
                } else if (stacks.size() == 3) {
                    try {
                        secondaryChance = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                        Utils.errorMessage("Parse error with " + clazz.getSimpleName().replaceAll("Recipe", "") + " recipe secondary chance '" + secondaryChance + "' from config for input " + stacks.get(0) + ", result " + stacks.get(1) + " and secondary " + stacks.get(2) + ", index: " + index, false);
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

    public void addChoppingRecipe(Block input, ItemStack output, int time, boolean hand) {
        addChoppingRecipe(Item.getItemFromBlock(input), output, time, hand);
    }

    public void addChoppingRecipe(Item input, ItemStack output, int time, boolean hand) {
        //addChoppingRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), result, time, hand);
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
        //addPressRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), result);
    }

    public void addPressRecipe(ItemStack input, ItemStack output) {
        addPressRecipe(new PressRecipe(input, output, ItemStack.EMPTY, 0, 0));
    }

    public void addPressRecipe(Block input, FluidStack output) {
        addPressRecipe(Item.getItemFromBlock(input), output);
    }

    public void addPressRecipe(Item input, FluidStack output) {
        //addPressRecipe(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), result);
    }

    public void addPressRecipe(ItemStack input, FluidStack output) {
        addPressRecipe(new PressRecipe(input, output));
    }

    public void addPressRecipe(PressRecipe recipe) {
        if (getPressResult(recipe.getInput()) != ItemStack.EMPTY) return;
        pressRecipes.put(new ComparableItemStack(recipe.getInput()), recipe);
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

    public ItemStack getChopperResult(ItemStack stack, boolean hand) {
        ChoppingBlockRecipe recipe = getChoppingBlockRecipe(stack, hand);
        return recipe != null ? recipe.getOutput(): ItemStack.EMPTY;
    }

    public ItemStack getPressResult(ItemStack stack) {
        PressRecipe recipe = getPressRecipe(stack);
        return recipe != null ? recipe.getOutput(): ItemStack.EMPTY;
    }


    public boolean hasChopperRecipe(ItemStack stack, boolean hand) {
        return getChoppingBlockRecipe(stack, hand) != null;
    }

    public boolean hasPressRecipe(ItemStack stack) {
        return getPressRecipe(stack) != null;
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


    public static AbstractHPRecipe checkTypeRecipe(AbstractHPRecipe recipe, AbstractHPRecipe.Type type) {
        return !hasTypeRecipe(recipe, type) ? null : recipe;
    }

    public static boolean hasTypeRecipe(AbstractHPRecipe recipe, AbstractHPRecipe.Type type) {
        return recipe != null && recipe.getRecipeType().is(type);
    }

    public static int getTypeTime(AbstractHPRecipe recipe, AbstractHPRecipe.Type type) {
        return hasTypeRecipe(recipe, type) ? recipe.getTime(): 16;
    }

    public int getChoppingTime(ItemStack stack, boolean hand) {
        int mult = Configs.recipes.useSeperateChoppingRecipes ? 1: (hand ? Configs.SERVER.choppingMultiplier.get(): 1);
        ChoppingBlockRecipe recipe = getChoppingBlockRecipe(stack, hand);
        return mult * (recipe != null ? recipe.getTime(): 1);
    }
}
