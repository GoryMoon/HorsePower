package se.gory_moon.horsepower.util;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.network.message.MessageLookAt;
import se.gory_moon.horsepower.recipes.HPRecipes;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class HorsePowerCommand extends CommandBase {

    @Override
    public String getName() {
        return "horsepower";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.horsepower.usage";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("hp");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            if ("reload".equals(args[0])) {
                ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
                HPRecipes.instance().reloadRecipes(sender);
                notifyCommandListener(sender, this, "commands.horsepower.reload");
                return;
            } else if ("entity".equals(args[0])) {
                if (sender instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) sender;
                    PacketHandler.INSTANCE.sendTo(new MessageLookAt(), player);
                } else
                    throw new CommandException("commands.horsepower.entity.invalid");
                return;
            }
        }
        throw new WrongUsageException(getUsage(sender));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "entity", "reload"): Collections.emptyList();
    }
}
