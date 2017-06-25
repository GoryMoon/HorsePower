package se.gory_moon.horsepower.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.message.MessageLookAt;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL);

    public static void init() {
        INSTANCE.registerMessage(MessageLookAt.Handler.class, MessageLookAt.class, 0, Side.CLIENT);
    }

}
