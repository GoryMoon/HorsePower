package se.gory_moon.horsepower.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.network.messages.SyncServerRecipesMessage;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {
    private static ResourceLocation id = new ResourceLocation(Reference.MODID, "net");

    public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(id)
            .clientAcceptedVersions(s -> Objects.equals(s, "1"))
            .serverAcceptedVersions(s -> Objects.equals(s, "1"))
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    private static <MSG> void register(int id, Class<MSG> clazz, Function<PacketBuffer, MSG> decode, BiConsumer<MSG, PacketBuffer> encode, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        INSTANCE.messageBuilder(clazz, id).decoder(decode).encoder(encode).consumer(consumer).add();
    }

    public static void init() {
        register(0, SyncServerRecipesMessage.class, SyncServerRecipesMessage::fromBytes, SyncServerRecipesMessage::toBytes, SyncServerRecipesMessage::handle);

    }

}
