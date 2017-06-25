package se.gory_moon.horsepower.network;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class ServerMessageHandler<R extends IMessage> implements IMessageHandler<R, IMessage> {

    @Override
    public IMessage onMessage(final R message, final MessageContext ctx) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    protected abstract void handle(R message, MessageContext ctx);

}
