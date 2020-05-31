package se.gory_moon.horsepower.compat.jei.chopping;


import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.compat.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.util.Localization;

public class ManualChoppingAxesCategory implements IRecipeCategory<ManualChoppingAxeWrapper> {

    private IGuiHelper guiHelper;
    
    public ManualChoppingAxesCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }
    
    @Override
    public ResourceLocation getUid() {
        return HorsePowerPlugin.MANUAL_CHOPPING_AXES;
    }

    @Override
    public Class<? extends ManualChoppingAxeWrapper> getRecipeClass() {
        return ManualChoppingAxeWrapper.class;
    }

    @Override
    public String getTitle() {
        return Localization.JEI.CATEGORY$MANUAL_CHOPPING_AXES.translate();
    }

    @Override
    public IDrawable getBackground() {
        return this.guiHelper.createDrawable(new ResourceLocation("horsepower", "textures/gui/jei_axes.png"), 0, 0, 99, 33);
    }

    @Override
    public IDrawable getIcon() {
        return new ManualChoppingAxesCategoryIcon(); 
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ManualChoppingAxeWrapper recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 3, 7);
        guiItemStacks.set(ingredients);
        
    }
    
    @Override
    public void setIngredients(ManualChoppingAxeWrapper recipe, IIngredients ingredients) {
        recipe.setIngredients(ingredients);
    }

    @Override
    public void draw(ManualChoppingAxeWrapper recipe, double mouseX, double mouseY) {
        recipe.drawInfo(getBackground().getWidth(), getBackground().getHeight(), mouseX, mouseY);
    }

}
