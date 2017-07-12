package se.gory_moon.horsepower.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.network.ClientMessageHandler;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SyncServerRecipesMessage extends ClientMessageHandler<SyncServerRecipesMessage> implements IMessage {

    private List<String> grindstoneRecipes = new ArrayList<>();
    private List<String> handGrindstoneRecipes = new ArrayList<>();
    private List<String> choppingRecipes = new ArrayList<>();

    public SyncServerRecipesMessage() {
        grindstoneRecipes = Arrays.stream(Configs.grindstoneRecipes).collect(Collectors.toList());
        handGrindstoneRecipes = Arrays.stream(Configs.handGrindstoneRecipes).collect(Collectors.toList());
        choppingRecipes = Arrays.stream(Configs.choppingRecipes).collect(Collectors.toList());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
            grindstoneRecipes.add(ByteBufUtils.readUTF8String(buf));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            grindstoneRecipes.add(ByteBufUtils.readUTF8String(buf));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            grindstoneRecipes.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(grindstoneRecipes.size());
        grindstoneRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
        buf.writeInt(handGrindstoneRecipes.size());
        handGrindstoneRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
        buf.writeInt(choppingRecipes.size());
        choppingRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
    }

    @Override
    protected void handle(SyncServerRecipesMessage message, MessageContext ctx) {
        HPRecipes.serverSyncedRecipes = true;
        HPRecipes.instance().reloadRecipes(message.grindstoneRecipes, message.handGrindstoneRecipes, message.choppingRecipes);
        HorsePowerMod.logger.info("Synced recipes from server");
    }
}
