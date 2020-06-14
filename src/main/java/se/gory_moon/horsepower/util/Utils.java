package se.gory_moon.horsepower.util;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePower;

public class Utils {

    public static ArrayList<String> ERRORS = Lists.newArrayList();

    public static ArrayList<Class<? extends CreatureEntity>> getCreatureClasses() {
        ArrayList<Class<? extends CreatureEntity>> clazzes = Lists.newArrayList();
        if (Configs.SERVER.useHorseInterface.get().booleanValue())
            clazzes.add(AbstractHorseEntity.class);

        for (String e : Configs.SERVER.mobList.get()) {
            try {
                Class clazz = Class.forName(e);

                if (CreatureEntity.class.isAssignableFrom(clazz)) {
                    clazzes.add(clazz);
                } else {
                    HorsePower.LOGGER.error("Error in config, the mob (" + e + ") can't be leashed");
                }
            } catch (ClassNotFoundException ignored) {}
        }
        return clazzes;
    }

    public static int getItemStackHashCode(ItemStack stack) {
        if (stack.isEmpty())
            return 0;

        CompoundNBT tag = stack.write(new CompoundNBT());
        tag.remove("Count");
        return tag.hashCode();
    }

    public static int getItemStackCountHashCode(ItemStack stack) {
        if (stack.isEmpty())
            return 0;

        CompoundNBT tag = stack.write(new CompoundNBT());
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
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            errorMessage("Chance for chopping axe is malformed, (" + in + ")", false);
        }
        return 0;
    }

    public static void errorMessage(String message, boolean showDirectly) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().player != null && showDirectly)
                Minecraft.getInstance().player.sendMessage(new StringTextComponent(TextFormatting.RED + message).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Minecraft.getInstance().gameDir + "/config/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Change in in-game config or click to open the config file to fix this")))));
            else
                ERRORS.add(message);
        });
        HorsePower.LOGGER.warn(message);
    }

    public static void sendSavedErrors() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().player != null && ERRORS.size() > 0) {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "HorsePower config errors"));
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"));
                ERRORS.forEach(s -> player.sendMessage(new StringTextComponent(TextFormatting.RED + s).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Minecraft.getInstance().gameDir + "/config/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Changed in in-game config or click to open the config file to fix this"))))));
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"));
                ERRORS.clear();
            }
        });
    }

    public static Object parseItemStack(String incomingItem, boolean acceptAmount) throws Exception {
        String item = incomingItem;
        String[] data = item.split("\\$");
        CompoundNBT nbt = data.length == 1 ? null: JsonToNBT.getTagFromJson(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("$"));

        data = item.split("@");
        int amount = !acceptAmount || data.length == 1 ? 1: Integer.parseInt(data[1]);

        data = item.split(":");
        CompoundNBT compound = new CompoundNBT();
        compound.putString("id", data[0] + ":" + data[1]);
        compound.putByte("Count", (byte) amount);
        if (nbt != null)
            compound.put("tag", nbt);
        return ItemStack.read(compound);
    }
/*
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
    }*/
    
    private Utils() {
        // hidden
    }
}
