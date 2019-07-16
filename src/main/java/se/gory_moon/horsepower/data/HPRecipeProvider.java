package se.gory_moon.horsepower.data;

import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.items.ModItems;
import se.gory_moon.horsepower.util.color.HPTags;

import java.util.function.Consumer;

public class HPRecipeProvider extends RecipeProvider {

    public HPRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.DOUGH.orElse(null)).addIngredient(ModItems.FLOUR.orElse(null)).addIngredient(Items.WATER_BUCKET).addCriterion("has_flour", hasItem(HPTags.Items.FLOUR)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.BLOCK_MILLSTONE.orElse(null)).key('#', Tags.Items.STONE).key('L', Items.LEAD).key('S', Tags.Items.RODS_WOODEN).patternLine("SLS").patternLine("###").patternLine("###").addCriterion("has_lead", hasItem(Items.LEAD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.BLOCK_HAND_MILLSTONE.orElse(null)).key('#', Tags.Items.STONE).key('S', Tags.Items.RODS_WOODEN).patternLine("  S").patternLine("###").patternLine("###").addCriterion("has_stone", hasItem(Tags.Items.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.BLOCK_PRESS.orElse(null)).key('#', ItemTags.PLANKS).key('L', Items.LEAD).key('S', Tags.Items.RODS_WOODEN).key('P', ItemTags.WOODEN_PRESSURE_PLATES).patternLine("LSL").patternLine("#P#").patternLine("###").addCriterion("has_lead", hasItem(Items.LEAD)).build(consumer);

        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(ModItems.DOUGH.orElse(null)), Items.BREAD, 0.1F, 200).addCriterion("has_dough", hasItem(HPTags.Items.DOUGH)).build(consumer, "horsepower:bread");
    }

    @Override
    public String getName() {
        return "Horsepower Recipes";
    }
}
