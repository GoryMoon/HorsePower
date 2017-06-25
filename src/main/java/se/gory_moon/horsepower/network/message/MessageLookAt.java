package se.gory_moon.horsepower.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.horsepower.network.ClientMessageHandler;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class MessageLookAt implements IMessage {

    public MessageLookAt() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler extends ClientMessageHandler<MessageLookAt> {

        @Override
        @SideOnly(Side.CLIENT)
        protected void handle(MessageLookAt message, MessageContext ctx) {
            RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entity = result.entityHit;
                player.sendMessage(new TextComponentTranslation("commands.horsepower.entity.has", entity.getClass().getName()));
                try {
                    StringSelection selection = new StringSelection(entity.getClass().getName());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                } catch (Exception ignored){}
            } else
                player.sendMessage(new TextComponentTranslation("commands.horsepower.entity.no"));
        }
    }
}
