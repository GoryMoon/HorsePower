package se.gory_moon.horsepower.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import se.gory_moon.horsepower.network.messages.EntityMessage;
import se.gory_moon.horsepower.util.Constants;

import java.util.Objects;

public class PacketHandler {

    public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(Constants.NET_ID)
            .clientAcceptedVersions(s -> Objects.equals(s, "1"))
            .serverAcceptedVersions(s -> Objects.equals(s, "1"))
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void init() {
        INSTANCE.messageBuilder(EntityMessage.class, 0)
                .encoder(EntityMessage::encode)
                .decoder(EntityMessage::decode)
                .consumer(EntityMessage::handle)
                .add();
    }

}
