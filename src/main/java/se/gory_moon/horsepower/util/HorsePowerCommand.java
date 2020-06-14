package se.gory_moon.horsepower.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import se.gory_moon.horsepower.network.PacketHandler;
import se.gory_moon.horsepower.network.messages.EntityMessage;

public class HorsePowerCommand {

    public HorsePowerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("horsepower")
                .then(registerEntity())
        );
    }

    private ArgumentBuilder<CommandSource, ?> registerEntity()
    {
        return Commands.literal("entity")
                .requires(cs -> cs.hasPermissionLevel(2)) //permission
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().asPlayer();
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new EntityMessage());
                    return 0;
                });
    }
}
