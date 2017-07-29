package se.gory_moon.horsepower;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.items.ModItems;
import se.gory_moon.horsepower.jei.DummyJeiPlugin;
import se.gory_moon.horsepower.jei.IJeiPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.proxy.CommonProxy;
import se.gory_moon.horsepower.tweaker.DummyTweakPluginImpl;
import se.gory_moon.horsepower.tweaker.ITweakerPlugin;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;

@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME, acceptedMinecraftVersions = "[1.11.2]", dependencies = "after:crafttweaker;after:jei;after:waila;after:theoneprobe;")
@EventBusSubscriber
public class HorsePowerMod {

    @Instance(Reference.MODID)
    public static HorsePowerMod instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy proxy;

    public static HorsePowerCreativeTab creativeTab = new HorsePowerCreativeTab();
    public static ITweakerPlugin tweakerPlugin;
    public static IJeiPlugin jeiPlugin = new DummyJeiPlugin();
    public static Logger logger = LogManager.getLogger("HorsePower");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        PacketHandler.init();

        FMLInterModComms.sendMessage("waila", "register", Reference.WAILA_PROVIDER);

        ModBlocks.registerTileEntities();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModItems.registerRecipes();

        if (Loader.isModLoaded("crafttweaker")) {
            tweakerPlugin = new TweakerPluginImpl();
            tweakerPlugin.register();
        } else
            tweakerPlugin = new DummyTweakPluginImpl();

        OreDictionary.registerOre("seed", Items.WHEAT_SEEDS);
        OreDictionary.registerOre("seed", Items.PUMPKIN_SEEDS);
        OreDictionary.registerOre("seed", Items.MELON_SEEDS);
        OreDictionary.registerOre("seed", Items.BEETROOT_SEEDS);

        HPEventHandler.reloadConfig();
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete();
    }

}
