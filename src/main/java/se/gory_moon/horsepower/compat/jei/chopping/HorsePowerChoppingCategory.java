package se.gory_moon.horsepower.compat.jei.chopping;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.compat.jei.HorsePowerCategory;
import se.gory_moon.horsepower.compat.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.ChoppingRecipe;
import se.gory_moon.horsepower.util.Localization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HorsePowerChoppingCategory extends HorsePowerCategory<ChoppingRecipe> {

    private final boolean handHandler;

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;

    private final String localizedName;
    
    private final IDrawableAnimated arrow;

    public HorsePowerChoppingCategory(IGuiHelper guiHelper, boolean hand) {
        super(guiHelper);
        this.handHandler = hand;

        localizedName = handHandler ? Localization.JEI.CATEGORY$MANUAL_CHOPPING.translate(): Localization.JEI.CATEGORY$CHOPPING.translate();
    
        arrow = guiHelper.drawableBuilder(HorsePowerCategory.COMPONENTS, 60, 0, 24, 17)
                .buildAnimated(150, IDrawableAnimated.StartDirection.LEFT, false);
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
        return HorsePowerPlugin.guiHelper.createDrawableIngredient(new ItemStack(handHandler ? Registration.MANUAL_CHOPPER_BLOCK.get(): Registration.CHOPPER_BLOCK.get()));
    }

    @Override
    public void setIngredients(ChoppingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, Stream.of(recipe.getRecipeOutput()).collect(Collectors.toList()));
    }

    @Override
    public void draw(ChoppingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
        arrow.draw(matrixStack, 57, 32);
    }

    @Override
    public List<ITextComponent> getTooltipStrings(ChoppingRecipe recipe, double mouseX, double mouseY) {
        List<ITextComponent> tooltip = super.getTooltipStrings(recipe, mouseX, mouseY);
        if (mouseX >= 55 && mouseY >= 21 && mouseX < 80 && mouseY < 45) {
            tooltip.add(new TranslationTextComponent(handHandler ? "info.horsepower.manual.chopping.time": "info.horsepower.horse.chopping.time", getLaps(recipe)));
        }
        return tooltip;
    }

    private double getLaps(ChoppingRecipe recipe) {
        return handHandler ? 
                recipe.getTime() // means amount of chops for manual chopper
                : 
                (Math.round((Configs.SERVER.pointsForWindup.get().doubleValue() / 8D) * 100.0D) / 100.0D) //how many laps for a windup
                * recipe.getTime(); // means the amount of chops of the cutter to break the item
    }
}
