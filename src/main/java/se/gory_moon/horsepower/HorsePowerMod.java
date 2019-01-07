package se.gory_moon.horsepower;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.client.ModModelManager;
import se.gory_moon.horsepower.items.ModItems;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.proxy.ClientProxy;
import se.gory_moon.horsepower.proxy.CommonProxy;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.util.Utils;

//"after:crafttweaker;after:jei;after:waila;after:theoneprobe;"
@Mod(Reference.MODID)
public class HorsePowerMod {

    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static HorsePowerCreativeTab creativeTab = new HorsePowerCreativeTab();
    //public static ITweakerPlugin tweakerPlugin = new DummyTweakPluginImpl();
    public static Logger logger = LogManager.getLogger("HorsePower");

    public HorsePowerMod() {
        FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLModLoadingContext.get().getModEventBus().addListener(this::serverLoad);
        FMLModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);

        MinecraftForge.EVENT_BUS.register(ModItems.RegistrationHandler.class);
        MinecraftForge.EVENT_BUS.register(ModBlocks.RegistrationHandler.class);
        MinecraftForge.EVENT_BUS.register(HPEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ModModelManager.class);
    }

    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        PacketHandler.init();

        //FMLInterModComms.sendMessage("waila", "register", Reference.WAILA_PROVIDER);

        /*if (Loader.isModLoaded("crafttweaker"))
            tweakerPlugin = new TweakerPluginImpl();

        tweakerPlugin.register();*/
    }

    public void init(FMLInitializationEvent event) {
        proxy.init();
        ModItems.registerRecipes();
    }

    public void loadComplete(FMLPostInitializationEvent event) {
        //tweakerPlugin.getRemove().forEach(IHPAction::run);
        //tweakerPlugin.getAdd().forEach(IHPAction::run);

        HPEventHandler.reloadConfig();
        proxy.loadComplete();
    }

    public void serverLoad(FMLServerAboutToStartEvent event) {
        HPRecipes.instance().reloadRecipes();
        Utils.sendSavedErrors();
    }

    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        logger.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

}
