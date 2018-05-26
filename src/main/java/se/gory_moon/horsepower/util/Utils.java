package se.gory_moon.horsepower.util;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.BlockHPChoppingBase;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.ShapedChoppingRecipe;
import se.gory_moon.horsepower.recipes.ShapelessChoppingRecipe;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static ArrayList<Class<? extends EntityCreature>> getCreatureClasses() {
        ArrayList<Class<? extends EntityCreature>> clazzes = Lists.newArrayList();
        if (Configs.general.useHorseInterface)
            clazzes.add(AbstractHorse.class);

        for (String e: Configs.general.grindstoneMobList) {
            try {
                Class clazz = Class.forName(e);

                if (EntityCreature.class.isAssignableFrom(clazz)) {
                    clazzes.add(clazz);
                } else {
                    HorsePowerMod.logger.error("Error in config, the mob (" + e + ") can't be leashed");
                }
            } catch (ClassNotFoundException e1) {
                HorsePowerMod.logger.error("Error in config, could not find (" + e + ") mob class, mod for entity might not be installed");
            }
        }
        return clazzes;
    }

    public static int getItemStackHashCode(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
        tag.removeTag("Count");
        tag.removeTag("Damage");
        return tag.hashCode();
    }

    public static int getItemStackCountHashCode(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
        tag.removeTag("Damage");
        return tag.hashCode();

    }

    public static int getBaseAmount(String in) {
        try {
            return Integer.parseInt(in.split("-")[0]);
        } catch (NumberFormatException e) {
            errorMessage("Base amount for chopping axe is malformed, (" + in + ")", false);
        }
        return 0;
    }

    public static int getChance(String in) {
        try {
            return Integer.parseInt(in.split("-")[1]);
        } catch (NumberFormatException e) {
            errorMessage("Chance for chopping axe is malformed, (" + in + ")", false);
        }
        return 0;
    }

    public static void errorMessage(String message, boolean showDirectly) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (FMLClientHandler.instance().getClientPlayerEntity() != null && showDirectly)
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + message).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Loader.instance().getConfigDir() + "/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Change in in-game config or click to open the config file to fix this")))));
            else
                HPRecipes.ERRORS.add(message);
        }
        HorsePowerMod.logger.warn(message);
    }

    public static void sendSavedErrors() {
        if (FMLCommonHandler.instance().getSide().isClient() && FMLClientHandler.instance().getClientPlayerEntity() != null && HPRecipes.ERRORS.size() > 0) {
            FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "HorsePower config errors"));
            FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"));
            HPRecipes.ERRORS.forEach(s -> FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + s).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Loader.instance().getConfigDir() + "/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Changed in in-game config or click to open the config file to fix this"))))));
            FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"));
            HPRecipes.ERRORS.clear();
        }
    }

    public static Object parseItemStack(String item, boolean acceptOre, boolean acceptAmount) throws Exception {
        String[] data = item.split("\\$");
        NBTTagCompound nbt = data.length == 1 ? null: JsonToNBT.getTagFromJson(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("$"));

        data = item.split("@");
        int amount = !acceptAmount || data.length == 1 ? 1: Integer.parseInt(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("@"));

        data = item.split(":");
        int meta = data.length == 2 ? 0 : "*".equals(data[2]) ? OreDictionary.WILDCARD_VALUE: Integer.parseInt(data[2]);

        if (item.startsWith("ore:")) {
            if (!acceptOre)
                throw new InvalidParameterException();
            if (amount > 1) {
                return OreDictionary.getOres(item.substring(4)).stream().map(stack -> {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(amount);
                    return stack1;
                }).collect(Collectors.toList());
            } else
                return OreDictionary.getOres(item.substring(4));
        } else {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("id", data[0] + ":" + data[1]);
            compound.setByte("Count", (byte) amount);
            compound.setShort("Damage", (short) meta);
            if (nbt != null)
                compound.setTag("tag", nbt);
            return new ItemStack(compound);
        }
    }

    public static List<ItemStack> getCraftingItems(BlockHPChoppingBase block) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        main: for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe instanceof ShapedChoppingRecipe) {
                if (((ShapedChoppingRecipe) recipe).getSimpleRecipeOutput().getItem() instanceof ItemBlock && ((ItemBlock) ((ShapedChoppingRecipe) recipe).getSimpleRecipeOutput().getItem()).getBlock() == block) {
                    for (ItemStack stack : ((ShapedChoppingRecipe) recipe).outputBlocks) {
                        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockHPChoppingBase) {
                            Block.getBlockFromItem(stack.getItem()).getSubBlocks(null, stacks);
                            continue main;
                        }
                    }
                    stacks.addAll(((ShapedChoppingRecipe) recipe).outputBlocks);
                }
            } else if (recipe instanceof ShapelessChoppingRecipe) {
                if (((ShapelessChoppingRecipe) recipe).getSimpleRecipeOutput().getItem() instanceof ItemBlock && ((ItemBlock) ((ShapelessChoppingRecipe) recipe).getSimpleRecipeOutput().getItem()).getBlock() == block) {
                    for (ItemStack stack : ((ShapelessChoppingRecipe) recipe).outputBlocks) {
                        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockHPChoppingBase) {
                            Block.getBlockFromItem(stack.getItem()).getSubBlocks(null, stacks);
                            continue main;
                        }
                    }
                    stacks.addAll(((ShapelessChoppingRecipe) recipe).outputBlocks);
                }
            }
        }
        return stacks;
    }
}
