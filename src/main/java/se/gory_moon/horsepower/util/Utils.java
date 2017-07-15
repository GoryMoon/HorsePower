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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static ArrayList<Class<? extends EntityCreature>> getCreatureClasses() {
        ArrayList<Class<? extends EntityCreature>> clazzes = Lists.newArrayList();
        if (Configs.useHorseInterface)
            clazzes.add(AbstractHorse.class);

        for (String e: Configs.grindstoneMobList) {
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

}
