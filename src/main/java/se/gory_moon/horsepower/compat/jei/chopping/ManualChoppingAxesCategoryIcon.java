package se.gory_moon.horsepower.compat.jei.chopping;

import com.mojang.blaze3d.platform.GlStateManager;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import se.gory_moon.horsepower.Registration;

public class ManualChoppingAxesCategoryIcon implements IDrawable {
    @Override
    public int getWidth() {
        return 16;
    }
    
    @Override
    public int getHeight() {
        return 16;
    }
    
    @Override
    public void draw(int xOffset, int yOffset) {
        GlStateManager.disableDepthTest();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderHelper.enableStandardItemLighting();
        itemRenderer.renderItemAndEffectIntoGUI(null, new ItemStack(Registration.MANUAL_CHOPPER_BLOCK.get()), xOffset, yOffset);
        itemRenderer.renderItemAndEffectIntoGUI(null, new ItemStack(Items.IRON_AXE), xOffset, yOffset);
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
    }

}
