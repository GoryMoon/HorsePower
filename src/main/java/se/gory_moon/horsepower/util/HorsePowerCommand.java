package se.gory_moon.horsepower.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class HorsePowerCommand extends CommandBase implements IClientCommand {

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
            if ("entity".equals(args[0])) {
                if (sender instanceof EntityPlayerSP) {
                    RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;

                    if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
                        Entity entity = result.entityHit;
                        sender.sendMessage(new TextComponentTranslation("commands.horsepower.entity.has", entity.getClass().getName()));
                        try {
                            StringSelection selection = new StringSelection(entity.getClass().getName());
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                        } catch (Exception ignored){}
                    } else
                        sender.sendMessage(new TextComponentTranslation("commands.horsepower.entity.no"));
                } else
                    throw new CommandException("commands.horsepower.entity.invalid");
                return;
            } else if ("reload".equals(args[0])) {
                throw new CommandException("commands.horsepower.reload");
            }
        }
        throw new WrongUsageException(getUsage(sender));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "entity"): Collections.emptyList();
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }
}
