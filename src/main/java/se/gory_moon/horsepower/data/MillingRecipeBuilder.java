package se.gory_moon.horsepower.data;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.recipes.MillingRecipe;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MillingRecipeBuilder {

    private final MillingRecipe.Type type;
    private final Item result;
    private final int count;
    private final Ingredient ingredient;
    private final int time;
    private final Item secondary;
    private final int secondaryCount;
    private final int secondaryChance;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

    private MillingRecipeBuilder(MillingRecipe.Type type, IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        this.type = type;
        this.result = result.asItem();
        this.count = count;
        ingredient = input;
        this.time = time;
        this.secondary = secondary != null ? secondary.asItem(): null;
        this.secondaryCount = secondaryCount;
        this.secondaryChance = secondaryChance;
    }

    public static MillingRecipeBuilder millingRecipe(IItemProvider result, int count, Ingredient input, int time) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0);
    }

    public static MillingRecipeBuilder millingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance);
    }

    public static MillingRecipeBuilder handMillingRecipe(IItemProvider result, int count, Ingredient input, int time) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0);
    }

    public static MillingRecipeBuilder handMillingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance);
    }

    public static MillingRecipeBuilder horseMillingRecipe(IItemProvider result, int count, Ingredient input, int time) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0);
    }

    public static MillingRecipeBuilder horseMillingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance);
    }

    /**
     * Adds a criterion needed to unlock the recipe.
     */
    public MillingRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
        this.advancementBuilder.withCriterion(name, criterionIn);
        return this;
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, this.result.getRegistryName());
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = this.result.getRegistryName();
        if ((new ResourceLocation(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, new ResourceLocation(save));
        }
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(new MillingRecipeBuilder.Result(id, type, ingredient, this.result, this.count, this.time, this.secondary, this.secondaryCount, this.secondaryChance, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath())));
    }

    private void validate(ResourceLocation id) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final MillingRecipe.Type type;
        private final Ingredient input;
        private final Item result;
        private final int count;
        private final int time;
        private final Item secondary;
        private final int secondaryCount;
        private final int secondaryChance;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, MillingRecipe.Type type, Ingredient input, Item result, int count, int time, Item secondary, int secondaryCount, int secondaryChance, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.type = type;
            this.input = input;
            this.result = result;
            this.count = count;
            this.time = time;
            this.secondary = secondary;
            this.secondaryCount = secondaryCount;
            this.secondaryChance = secondaryChance;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("ingredient", this.input.serialize());

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", this.result.getRegistryName().toString());
            if (this.count > 1) {
                resultObj.addProperty("count", this.count);
            }
            json.add("result", resultObj);
            json.addProperty("time", this.time);
            json.addProperty("milling_type", this.type.getName());
            if (this.secondary != null && this.secondaryCount > 0 && this.secondaryChance > 0) {
                JsonObject secondaryObj = new JsonObject();
                secondaryObj.addProperty("item", this.result.getRegistryName().toString());
                if (this.count > 1) {
                    secondaryObj.addProperty("count", this.count);
                }
                json.add("secondary", secondaryObj);
                json.addProperty("secondary_chance", secondaryChance);
            }
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return RecipeSerializers.MILLING_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return advancementBuilder.serialize();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return advancementId;
        }
    }

}
