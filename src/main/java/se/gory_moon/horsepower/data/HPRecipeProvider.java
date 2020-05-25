package se.gory_moon.horsepower.data;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.items.ModItems;
import se.gory_moon.horsepower.util.color.HPTags;

import java.util.Arrays;
import java.util.function.Consumer;

public class HPRecipeProvider extends RecipeProvider {

    public HPRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.DOUGH.orElse(null)).addIngredient(ModItems.FLOUR.orElse(null)).addIngredient(Items.WATER_BUCKET).addCriterion("has_flour", hasItem(HPTags.Items.FLOUR)).build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.millstoneBlock.orElse(null)).key('#', Tags.Items.STONE).key('L', Items.LEAD).key('S', Tags.Items.RODS_WOODEN).patternLine("SLS").patternLine("###").patternLine("###").addCriterion("has_lead", hasItem(Items.LEAD)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.manualMillstoneBlock.orElse(null)).key('#', Tags.Items.STONE).key('S', Tags.Items.RODS_WOODEN).patternLine("  S").patternLine("###").patternLine("###").addCriterion("has_stone", hasItem(Tags.Items.STONE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.chopperBlock.orElse(null)).patternLine("   ").patternLine("   ").patternLine("WWW").key('W', Items.BIRCH_LOG).addCriterion("has_wood", hasItem(Items.BIRCH_LOG)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.choppingBlock.orElse(null)).patternLine("   ").patternLine("   ").patternLine("WWW").key('W', Items.OAK_LOG).addCriterion("has_wood", hasItem(Items.OAK_LOG)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.pressBlock.orElse(null)).key('#', ItemTags.PLANKS).key('L', Items.LEAD).key('S', Tags.Items.RODS_WOODEN).key('P', ItemTags.WOODEN_PRESSURE_PLATES).patternLine("LSL").patternLine("#P#").patternLine("###").addCriterion("has_lead", hasItem(Items.LEAD)).build(consumer);

        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(ModItems.DOUGH.orElse(null)), Items.BREAD, 0.1F, 200).addCriterion("has_dough", hasItem(HPTags.Items.DOUGH)).build(consumer, "horsepower:bread");


        //Milling recipes
        MillingRecipeBuilder.millingRecipe(ModItems.FLOUR.get(), 1, Ingredient.fromItems(Items.WHEAT), 12,1).addCriterion("has_wheat", hasItem(Items.WHEAT)).build(consumer, "horsepower:milling/flour");
        MillingRecipeBuilder.millingRecipe(Items.SUGAR, 1, Ingredient.fromItems(Items.SUGAR_CANE), 12,1).addCriterion("has_sugar_cane", hasItem(Items.SUGAR_CANE)).build(consumer, "horsepower:milling/sugar");
        MillingRecipeBuilder.millingRecipe(Items.BONE_MEAL, 3, Ingredient.fromItems(Items.BONE), 12,1).addCriterion("has_bone", hasItem(Items.BONE)).build(consumer, "horsepower:milling/bone_meal");
        MillingRecipeBuilder.millingRecipe(Items.BONE_MEAL, 9, Ingredient.fromItems(Blocks.BONE_BLOCK), 12,1).addCriterion("has_bone_block", hasItem(Blocks.BONE_BLOCK)).build(consumer, "horsepower:milling/bone_meal_2");

        MillingRecipeBuilder.millingRecipe(Items.RED_DYE, 1, Ingredient.fromItems(Items.POPPY, Items.RED_TULIP, Items.BEETROOT), 12,1).addCriterion("has_red_plant", hasItem(Items.POPPY, Items.RED_TULIP, Items.BEETROOT)).build(consumer, "horsepower:milling/red_dye");
        MillingRecipeBuilder.millingRecipe(Items.RED_DYE, 2, Ingredient.fromItems(Items.POPPY, Items.RED_TULIP, Items.BEETROOT), 12,1).addCriterion("has_red_plant", hasItem(Items.ROSE_BUSH)).build(consumer, "horsepower:milling/red_dye_2");
        MillingRecipeBuilder.millingRecipe(Items.LIGHT_GRAY_DYE, 1, Ingredient.fromItems(Items.AZURE_BLUET, Items.WHITE_TULIP, Items.OXEYE_DAISY), 12,1).addCriterion("has_light_grey_plant", hasItem(Items.AZURE_BLUET, Items.WHITE_TULIP, Items.OXEYE_DAISY)).build(consumer, "horsepower:milling/light_gray_dye");
        MillingRecipeBuilder.millingRecipe(Items.PINK_DYE, 1, Ingredient.fromItems(Items.PINK_TULIP, Items.PEONY), 12,1).addCriterion("has_pink_plant", hasItem(Items.PINK_TULIP, Items.PEONY)).build(consumer, "horsepower:milling/pink_dye");
        MillingRecipeBuilder.millingRecipe(Items.YELLOW_DYE, 1, Ingredient.fromItems(Items.DANDELION), 12,1).addCriterion("has_yellow_plant", hasItem(Items.DANDELION)).build(consumer, "horsepower:milling/yellow_dye");
        MillingRecipeBuilder.millingRecipe(Items.YELLOW_DYE, 2, Ingredient.fromItems(Items.SUNFLOWER), 12,1).addCriterion("has_yellow_plant", hasItem(Items.SUNFLOWER)).build(consumer, "horsepower:milling/yellow_dye_2");
        MillingRecipeBuilder.millingRecipe(Items.LIGHT_BLUE_DYE, 1, Ingredient.fromItems(Items.BLUE_ORCHID), 12,1).addCriterion("has_light_blue_plant", hasItem(Items.BLUE_ORCHID)).build(consumer, "horsepower:milling/light_blue_dye");
        MillingRecipeBuilder.millingRecipe(Items.MAGENTA_DYE, 1, Ingredient.fromItems(Items.ALLIUM), 12,1).addCriterion("has_magenta_plant", hasItem(Items.ALLIUM)).build(consumer, "horsepower:milling/magenta_dye");
        MillingRecipeBuilder.millingRecipe(Items.MAGENTA_DYE, 2, Ingredient.fromItems(Items.LILAC), 12,1).addCriterion("has_magenta_plant", hasItem(Items.LILAC)).build(consumer, "horsepower:milling/magenta_dye_2");
        MillingRecipeBuilder.millingRecipe(Items.ORANGE_DYE, 1, Ingredient.fromItems(Items.ORANGE_TULIP), 12,1).addCriterion("has_orange_plant", hasItem(Items.ORANGE_TULIP)).build(consumer, "horsepower:milling/orange_dye");

        //Pressing Recipes
        PressingRecipeBuilder.pressingRecipe(Items.DIRT, 1, Ingredient.fromItems(Items.WHEAT_SEEDS),12).addCriterion("has_seed", hasItem(Items.WHEAT_SEEDS)).build(consumer, "horsepower:pressing/dirt");
        PressingRecipeBuilder.pressingRecipe(new FluidStack(Fluids.WATER, 1000), Ingredient.fromTag(ItemTags.LEAVES),8).addCriterion("has_leave", hasItem(Items.OAK_LEAVES)).build(consumer, "horsepower:pressing/water_from_leaves");

        //Chopping Recipes
        ChoppingRecipeBuilder.choppingRecipe(Items.OAK_PLANKS, 1, Ingredient.fromItems(Items.OAK_LOG)).addCriterion("has_oak", hasItem(Items.OAK_LOG)).build(consumer, "horsepower:chopping/oak_planks");
        ChoppingRecipeBuilder.choppingRecipe(Items.ACACIA_PLANKS, 1, Ingredient.fromItems(Items.ACACIA_LOG)).addCriterion("has_acacia", hasItem(Items.ACACIA_LOG)).build(consumer, "horsepower:chopping/acacia_planks");
        ChoppingRecipeBuilder.choppingRecipe(Items.BIRCH_PLANKS, 1, Ingredient.fromItems(Items.BIRCH_LOG)).addCriterion("has_birch", hasItem(Items.BIRCH_LOG)).build(consumer, "horsepower:chopping/birch_planks");
        ChoppingRecipeBuilder.choppingRecipe(Items.DARK_OAK_PLANKS, 1, Ingredient.fromItems(Items.DARK_OAK_LOG)).addCriterion("has_dark_oak", hasItem(Items.DARK_OAK_LOG)).build(consumer, "horsepower:chopping/dark_oak_planks");
        ChoppingRecipeBuilder.choppingRecipe(Items.JUNGLE_PLANKS, 1, Ingredient.fromItems(Items.JUNGLE_LOG)).addCriterion("has_jungle", hasItem(Items.JUNGLE_LOG)).build(consumer, "horsepower:chopping/jungle_planks");
        ChoppingRecipeBuilder.choppingRecipe(Items.SPRUCE_PLANKS, 1, Ingredient.fromItems(Items.SPRUCE_LOG)).addCriterion("has_spruce", hasItem(Items.SPRUCE_LOG)).build(consumer, "horsepower:chopping/spruce_planks");
    }

    @Override
    public String getName() {
        return "Horsepower Recipes";
    }

    public InventoryChangeTrigger.Instance hasItem(IItemProvider... providers) {
        return hasItem(Arrays.stream(providers).map(provider -> ItemPredicate.Builder.create().item(provider).build()).toArray(ItemPredicate[]::new));
    }
}
