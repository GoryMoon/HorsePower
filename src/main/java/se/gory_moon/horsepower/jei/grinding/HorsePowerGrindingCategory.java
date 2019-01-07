package se.gory_moon.horsepower.jei.grinding;
/*
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.util.Localization;


public class HorsePowerGrindingCategory extends HorsePowerCategory<GrindstoneRecipeWrapper> {

    private boolean handHandler;

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;
    private static final int secondarySlot = 2;

    private final String localizedName;

    public HorsePowerGrindingCategory(IGuiHelper guiHelper, boolean hand) {
        super(guiHelper, true, 146, 85, new ResourceLocation("horsepower", "textures/gui/jei_grindstone.png"));
        this.handHandler = hand;

        localizedName = handHandler ? Localization.GUI.CATEGORY_HAND_GRINDING.translate(): Localization.GUI.CATEGORY_GRINDING.translate();
    }

    @Override
    public String getUid() {
        return handHandler ? HorsePowerPlugin.HAND_GRINDING : HorsePowerPlugin.GRINDING;
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
    public void setRecipe(IRecipeLayout recipeLayout, GrindstoneRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 34, 27);
        guiItemStacks.init(outputSlot, false, 90, 27);
        guiItemStacks.init(secondarySlot, false, 90, 50);
        guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == secondarySlot && !ingredient.isEmpty()) {
                tooltip.add(tooltip.size()-1,  "Chance: " + recipeWrapper.getSecondaryChance() + "%");
            }
        });

        guiItemStacks.set(ingredients);
        super.openRecipe();
    }
}
*/