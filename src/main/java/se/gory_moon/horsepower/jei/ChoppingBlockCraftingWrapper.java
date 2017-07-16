package se.gory_moon.horsepower.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.blocks.BlockChopper;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.recipes.ChoppingRecipe;

import java.util.List;

public class ChoppingBlockCraftingWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper, ICustomCraftingRecipeWrapper {

    private final ChoppingRecipe recipe;
    private final int width;
    private final int height;
    private final List<List<ItemStack>> outputs;

    public ChoppingBlockCraftingWrapper(ChoppingRecipe recipe) {
        this.recipe = recipe;

        for (Object input : this.recipe.getIngredients()) {
            if (input instanceof ItemStack) {
                ItemStack itemStack = (ItemStack) input;
                if (itemStack.getCount() != 1) {
                    itemStack.setCount(1);
                }
            }
        }
        this.width = recipe.getWidth();
        this.height = recipe.getHeight();

        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for(ItemStack stack : recipe.outputBlocks) {
            BlockChopper block = ModBlocks.BLOCK_CHOPPER;
            Block baseBlock = Block.getBlockFromItem(stack.getItem());
            if(stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                for(ItemStack sub : HorsePowerPlugin.jeiHelpers.getStackHelper().getSubtypes(stack)) {
                    builder.add(BlockChopper.createItemStack(block, baseBlock, sub.getItemDamage()));
                }
            }
            else {
                builder.add(BlockChopper.createItemStack(block, baseBlock, stack.getItemDamage()));
            }
        }
        outputs = ImmutableList.of(builder.build());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = HorsePowerPlugin.jeiHelpers.getStackHelper();

        List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());
        ingredients.setInputLists(ItemStack.class, inputs);

        if (!outputs.isEmpty())
            ingredients.setOutputLists(ItemStack.class, outputs);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private boolean isOutputBlock(ItemStack stack) {
        if(stack.isEmpty()) {
            return false;
        }

        for(ItemStack output : recipe.outputBlocks) {
            // if the item matches the oredict entry, it is an output block
            if(OreDictionary.itemMatches(output, stack, false)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class).get(0);

        // determine the focused stack
        IFocus<?> ifocus = recipeLayout.getFocus();
        Object focusObj = ifocus.getValue();

        // if the thing in focus is an itemstack
        if(focusObj instanceof ItemStack) {
            IGuiIngredientGroup<ItemStack> guiIngredients = recipeLayout.getIngredientsGroup(ItemStack.class);
            ItemStack focus = (ItemStack)focusObj;
            IFocus.Mode mode = ifocus.getMode();

            // input means we clicked on an ingredient, make sure it is one that affects the legs
            if(mode == IFocus.Mode.INPUT && isOutputBlock(focus)) {
                // first, get the output recipe
                BlockChopper block = ModBlocks.BLOCK_CHOPPER;

                // then create a stack with the focus item (which we already validated above)
                ItemStack outputFocus = BlockChopper.createItemStack(block, Block.getBlockFromItem(focus.getItem()), focus.getItemDamage());

                // and finally, set the focus override for the recipe
                guiIngredients.setOverrideDisplayFocus(HorsePowerPlugin.recipeRegistry.createFocus(IFocus.Mode.OUTPUT, outputFocus));
            }

            // if we clicked the table, remove all items which affect the legs textures that are not the leg item
            else if(mode == IFocus.Mode.OUTPUT) {
                // so determine the legs
                ItemStack base = new ItemStack(focus.hasTagCompound() ? focus.getTagCompound().getCompoundTag("textureBlock") : new NBTTagCompound());
                if(!base.isEmpty()) {
                    // and loop through all slots removing leg affecting inputs which don't match
                    guiIngredients.setOverrideDisplayFocus(HorsePowerPlugin.recipeRegistry.createFocus(IFocus.Mode.INPUT, base));
                }
            }
        }

        // add the itemstacks to the grid
        HorsePowerPlugin.craftingGridHelper.setInputs(guiItemStacks, inputs, this.getWidth(), this.getHeight());
        recipeLayout.getItemStacks().set(0, outputs);
    }
}
