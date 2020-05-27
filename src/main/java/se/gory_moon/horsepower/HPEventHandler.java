package se.gory_moon.horsepower;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import se.gory_moon.horsepower.data.PlankRecipesDataPackGeneratorListener;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Utils;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class HPEventHandler {

    public static Map<ItemStack, Pair<Integer, Integer>> choppingAxes = new HashMap<>();
    public static Map<Integer, Pair<Integer, Integer>> harvestPercentages = new HashMap<>();

    public static void reloadConfig() {
        choppingAxes.clear();
        Configs.SERVER.choppingBlockAxes.get().forEach(s -> {
            String[] data = s.split("=");
            int base = Utils.getBaseAmount(data[1]);
            int chance = Utils.getChance(data[1]);
            ItemStack stack = ItemStack.EMPTY;

            try {
                stack = (ItemStack) Utils.parseItemStack(data[0], false);
            } catch (Exception e) {
                Utils.errorMessage("Parse error with item for custom axes for the chopping block", false);
            }

            if (!stack.isEmpty())
                choppingAxes.put(stack, Pair.of(base, chance));
            else
                Utils.errorMessage("Parse error with item for custom axes for the chopping block", false);
        });

        harvestPercentages.clear();
        Configs.SERVER.harvestablePercentage.get().forEach(s -> {
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
        Utils.sendSavedErrors();
    }

    @SubscribeEvent
    public static void onWorldJoin(EntityJoinWorldEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            if (event.getEntity() instanceof ClientPlayerEntity && event.getWorld() instanceof ClientWorld && Minecraft.getInstance().player != null) {
                Utils.sendSavedErrors();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public static void onToolTip(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty()) {
            List<ITextComponent> tooltipsToAdd = new ArrayList<>();
            StringBuilder part = new StringBuilder();
            if (Configs.CLIENT.showTags.get()) {
                Item item = event.getItemStack().getItem();
                Set<ResourceLocation> tags = item.getTags();

                StringBuilder out = null;
                for (ResourceLocation tag : tags) {
                    if (out == null)
                        out = new StringBuilder(Colors.LIGHTGRAY + "HPTags:\n    " + Colors.ORANGE + tag);
                    else
                        out.append("\n    ").append(tag);
                }
                if (out != null) {
                    for (String s : out.toString().split("\n")) {
                        tooltipsToAdd.add(new StringTextComponent(s));
                    }
                    part = new StringBuilder("HPTags");
                }
            }

            if (Configs.CLIENT.showHarvestLevel.get()) {
                boolean added = false;
                for (String harv : Configs.CLIENT.harvestTypes.get()) {
                    int harvestLevel = event.getItemStack().getItem().getHarvestLevel(event.getItemStack(), ToolType.get(harv), null, null);
                    if (harvestLevel > -1) {
                        tooltipsToAdd.add(new StringTextComponent(Colors.LIGHTGRAY + "HarvestLevel: " + Colors.ORANGE + StringUtils.capitalize(harv) + " (" + harvestLevel + ")"));
                        if (!added) {
                            part.append((part.length() > 0) ? " and ": "").append("HarvestLevel");
                            added = true;
                        }
                    }
                }
            }

            if (!tooltipsToAdd.isEmpty()) {
                tooltipsToAdd.add(new StringTextComponent(Colors.LIGHTGRAY + "The " + part + " tooltip was added by HorsePower, to disabled check the config."));
                if (Screen.hasShiftDown()) {
                    event.getToolTip().addAll(tooltipsToAdd);
                } else {
                    event.getToolTip().add(new StringTextComponent(Colors.LIGHTGRAY + "[Hold shift for more]"));
                }
            }
        }
    }
    
    
    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        //Add Reload Listener for the Plank Recipe Generator
        event.getServer().getResourceManager().addReloadListener(new PlankRecipesDataPackGeneratorListener(event));
    }
    
    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event){
        //Reload to make sure Plank Recipes are available
        HorsePower.LOGGER.info("onServerStarted "+event);
        if(Boolean.TRUE.equals(Configs.SERVER.plankDataPackGeneration.get())){
            event.getServer().reload();
        }
    }
}
