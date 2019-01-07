package se.gory_moon.horsepower.network.messages;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SyncServerRecipesMessage {

    private List<String> grindstoneRecipes;
    private List<String> handGrindstoneRecipes;
    private List<String> choppingRecipes;
    private List<String> manualChoppingRecipes;
    private List<String> pressRecipes;

    public SyncServerRecipesMessage() {
        grindstoneRecipes = Arrays.stream(Configs.recipes.grindstoneRecipes).collect(Collectors.toList());
        handGrindstoneRecipes = Arrays.stream(Configs.recipes.handGrindstoneRecipes).collect(Collectors.toList());
        choppingRecipes = Arrays.stream(Configs.recipes.choppingRecipes).collect(Collectors.toList());
        manualChoppingRecipes = Arrays.stream(Configs.recipes.manualChoppingRecipes).collect(Collectors.toList());
        pressRecipes = Arrays.stream(Configs.recipes.pressRecipes).collect(Collectors.toList());
    }

    public SyncServerRecipesMessage(List<String> grindstoneRecipes, List<String> handGrindstoneRecipes, List<String> choppingRecipes, List<String> manualChoppingRecipes, List<String> pressRecipes) {
        this.grindstoneRecipes = grindstoneRecipes;
        this.handGrindstoneRecipes = handGrindstoneRecipes;
        this.choppingRecipes = choppingRecipes;
        this.manualChoppingRecipes = manualChoppingRecipes;
        this.pressRecipes = pressRecipes;
    }

    public static SyncServerRecipesMessage fromBytes(PacketBuffer buf) {
        return new SyncServerRecipesMessage(read(buf), read(buf), read(buf), read(buf), read(buf));
    }

    private static List<String> read(PacketBuffer buf) {
        int size = buf.readInt();
        List<String> recipes = new ArrayList<>();
        for (int i = 0; i < size; i++)
            recipes.add(buf.readString(32767));
        return recipes;
    }

    public static void toBytes(SyncServerRecipesMessage message, PacketBuffer buf) {
        write(message.grindstoneRecipes, buf);
        write(message.handGrindstoneRecipes, buf);
        write(message.choppingRecipes, buf);
        write(message.manualChoppingRecipes, buf);
        write(message.pressRecipes, buf);
    }

    private static void write(List<String> recipes, PacketBuffer buf) {
        buf.writeInt(recipes.size());
        recipes.forEach(buf::writeString);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(SyncServerRecipesMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HPRecipes.serverSyncedRecipes = true;
            HPRecipes.instance().reloadRecipes(message.grindstoneRecipes, message.handGrindstoneRecipes, message.choppingRecipes, message.manualChoppingRecipes, message.pressRecipes);
            HorsePowerMod.logger.info("Synced recipes from server");
        });
    }
}
