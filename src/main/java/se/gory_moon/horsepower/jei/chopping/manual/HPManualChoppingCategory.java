package se.gory_moon.horsepower.jei.chopping.manual;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.TileEntityManualChopper;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.List;

public class HPManualChoppingCategory implements IRecipeCategory<ManualChoppingRecipeWrapper> {

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;
    private static final int axeSlot = 2;

    private final String localizedName;
    protected IDrawable background;

    public HPManualChoppingCategory(IGuiHelper guiHelper) {
        final ResourceLocation location = new ResourceLocation("horsepower", "textures/gui/jei_manual_chopping.png");
        background = guiHelper.createDrawable(location, 0, 0, 78, 44);
        localizedName = Localization.GUI.CATEGORY_MANUAL_CHOPPING.translate();
    }

    @Override
    public String getUid() {
        return HorsePowerPlugin.MANUAL_CHOPPING;
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
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ManualChoppingRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == axeSlot) {
                int base = TileEntityManualChopper.getBaseAmount(ingredient, null);
                int chance = TileEntityManualChopper.getChance(ingredient, null);
                tooltip.add(Colors.LIGHTGRAY + Localization.GUI.JEI.MANUAL_CHOPPING_DESC_1.translate(Colors.WHITE.toString() + base));
                tooltip.add(Colors.LIGHTGRAY + Localization.GUI.JEI.MANUAL_CHOPPING_DESC_2.translate(Colors.WHITE.toString() + chance));
                tooltip.add(Colors.LIGHTGRAY + "\n" + Localization.GUI.JEI.MANUAL_CHOPPING_DESC_3.translate(Colors.WHITE, Colors.LIGHTGRAY));
            }
        });
        guiItemStacks.setOverrideDisplayFocus(null);
        guiItemStacks.init(inputSlot, true, 0, 22);
        guiItemStacks.init(outputSlot, false, 56, 22);
        guiItemStacks.init(axeSlot, true, 26, 0);

        IFocus<?> focus = recipeLayout.getFocus();
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

        IStackHelper stackHelper = HorsePowerPlugin.jeiHelpers.getStackHelper();
        if (focus != null && focus.getValue() instanceof ItemStack) {
            ItemStack stack = (ItemStack) focus.getValue();

            if (focus.getMode() == IFocus.Mode.INPUT) {
                int index = -1;
                final List<ItemStack> stacks = inputs.get(1);
                for (int i = 0; i < stacks.size(); i++) {
                    if (stackHelper.isEquivalent(stacks.get(i), stack)) {
                        index = i;
                        break;
                    }
                }
                if (index > -1) {
                    inputs.get(1).removeIf(itemStack -> !stackHelper.isEquivalent(stack, itemStack));
                    final ItemStack output = outputs.get(0).get(index);
                    outputs.get(0).removeIf(itemStack -> output != itemStack);
                }
            }
        }

        guiItemStacks.set(inputSlot, inputs.get(0));
        guiItemStacks.set(axeSlot, inputs.get(1));
        guiItemStacks.set(outputSlot, outputs.get(0));
    }
}
