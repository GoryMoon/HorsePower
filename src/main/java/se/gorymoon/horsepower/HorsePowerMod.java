package se.gorymoon.horsepower;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gorymoon.horsepower.blocks.ModBlocks;
import se.gorymoon.horsepower.items.ModItems;
import se.gorymoon.horsepower.lib.Reference;
import se.gorymoon.horsepower.proxy.CommonProxy;
import se.gorymoon.horsepower.recipes.MillRecipes;
import se.gorymoon.horsepower.tweaker.DummyTweakPluginImpl;
import se.gorymoon.horsepower.tweaker.ITweakerPlugin;
import se.gorymoon.horsepower.tweaker.TweakerPluginImpl;

import javax.annotation.Nullable;
import java.util.List;

@Mod(modid = Reference.MODID, version = Reference.VERSION, name = Reference.NAME, acceptedMinecraftVersions = "[1.11.2]")
@EventBusSubscriber
public class HorsePowerMod {

    @Instance(Reference.MODID)
    public static HorsePowerMod instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy proxy;

    public static ITweakerPlugin tweakerPlugin;
    public static Logger logger = LogManager.getLogger("HorsePower");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        ModBlocks.registerTileEntities();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModItems.registerRecipes();
    }

    @EventHandler
    public void loaded(FMLLoadCompleteEvent event) {
        if (Loader.isModLoaded("crafttweaker")) {
            tweakerPlugin = new TweakerPluginImpl();
        } else {
            tweakerPlugin = new DummyTweakPluginImpl();
        }
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        MillRecipes.instance().reloadRecipes();
        event.registerServerCommand(new CommandBase() {
            @Override
            public String getName() {
                return "horsepower";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return "/horsepower reload";
            }

            @Override
            public int getRequiredPermissionLevel()
            {
                return 2;
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length == 1 && "reload".equals(args[0])) {
                    ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
                    MillRecipes.instance().reloadRecipes();
                    sender.sendMessage(new TextComponentString("HorsePower configs reloaded"));
                } else {
                    throw new WrongUsageException("/horsepower reload");
                }
            }

            @Override
            public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
                return args.length == 1 ? Lists.asList("reload", new String[0]): Lists.<String>newArrayList();
            }
        });
    }
}
