package se.gory_moon.horsepower.jei.press;
/*
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.util.Localization;

public class HorsePowerPressCategory extends HorsePowerCategory<PressRecipeWrapper> {

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;

    private final String localizedName;
    private boolean isLiquid;

    public HorsePowerPressCategory(IGuiHelper guiHelper, boolean isLiquid) {
        super(guiHelper);
        this.isLiquid = isLiquid;
        if (isLiquid)
            background = guiHelper.createDrawable(new ResourceLocation("horsepower", "textures/gui/jei_fluid.png"), 0, 0, 146, 74);
        localizedName = isLiquid ? Localization.GUI.CATEGORY_PRESS_FLUID.translate(): Localization.GUI.CATEGORY_PRESS_ITEM.translate();
    }

    @Override
    public String getUid() {
        return isLiquid ? HorsePowerPlugin.PRESS_FLUID: HorsePowerPlugin.PRESS_ITEM;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PressRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStack = recipeLayout.getFluidStacks();

        guiItemStacks.init(inputSlot, true, 34, 32);
        if (isLiquid)
            guiFluidStack.init(outputSlot, false, 95, 23, 16, 27, Configs.general.pressFluidTankSize, true, null);
        else
            guiItemStacks.init(outputSlot, false, 90, 32);

        guiItemStacks.set(ingredients);
        if (isLiquid)
            guiFluidStack.set(ingredients);
        super.openRecipe();
    }
}
*/