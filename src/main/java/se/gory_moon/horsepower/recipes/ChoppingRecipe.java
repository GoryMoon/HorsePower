package se.gory_moon.horsepower.recipes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import se.gory_moon.horsepower.blocks.BlockHPChoppingBase;

import javax.annotation.Nonnull;
import java.util.List;

import static se.gory_moon.horsepower.blocks.BlockChopper.createItemStack;

public class ChoppingRecipe extends ShapedOreRecipe {

    public final List<ItemStack> outputBlocks;

    public ChoppingRecipe(ResourceLocation location, List<ItemStack> variantItems, BlockHPChoppingBase result, Object... recipe) {
        super(location, result, recipe);
        this.outputBlocks = variantItems;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting craftMatrix) {
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            for (ItemStack ore: outputBlocks) {
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if (OreDictionary.itemMatches(ore, stack, false) && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
                    BlockHPChoppingBase block = (BlockHPChoppingBase) Block.getBlockFromItem(getSimpleRecipeOutput().getItem());;
                    return createItemStack(block, Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
                }
            }
        }
        return super.getCraftingResult(craftMatrix);
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        if (!outputBlocks.isEmpty() && !output.isEmpty()) {
            ItemStack stack = outputBlocks.get(0);
            BlockHPChoppingBase block = (BlockHPChoppingBase) Block.getBlockFromItem(output.getItem());
            int meta = stack.getMetadata();
            if (meta == OreDictionary.WILDCARD_VALUE)
                meta = 0;
            return createItemStack(block, Block.getBlockFromItem(stack.getItem()), meta);

        }
        return super.getRecipeOutput();
    }

    public ItemStack getSimpleRecipeOutput() {
        return output;
    }
}
