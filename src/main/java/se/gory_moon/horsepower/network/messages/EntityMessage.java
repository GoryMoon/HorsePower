package se.gory_moon.horsepower.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityMessage {

    public void encode(PacketBuffer packetBuffer) {}

    public static EntityMessage decode(PacketBuffer packetBuffer) {
        return new EntityMessage();
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            RayTraceResult result = mc.objectMouseOver;

            if (result != null && result.getType() == RayTraceResult.Type.ENTITY) {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
                String name = entity.getClass().getName();
                mc.player.sendMessage(new TranslationTextComponent("commands.horsepower.entity.has", name), Util.DUMMY_UUID);
                mc.keyboardListener.setClipboardString(name);
            } else
                mc.player.sendMessage(new TranslationTextComponent("commands.horsepower.entity.no"), Util.DUMMY_UUID);
        });
        context.get().setPacketHandled(true);
    }
}
