package se.gory_moon.horsepower.waila;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.nio.charset.Charset;
import java.util.Base64;

public class TTRenderStack implements IWailaTooltipRenderer {
    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(16, 16);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        final int type = Integer.valueOf(params[0]); //0 for block, 1 for item
        final String name = params[1]; //Fully qualified name
        final int amount = Integer.valueOf(params[2]);
        final int meta = Integer.valueOf(params[3]);
        final String nbt = new String(Base64.getDecoder().decode(params[4]), Charset.forName("UTF-8"));

        ItemStack stack = null;
        if (type == 0)
            stack = new ItemStack(Block.REGISTRY.getObject(new ResourceLocation(name)), amount, meta);
        if (type == 1) {
            stack = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(name)), amount, meta);
        }


        try {
            if (!nbt.isEmpty())
                stack = new ItemStack(JsonToNBT.getTagFromJson(nbt));
        } catch (NBTException e) {
            e.printStackTrace();
        }
        RenderHelper.enableGUIStandardItemLighting();
        DisplayUtil.renderStack(0, 0, stack);
        RenderHelper.disableStandardItemLighting();
    }
}
