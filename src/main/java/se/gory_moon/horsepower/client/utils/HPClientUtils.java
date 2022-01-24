package se.gory_moon.horsepower.client.utils;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public final class HPClientUtils {

    public static ArrayList<String> ERRORS = Lists.newArrayList();

    private HPClientUtils() {}

    public static BiConsumer<String, Boolean> errorMessageConsumer() {
        return (message, showDirectly) -> {
            if (Minecraft.getInstance().player != null && showDirectly)
                Minecraft.getInstance().player.sendMessage(new StringTextComponent(TextFormatting.RED + message).setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Minecraft.getInstance().gameDir + "/config/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Change in in-game config or click to open the config file to fix this")))), Util.DUMMY_UUID);
            else
                ERRORS.add(message);
        };
    }

    public static void sendSavedErrors() {
        if (Minecraft.getInstance().player != null && ERRORS.size() > 0) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "HorsePower config errors"), Util.DUMMY_UUID);
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"), Util.DUMMY_UUID);
            ERRORS.forEach(s -> player.sendMessage(new StringTextComponent(TextFormatting.RED + s).setStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Minecraft.getInstance().gameDir + "/config/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Changed in in-game config or click to open the config file to fix this")))), Util.DUMMY_UUID));
            player.sendMessage(new StringTextComponent(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"), Util.DUMMY_UUID);
            ERRORS.clear();
        }
    }
}
