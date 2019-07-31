package se.gory_moon.horsepower;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.horsepower.advancements.AdvancementManager;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.PacketHandler;

//"after:crafttweaker;after:theoneprobe;"
@Mod(Reference.MODID)
public class HorsePowerMod {

    public static HorsePowerItemGroup itemGroup;
    public static final Logger LOGGER = LogManager.getLogger();

    //public static ITweakerPlugin tweakerPlugin = new DummyTweakPluginImpl();

    public HorsePowerMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(this::onFingerprintViolation);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configs.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configs.serverSpec);
        eventBus.register(Configs.class);
    }

    public void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
        AdvancementManager.register();

        /*if (Loader.isModLoaded("crafttweaker"))
            tweakerPlugin = new TweakerPluginImpl();

        tweakerPlugin.register();*/
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        //tweakerPlugin.getRemove().forEach(IHPAction::run);
        //tweakerPlugin.getAdd().forEach(IHPAction::run);
    }

    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
