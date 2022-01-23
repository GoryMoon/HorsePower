package se.gory_moon.horsepower.compat.jei.chopping;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import se.gory_moon.horsepower.util.Localization;

public class ManualChoppingAxeWrapper implements IRecipeCategoryExtension {

    private final ManualChoppingAxeConfiguration axeConfiguration;

    public ManualChoppingAxeWrapper(ManualChoppingAxeConfiguration axeConfig) {
        this.axeConfiguration = axeConfig;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, axeConfiguration.axeItem);
    }

    //FONT Stuff
    private static void print(String string, MatrixStack matrixStack, int translateX, int translateY) {
        matrixStack.push();
        matrixStack.translate(translateX, translateY, 0);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, string, 0, 0, 8);
        matrixStack.pop();
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
        print(Localization.INFO.MANUAL_CHOPPING_AXES_BASE_AMOUNT.translate() + axeConfiguration.baseAmount + "%", matrixStack, 27, 3);
        print(Localization.INFO.MANUAL_CHOPPING_AXES_ADDITIONAL_CHANCE.translate() + axeConfiguration.additionalChance + "%", matrixStack, 27, 12);
    }
}
