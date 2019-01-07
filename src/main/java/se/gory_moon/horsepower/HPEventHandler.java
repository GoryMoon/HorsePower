package se.gory_moon.horsepower;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.network.NetworkDirection;
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
            Utils.sendSavedErrors();
        }
    }

    public static void reloadConfig() {
        //ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
        HPRecipes.instance().reloadRecipes();
        choppingAxes.clear();
        Arrays.stream(Configs.general.choppingBlockAxes).forEach(s -> {
            String[] data = s.split("=");
            int base = Utils.getBaseAmount(data[1]);
            int chance = Utils.getChance(data[1]);
            ItemStack stack = ItemStack.EMPTY;

            /*try {
                stack = (ItemStack) Utils.parseItemStack(data[0], false, false);
            } catch (Exception e) {
                Utils.errorMessage("Parse error with item for custom axes for the chopping block", false);
            }*/

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
                Utils.errorMessage("HarvestLevel config is malformed, make sure only numbers are used as values, (" + s + ")", false);
            }
        });
    }

    @SubscribeEvent
    public static void onWorldJoin(EntityJoinWorldEvent event) {
        /*if (FMLCommonHandler.instance().getSide().isClient() && event.getEntity() instanceof EntityPlayerSP && event.getWorld() instanceof WorldClient && FMLClientHandler.instance().getClientPlayerEntity() != null) {
            Utils.sendSavedErrors();
            //HPEventHandler.reloadConfig();
        }*/
    }

    @SubscribeEvent
    public static void onServerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> PacketHandler.INSTANCE.sendTo(new SyncServerRecipesMessage(), ((EntityPlayerMP)event.player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT));
    }

    @SubscribeEvent
    public static void onServerLeave(WorldEvent.Unload event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            NetworkManager manager = Minecraft.getInstance().getConnection().getNetworkManager();
            if (manager != null && !manager.isLocalChannel() && HPRecipes.serverSyncedRecipes) {
                HPRecipes.serverSyncedRecipes = false;
                HPRecipes.instance().reloadRecipes();
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public static void onToolTip(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty()) {
            String part = "";
            /*if (Configs.misc.showOreDictionaries) {
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
            }*/

            if (Configs.misc.showHarvestLevel) {
                boolean added = false;
                for (String harv : Configs.misc.harvestTypes) {
                    int harvestLevel = event.getItemStack().getItem().getHarvestLevel(event.getItemStack(), ToolType.get(harv), null, null);
                    if (harvestLevel > -1) {
                        event.getToolTip().add(new TextComponentString(Colors.LIGHTGRAY + "HarvestLevel: " + Colors.ORANGE + StringUtils.capitalize(harv) + " (" + harvestLevel + ")"));
                        if (!added) {
                            part += (!part.isEmpty() ? " and " : "") + "HarvestLevel";
                            added = true;
                        }
                    }
                }
            }

            if (!part.isEmpty()) {
                event.getToolTip().add(new TextComponentString(Colors.LIGHTGRAY + "The " + part + " tooltip was added by HorsePower, to disabled check the config."));
            }
        }
    }
}
