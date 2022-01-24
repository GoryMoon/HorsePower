package se.gory_moon.horsepower.util;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.client.utils.HPClientUtils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class HPUtils {

    private HPUtils() {}

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(Constants.MOD_ID, path);
    }

    public static Entity getEntityWithinArea(World world, AxisAlignedBB alignedBB, Predicate<CreatureEntity> predicate) {
        List<CreatureEntity> entities = world.getEntitiesWithinAABB(CreatureEntity.class, alignedBB,
                e -> HPTags.Entities.WORKER_ENTITIES.contains(e.getType()) && predicate.test(e));
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public static int getBaseAmount(String in) {
        try {
            return Integer.parseInt(in.split("-")[0]);
        } catch (NumberFormatException e) {
            errorMessage("Base amount for chopping axe is malformed, (" + in + ")", false);
        }
        return 0;
    }

    public static int getChance(String in) {
        try {
            return Integer.parseInt(in.split("-")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            errorMessage("Chance for chopping axe is malformed, (" + in + ")", false);
        }
        return 0;
    }

    public static void errorMessage(String message, boolean showDirectly) {
        BiConsumer<String, Boolean> handler = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> HPClientUtils::errorMessageConsumer);
        if (handler != null)
            handler.accept(message, showDirectly);

        HorsePower.LOGGER.warn(message);
    }

    public static void sendSavedErrors() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> HPClientUtils::sendSavedErrors);
    }
/*
    public static List<ItemStack> getCraftingItems(BlockHPChoppingBase block) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        main: for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe instanceof ShapedChoppingRecipe) {
                if (((ShapedChoppingRecipe) recipe).getSimpleRecipeOutput().getItem() instanceof ItemBlock && ((ItemBlock) ((ShapedChoppingRecipe) recipe).getSimpleRecipeOutput().getItem()).getBlock() == block) {
                    for (ItemStack stack : ((ShapedChoppingRecipe) recipe).outputBlocks) {
                        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockHPChoppingBase) {
                            Block.getBlockFromItem(stack.getItem()).getSubBlocks(null, stacks);
                            continue main;
                        }
                    }
                    stacks.addAll(((ShapedChoppingRecipe) recipe).outputBlocks);
                }
            } else if (recipe instanceof ShapelessChoppingRecipe) {
                if (((ShapelessChoppingRecipe) recipe).getSimpleRecipeOutput().getItem() instanceof ItemBlock && ((ItemBlock) ((ShapelessChoppingRecipe) recipe).getSimpleRecipeOutput().getItem()).getBlock() == block) {
                    for (ItemStack stack : ((ShapelessChoppingRecipe) recipe).outputBlocks) {
                        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockHPChoppingBase) {
                            Block.getBlockFromItem(stack.getItem()).getSubBlocks(null, stacks);
                            continue main;
                        }
                    }
                    stacks.addAll(((ShapelessChoppingRecipe) recipe).outputBlocks);
                }
            }
        }
        return stacks;
    }*/

    public static Object parseItemStack(String incomingItem, boolean acceptAmount) throws Exception {
        String item = incomingItem;
        String[] data = item.split("\\$");
        CompoundNBT nbt = data.length == 1 ? null: JsonToNBT.getTagFromJson(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("$"));

        data = item.split("@");
        int amount = !acceptAmount || data.length == 1 ? 1: Integer.parseInt(data[1]);

        data = item.split(":");
        CompoundNBT compound = new CompoundNBT();
        compound.putString("id", data[0] + ":" + data[1]);
        compound.putByte("Count", (byte) amount);
        if (nbt != null)
            compound.put("tag", nbt);
        return ItemStack.read(compound);
    }
}
