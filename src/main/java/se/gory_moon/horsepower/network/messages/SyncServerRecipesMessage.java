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
    private List<String> manualChoppingRecipes = new ArrayList<>();
    private List<String> pressRecipes = new ArrayList<>();

    public SyncServerRecipesMessage() {
        grindstoneRecipes = Arrays.stream(Configs.recipes.grindstoneRecipes).collect(Collectors.toList());
        handGrindstoneRecipes = Arrays.stream(Configs.recipes.handGrindstoneRecipes).collect(Collectors.toList());
        choppingRecipes = Arrays.stream(Configs.recipes.choppingRecipes).collect(Collectors.toList());
        manualChoppingRecipes = Arrays.stream(Configs.recipes.manualChoppingRecipes).collect(Collectors.toList());
        pressRecipes = Arrays.stream(Configs.recipes.pressRecipes).collect(Collectors.toList());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
            grindstoneRecipes.add(ByteBufUtils.readUTF8String(buf));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            handGrindstoneRecipes.add(ByteBufUtils.readUTF8String(buf));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            choppingRecipes.add(ByteBufUtils.readUTF8String(buf));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            manualChoppingRecipes.add(ByteBufUtils.readUTF8String(buf));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            pressRecipes.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(grindstoneRecipes.size());
        grindstoneRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
        buf.writeInt(handGrindstoneRecipes.size());
        handGrindstoneRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
        buf.writeInt(choppingRecipes.size());
        choppingRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
        buf.writeInt(manualChoppingRecipes.size());
        manualChoppingRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
        buf.writeInt(pressRecipes.size());
        pressRecipes.forEach(s -> ByteBufUtils.writeUTF8String(buf, s));
    }

    @Override
    protected void handle(SyncServerRecipesMessage message, MessageContext ctx) {
        HPRecipes.serverSyncedRecipes = true;
        HPRecipes.instance().reloadRecipes(message.grindstoneRecipes, message.handGrindstoneRecipes, message.choppingRecipes, message.manualChoppingRecipes, message.pressRecipes);
        HorsePowerMod.logger.info("Synced recipes from server");
    }
}
