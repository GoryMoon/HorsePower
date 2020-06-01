package se.gory_moon.horsepower.compat.jei.chopping;

import com.mojang.blaze3d.platform.GlStateManager;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;

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
        //FIXME add localization for the values here
        print(String.valueOf(axeConfiguration.baseChance), 27, 5);
        print(String.valueOf(axeConfiguration.otherChance), 27, 13);
    }
    
    //FONT Stuff
    private static void print(String string, int translateX, int translateY) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(translateX, translateY, 0);
        Minecraft.getInstance().fontRenderer.drawString(string, 0, 0, 8);
        GlStateManager.popMatrix();
    }
}
