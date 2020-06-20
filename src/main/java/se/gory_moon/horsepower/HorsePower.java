package se.gory_moon.horsepower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.gory_moon.horsepower.advancements.AdvancementManager;
import se.gory_moon.horsepower.compat.top.TOPCompatibility;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.HorsePowerCommand;

//"after:crafttweaker;"
@Mod(Constants.MOD_ID)
public class HorsePower {

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(Constants.MOD_ID).itemGroup(HorsePowerItemGroup::new));
    public static final Logger LOGGER = LogManager.getLogger();

    //public static ITweakerPlugin tweakerPlugin = new DummyTweakPluginImpl();

    public HorsePower() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(this::onFingerprintViolation);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        Registration.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configs.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configs.serverSpec);
        eventBus.register(Configs.class);
    }

    public static Registrate getRegistrate() {
        return REGISTRATE.getValue();
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
        AdvancementManager.register();

        if (ModList.get().isLoaded("theoneprobe")) {
            TOPCompatibility.register();
        }
        /*if (Loader.isModLoaded("crafttweaker"))
            tweakerPlugin = new TweakerPluginImpl();

        tweakerPlugin.register();*/
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        //tweakerPlugin.getRemove().forEach(IHPAction::run);
        //tweakerPlugin.getAdd().forEach(IHPAction::run);
    }

    private void serverStarting(FMLServerStartingEvent event) {
        new HorsePowerCommand(event.getCommandDispatcher());
    }

    private void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }  
}
