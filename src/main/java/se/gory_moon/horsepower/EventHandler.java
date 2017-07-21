package se.gory_moon.horsepower;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.network.messages.SyncServerRecipesMessage;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class EventHandler {

    public static List<ItemStack> choppingAxes = new ArrayList();

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MODID)) {
            ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
            HPRecipes.instance().reloadRecipes();
            choppingAxes = Arrays.stream(Configs.general.choppingBlockAxes).map(s -> {
                String[] data = s.split(":");
                if (data.length >= 2) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(data[0] + ":" + data[1]));
                    if (item != null) {
                        int meta = 0;
                        if (data.length == 3)
                            meta = Integer.parseInt(data[2]);
                        return new ItemStack(item, 1, meta);
                    }
                }
                return ItemStack.EMPTY;
            }).collect(Collectors.toList());
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(EntityJoinWorldEvent event) {
        if (FMLCommonHandler.instance().getSide().isClient() && event.getEntity() instanceof EntityPlayerSP && event.getWorld() instanceof WorldClient && FMLClientHandler.instance().getClientPlayerEntity() != null && HPRecipes.ERRORS.size() > 0) {
            HPRecipes.ERRORS.forEach(s -> FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED +s)));
            HPRecipes.ERRORS.clear();
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
}
