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
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class AbstractRecipeBuilder {

    private final AbstractHPRecipe.Type type;
    private final Item result;
    private final int count;
    private final Ingredient ingredient;
    private final int time;
    private final Item secondary;
    private final int secondaryCount;
    private final int secondaryChance;
    private final FluidStack outputFluid;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

    protected AbstractRecipeBuilder(AbstractHPRecipe.Type type, IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance, FluidStack outputFluid) {
        this.type = type;
        this.result = result != null ? result.asItem(): null;
        this.count = count;
        ingredient = input;
        this.time = time;
        this.secondary = secondary != null ? secondary.asItem(): null;
        this.secondaryCount = secondaryCount;
        this.secondaryChance = secondaryChance;
        this.outputFluid = outputFluid;
    }

    /**
     * Adds a criterion needed to unlock the recipe.
     */
    public AbstractRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
        this.advancementBuilder.withCriterion(name, criterionIn);
        return this;
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, this.result != null ? this.result.getRegistryName(): ResourceLocation.tryCreate(outputFluid.getFluid().getName()));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = this.result != null ? this.result.getRegistryName(): ResourceLocation.tryCreate(outputFluid.getFluid().getName());
        if ((new ResourceLocation(save)).equals(resourcelocation)) {
            throw new IllegalStateException("HP Recipe " + save + " should remove its 'save' argument");
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
        consumerIn.accept(new Result(getSerializer(), id, type, ingredient, this.result, this.count, this.time, this.secondary, this.secondaryCount, this.secondaryChance, this.outputFluid, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath())));
    }

    private void validate(ResourceLocation id) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public abstract IRecipeSerializer<?> getSerializer();

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final AbstractHPRecipe.Type type;
        private final Ingredient input;
        private final Item result;
        private final int count;
        private final int time;
        private final Item secondary;
        private final int secondaryCount;
        private final int secondaryChance;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;
        private IRecipeSerializer<?> serializer;
        private FluidStack outputFluid;

        public Result(IRecipeSerializer<?> serializer, ResourceLocation id, AbstractHPRecipe.Type type, Ingredient input, Item result, int count, int time, Item secondary, int secondaryCount, int secondaryChance, FluidStack outputFluid, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.serializer = serializer;
            this.id = id;
            this.type = type;
            this.input = input;
            this.result = result;
            this.count = count;
            this.time = time;
            this.secondary = secondary;
            this.secondaryCount = secondaryCount;
            this.secondaryChance = secondaryChance;
            this.outputFluid = outputFluid;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("ingredient", this.input.serialize());

            if (outputFluid != null) {
                JsonObject fluid = new JsonObject();
                fluid.addProperty("id", outputFluid.getFluid().getName());
                fluid.addProperty("amount", outputFluid.amount);
                json.add("fluid", fluid);
            } else {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", this.result.getRegistryName().toString());
                if (this.count > 1) {
                    resultObj.addProperty("count", this.count);
                }
                json.add("result", resultObj);
            }
            if (time > 0) {
                json.addProperty("time", this.time);
            }
            if (this.type != null) {
                json.addProperty("recipe_type", this.type.getName());
            }
            if (this.secondary != null && this.secondaryCount > 0 && this.secondaryChance > 0) {
                JsonObject secondaryObj = new JsonObject();
                secondaryObj.addProperty("item", this.secondary.getRegistryName().toString());
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
            return serializer;
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
