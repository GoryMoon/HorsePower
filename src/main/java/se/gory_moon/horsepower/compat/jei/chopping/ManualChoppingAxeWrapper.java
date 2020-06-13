package se.gory_moon.horsepower.compat.jei.chopping;

import com.mojang.blaze3d.platform.GlStateManager;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import se.gory_moon.horsepower.util.Localization;

public class ManualChoppingAxeWrapper implements IRecipeCategoryExtension {

    private ManualChoppingAxeConfiguration axeConfiguration;
    
    public ManualChoppingAxeWrapper(ManualChoppingAxeConfiguration axeConfig) {
       this.axeConfiguration = axeConfig;
    }
    
    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, axeConfiguration.axeItem);
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, double mouseX, double mouseY) {
        print(Localization.INFO.MANUAL_CHOPPING_AXES_BASE_AMOUNT.translate() + String.valueOf(axeConfiguration.baseAmount) + "%", 27, 3);
        print(Localization.INFO.MANUAL_CHOPPING_AXES_ADDITIONAL_CHANCE.translate() + String.valueOf(axeConfiguration.additionalChance) + "%", 27, 12);
    }
    
    //FONT Stuff
    private static void print(String string, int translateX, int translateY) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(translateX, translateY, 0);
        Minecraft.getInstance().fontRenderer.drawString(string, 0, 0, 8);
        GlStateManager.popMatrix();
    }
}
