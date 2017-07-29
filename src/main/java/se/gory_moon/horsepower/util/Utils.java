package se.gory_moon.horsepower.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static ArrayList<Class<? extends EntityCreature>> getCreatureClasses() {
        ArrayList<Class<? extends EntityCreature>> clazzes = Lists.newArrayList();
        if (Configs.general.useHorseInterface)
            clazzes.add(AbstractHorse.class);

        for (String e: Configs.general.grindstoneMobList) {
            try {
                Class clazz = Class.forName(e);

                if (EntityCreature.class.isAssignableFrom(clazz)) {
                    clazzes.add(clazz);
                } else {
                    HorsePowerMod.logger.error("Error in config, the mob (" + e + ") can't be leashed");
                }
            } catch (ClassNotFoundException e1) {
                HorsePowerMod.logger.error("Error in config, could not find (" + e + ") mob class");
            }
        }
        return clazzes;
    }

    public static int getItemStackHashCode(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
        tag.removeTag("Count");
        tag.removeTag("Damage");
        return tag.hashCode();
    }

    public static int getItemStackCountHashCode(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
        tag.removeTag("Damage");
        return tag.hashCode();

    }

    public static TextureAtlasSprite getTopTextureFromBlock(Block block, int meta) {
        IBlockState state = block.getStateFromMeta(meta);
        return getTopTextureFromBlockstate(state);
    }

    public static TextureAtlasSprite getTopTextureFromBlockstate(IBlockState state) {
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
        if (model != Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
            List<BakedQuad> quads = model.getQuads(state, EnumFacing.UP, 0);
            return quads.size() >= 1 ? quads.get(0).getSprite(): Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        }
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public static TextureAtlasSprite getTextureFromBlock(Block block, int meta) {
        IBlockState state = block.getStateFromMeta(meta);
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public static TextureAtlasSprite getTextureFromBlockstate(IBlockState state) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(IPerspectiveAwareModel model) {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            TRSRTransformation transformation = new TRSRTransformation(model.handlePerspective(type).getRight());
            if(!transformation.equals(TRSRTransformation.identity())) {
                builder.put(type, TRSRTransformation.blockCenterToCorner(transformation));
            }
        }
        return builder.build();
    }

    public static int getBaseAmount(String in) {
        try {
            return Integer.parseInt(in.split("-")[0]);
        } catch (NumberFormatException e) {
            errorMessage("Base amount for chopping axe is malformed, (" + in + ")");
        }
        return 0;
    }

    public static int getChance(String in) {
        try {
            return Integer.parseInt(in.split("-")[1]);
        } catch (NumberFormatException e) {
            errorMessage("Chance for chopping axe is malformed, (" + in + ")");
        }
        return 0;
    }

    public static void errorMessage(String message) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (FMLClientHandler.instance().getClientPlayerEntity() != null)
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString(TextFormatting.RED + message).setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Loader.instance().getConfigDir() + "/horsepower.cfg")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Change in in-game config or click to open the config file to fix this")))));
            else
                HPRecipes.ERRORS.add(message);
        }
        HorsePowerMod.logger.warn(message);
    }

    public static Object parseItemStack(String item, boolean acceptOre, boolean acceptAmount) throws Exception {
        String[] data = item.split("\\$");
        NBTTagCompound nbt = data.length == 1 ? null: JsonToNBT.getTagFromJson(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("$"));

        data = item.split("@");
        int amount = !acceptAmount || data.length == 1 ? 1: Integer.parseInt(data[1]);
        if (data.length == 2)
            item = item.substring(0, item.indexOf("@"));

        data = item.split(":");
        int meta = data.length == 2 ? 0 : "*".equals(data[2]) ? OreDictionary.WILDCARD_VALUE: Integer.parseInt(data[2]);

        if (item.startsWith("ore:")) {
            if (!acceptOre)
                throw new InvalidParameterException();
            if (amount > 1) {
                return OreDictionary.getOres(item.substring(4)).stream().map(stack -> {
                    ItemStack stack1 = stack.copy();
                    stack1.setCount(amount);
                    return stack1;
                }).collect(Collectors.toList());
            } else
                return OreDictionary.getOres(item.substring(4));
        } else {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("id", data[0] + ":" + data[1]);
            compound.setByte("Count", (byte) amount);
            compound.setShort("Damage", (short) meta);
            if (nbt != null)
                compound.setTag("tag", nbt);
            return new ItemStack(compound);
        }
    }
}
