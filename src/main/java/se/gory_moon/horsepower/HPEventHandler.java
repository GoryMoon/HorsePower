package se.gory_moon.horsepower;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.network.messages.SyncServerRecipesMessage;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.util.Utils;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class HPEventHandler {

    public static Map<ItemStack, Pair<Integer, Integer>> choppingAxes = new HashMap<>();
    public static Map<Integer, Pair<Integer, Integer>> harvestPercentages = new HashMap<>();

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MODID)) {
            reloadConfig();
        }
    }

    public static void reloadConfig() {
        ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
        HPRecipes.instance().reloadRecipes();
        choppingAxes.clear();
        Arrays.stream(Configs.general.choppingBlockAxes).forEach(s -> {
            String[] data = s.split("=");
            int base = Utils.getBaseAmount(data[1]);
            int chance = Utils.getChance(data[1]);
            ItemStack stack = ItemStack.EMPTY;

            try {
                stack = (ItemStack) Utils.parseItemStack(data[0], false, false);
            } catch (Exception e) {
                Utils.errorMessage("Parse error with item for custom axes for the chopping block");
            }

            if (!stack.isEmpty())
                choppingAxes.put(stack, Pair.of(base, chance));
        });

        harvestPercentages.clear();
        Arrays.stream(Configs.general.harvestable_percentage).forEach(s -> {
            String[] data = s.split("=");
            try {
                int harvestLevel = Integer.parseInt(data[0]);
                int base = Utils.getBaseAmount(data[1]);
                int chance = Utils.getChance(data[1]);

                harvestPercentages.put(harvestLevel, Pair.of(base, chance));
            } catch (NumberFormatException e) {
                Utils.errorMessage("HarvestLevel config is malformed, make sure only numbers are used as values, (" + s + ")");
            }
        });
    }

    @SubscribeEvent
    public static void onWorldJoin(EntityJoinWorldEvent event) {
        if (FMLCommonHandler.instance().getSide().isClient() && event.getEntity() instanceof EntityPlayerSP && event.getWorld() instanceof WorldClient && FMLClientHandler.instance().getClientPlayerEntity() != null) {
            if (HPRecipes.ERRORS.size() > 0) {
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "HorsePower errors"));
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"));
                HPRecipes.ERRORS.forEach(s -> FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + s).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Loader.instance().getConfigDir() + "/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Changed in in-game config or click to open the config file to fix this"))))));
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + "" + TextFormatting.BOLD + "-----------------------------------------"));
                HPRecipes.ERRORS.clear();
            }
            //HPEventHandler.reloadConfig();
        }
    }

    @SubscribeEvent
    public static void onServerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        if (FMLCommonHandler.instance().getSide().isServer())
            PacketHandler.INSTANCE.sendTo(new SyncServerRecipesMessage(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void onServerLeave(WorldEvent.Unload event) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            NetworkManager manager = FMLClientHandler.instance().getClientToServerNetworkManager();
            if (manager != null && !manager.isLocalChannel() && HPRecipes.serverSyncedRecipes) {
                HPRecipes.serverSyncedRecipes = false;
                HPRecipes.instance().reloadRecipes();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public static void onToolTip(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty()) {
            String part = "";
            if (Configs.misc.showOreDictionaries) {
                StringBuilder out = null;
                for (int id : OreDictionary.getOreIDs(event.getItemStack())) {
                    String s = OreDictionary.getOreName(id);
                    if (out == null) out = new StringBuilder(Colors.LIGHTGRAY + "Ores: " + Colors.ORANGE + s);
                    else out.append(", ").append(s);
                }
                if (out != null) {
                    event.getToolTip().add(out.toString());
                    part = "OreDict";
                }
            }

            if (Configs.misc.showHarvestLevel) {
                boolean added = false;
                for (String harv : Configs.misc.harvestTypes) {
                    int harvestLevel = event.getItemStack().getItem().getHarvestLevel(event.getItemStack(), harv, null, null);
                    if (harvestLevel > -1) {
                        event.getToolTip().add(Colors.LIGHTGRAY + "HarvestLevel: " + Colors.ORANGE + StringUtils.capitalize(harv) + " (" + harvestLevel + ")");
                        if (!added) {
                            part += (!part.isEmpty() ? " and " : "") + "HarvestLevel";
                            added = true;
                        }
                    }
                }
            }

            if (!part.isEmpty()) {
                event.getToolTip().add(Colors.LIGHTGRAY + "The " + part + " tooltip was added by HorsePower, to disabled check the config.");
            }
        }
    }
}
