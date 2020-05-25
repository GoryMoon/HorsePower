package se.gory_moon.horsepower.compat.jei.chopping;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.compat.jei.HorsePowerCategory;
import se.gory_moon.horsepower.compat.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.ChoppingRecipe;
import se.gory_moon.horsepower.util.Localization;

public class HorsePowerChoppingCategory extends HorsePowerCategory<ChoppingRecipe> {

    private boolean handHandler;

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;

    private final String localizedName;

    public HorsePowerChoppingCategory(IGuiHelper guiHelper, boolean hand) {
        super(guiHelper);
        this.handHandler = hand;

        localizedName = handHandler ? Localization.JEI.CATEGORY$MANUAL_CHOPPING.translate(): Localization.JEI.CATEGORY$CHOPPING.translate();
    }

    @Override
    public ResourceLocation getUid() {
        return handHandler ? HorsePowerPlugin.MANUAL_CHOPPING : HorsePowerPlugin.CHOPPING;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }



    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ChoppingRecipe recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 34, 32);
        guiItemStacks.init(outputSlot, false, 90, 32);

        guiItemStacks.set(ingredients);
        super.openRecipe();
    }

    @Override
    public Class<? extends ChoppingRecipe> getRecipeClass() {
        return ChoppingRecipe.class;
    }

    @Override
    public IDrawable getIcon() {
        return HorsePowerPlugin.guiHelper.createDrawableIngredient(new ItemStack(handHandler ? Registration.CHOPPING_BLOCK.get(): Registration.CHOPPER_BLOCK.get()));
    }

    @Override
    public void setIngredients(ChoppingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, Stream.of(recipe.getRecipeOutput()).collect(Collectors.toList()));
    }
}
