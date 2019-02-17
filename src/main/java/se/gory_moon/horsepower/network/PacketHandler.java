package se.gory_moon.horsepower.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.messages.SyncServerRecipesMessage;

import java.util.Objects;

public class PacketHandler {
    private static ResourceLocation id = new ResourceLocation(Reference.MODID, "net");

    public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(id)
            .clientAcceptedVersions(s -> Objects.equals(s, "1"))
            .serverAcceptedVersions(s -> Objects.equals(s, "1"))
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void init() {
        INSTANCE.registerMessage(0, SyncServerRecipesMessage.class, SyncServerRecipesMessage::encode, SyncServerRecipesMessage::decode, SyncServerRecipesMessage::handle);
    }

}
