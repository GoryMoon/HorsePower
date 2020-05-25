package se.gory_moon.horsepower.compat.jei.milling;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.compat.jei.HorsePowerCategory;
import se.gory_moon.horsepower.compat.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.MillingRecipe;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HorsePowerMillingCategory extends HorsePowerCategory<MillingRecipe> {

    private boolean handHandler;

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;
    private static final int secondarySlot = 2;

    private final String localizedName;

    private final IDrawableAnimated arrow;

    public HorsePowerMillingCategory(IGuiHelper guiHelper, boolean hand) {
        super(guiHelper, true, 146, 85, new ResourceLocation(Constants.MOD_ID, "textures/gui/jei_millstone.png"));
        this.handHandler = hand;

        localizedName = handHandler ? Localization.JEI.CATEGORY$MANUAL_MILLING.translate(): Localization.JEI.CATEGORY$MILLING.translate();

        arrow = guiHelper.drawableBuilder(HorsePowerCategory.COMPONENTS, 60, 0, 24, 17)
                .buildAnimated(150, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public ResourceLocation getUid() {
        return handHandler ? HorsePowerPlugin.MANUAL_MILLING: HorsePowerPlugin.MILLING;
    }

    @Override
    public Class<? extends MillingRecipe> getRecipeClass() {
        return MillingRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getIcon() {
        return HorsePowerPlugin.guiHelper.createDrawableIngredient(new ItemStack(handHandler ? Registration.MANUAL_MILLSTONE_BLOCK.get(): Registration.MILLSTONE_BLOCK.get()));
    }

    @Override
    public void setIngredients(MillingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, Stream.of(recipe.getRecipeOutput(), recipe.getSecondaryOutput()).map(stack -> stack.isEmpty() ? null: stack).collect(Collectors.toList()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MillingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 34, 27);
        guiItemStacks.init(outputSlot, false, 90, 27);
        guiItemStacks.init(secondarySlot, false, 90, 50);
        guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == secondarySlot && !ingredient.isEmpty()) {
                tooltip.add(tooltip.size() - 1, "Chance: " + recipe.getSecondaryChance() + "%");
            }
        });

        guiItemStacks.set(ingredients);
        super.openRecipe();
    }

    @Override
    public void draw(MillingRecipe recipe, double mouseX, double mouseY) {
        super.draw(recipe, mouseX, mouseY);

        double printLaps = handHandler ? ((double) recipe.getTime()) / Configs.SERVER.pointsPerRotation.get(): (double) Math.round((recipe.getTime() / 8D) * 100.0D) / 100.0D;
        arrow.draw(57, 27);
        Minecraft.getInstance().fontRenderer.drawStringWithShadow("x" + printLaps, 33, 48, Colors.WHITE.getRGB());
        if (recipe.getSecondaryChance() > 0)
            Minecraft.getInstance().fontRenderer.drawString(recipe.getSecondaryChance() + "%", 65, 58, 0x808080);
    }

    @Override
    public List<String> getTooltipStrings(MillingRecipe recipe, double mouseX, double mouseY) {
        List<String> tooltip = super.getTooltipStrings(recipe, mouseX, mouseY);
        if (mouseX >= 55 && mouseY >= 21 && mouseX < 80 && mouseY < 45) {
            double printLaps = handHandler ? ((double) recipe.getTime()) / Configs.SERVER.pointsPerRotation.get(): (double) Math.round((recipe.getTime() / 8D) * 100.0D) / 100.0D;
            tooltip.add("Time to grind: " + printLaps + (handHandler ? " rotation": " lap") + (printLaps >= 2D ? "s": ""));
        }
        return tooltip;
    }
}
