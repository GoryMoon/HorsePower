package se.gory_moon.horsepower.network;

import java.util.Objects;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import se.gory_moon.horsepower.network.messages.EntityMessage;
import se.gory_moon.horsepower.util.Constants;

public class PacketHandler {

    public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(Constants.NET_ID)
            .clientAcceptedVersions(s -> Objects.equals(s, "1"))
            .serverAcceptedVersions(s -> Objects.equals(s, "1"))
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    //No packets needed for now
    public static void init() {
        INSTANCE.registerMessage(0, EntityMessage.class, EntityMessage::encode, EntityMessage::decode, EntityMessage::handle);
    }

}
