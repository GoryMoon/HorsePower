package se.gory_moon.horsepower.client.model;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.horsepower.blocks.BlockChopper;
import se.gory_moon.horsepower.blocks.BlockHPChoppingBase;
import se.gory_moon.horsepower.util.RenderUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BakedChopperModel implements IBakedModel {

    private final IBakedModel standard;
    private final IModel choppingModel;
    private final VertexFormat format;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

    private final Map<String, IBakedModel> cache = Maps.newHashMap();
    private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;

    private final Cache<TableCombinationCacheKey, IBakedModel> tableCombinedCache = CacheBuilder.newBuilder().maximumSize(20).build();

    public BakedChopperModel(IBakedModel standard, IModel choppingModel, VertexFormat format) {
        this.standard = standard;
        this.choppingModel = choppingModel;
        this.format = format;
        this.transforms = RenderUtils.getTransforms(standard);

        textureGetter = location -> {
            assert location != null;
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        };
    }

    protected IBakedModel getActualModel(String texture, String top_texture, EnumFacing facing) {
        IBakedModel bakedModel = standard;

        if(texture != null) {
            if(cache.containsKey(texture)) {
                bakedModel = cache.get(texture);
            }
            else if(choppingModel != null) {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                builder.put("2", texture);
                builder.put("1", top_texture);
                IModel retexturedModel = choppingModel.retexture(builder.build());
                IModelState modelState = new SimpleModelState(transforms);

                bakedModel = retexturedModel.bake(modelState, format, textureGetter);
                cache.put(texture, bakedModel);
            }
        }

        final IBakedModel parentModel = bakedModel;
        try {
            bakedModel = tableCombinedCache.get(new TableCombinationCacheKey(bakedModel, facing), () -> getCombinedBakedModel(facing, parentModel));
        } catch(ExecutionException ignored) {
        }

        return bakedModel;
    }

    private IBakedModel getCombinedBakedModel(EnumFacing facing, IBakedModel parentModel) {
        IBakedModel out = parentModel;

        if(facing != null) {
            out = new TRSRBakedModel(out, facing);
        }
        return out;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        // get texture from state
        String side_texture = null;
        String top_texture = null;
        EnumFacing face = EnumFacing.SOUTH;

        if(state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            if(extendedState.getUnlistedNames().contains(BlockHPChoppingBase.SIDE_TEXTURE))
                side_texture = extendedState.getValue(BlockHPChoppingBase.SIDE_TEXTURE);
            if (extendedState.getUnlistedNames().contains(BlockHPChoppingBase.TOP_TEXTURE))
                top_texture = extendedState.getValue(BlockHPChoppingBase.TOP_TEXTURE);

            if(extendedState.getPropertyKeys().contains(BlockChopper.FACING)) {
                face = extendedState.getValue(BlockChopper.FACING);
            }
        }

        // models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
        if(side_texture == null) {
            return standard.getQuads(state, side, rand);
        }

        // the model returned by getActualModel should be a simple model with no special handling
        return getActualModel(side_texture, top_texture, face).getQuads(state, side, rand);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> pair = standard.handlePerspective(cameraTransformType);
        return Pair.of(this, pair.getRight());
    }

    @Override
    public boolean isAmbientOcclusion() {
        return standard.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return standard.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return standard.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return standard.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return standard.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ChopperItemOverrideList.INSTANCE;
    }

    private static class ChopperItemOverrideList extends ItemOverrideList {

        static ChopperItemOverrideList INSTANCE = new ChopperItemOverrideList();

        private ChopperItemOverrideList() {
            super(ImmutableList.of());
        }

        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            if(originalModel instanceof BakedChopperModel) {
                // read out the data on the itemstack
                ItemStack blockStack = new ItemStack(stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("textureBlock"): new NBTTagCompound());
                if(!blockStack.isEmpty()) {
                    // get model from data
                    Block block = Block.getBlockFromItem(blockStack.getItem());
                    IBlockState state = block.getStateFromMeta(blockStack.getItemDamage());
                    String side_texture = RenderUtils.getTextureFromBlockstate(state).getIconName();
                    String top_texture = RenderUtils.getTopTextureFromBlockstate(state).getIconName();
                    return ((BakedChopperModel) originalModel).getActualModel(side_texture, top_texture, null);
                }
            }

            return originalModel;
        }
    }

    private static class TableCombinationCacheKey {
        private final IBakedModel bakedBaseModel;
        private final EnumFacing facing;

        public TableCombinationCacheKey(IBakedModel bakedBaseModel, EnumFacing facing) {
            this.bakedBaseModel = bakedBaseModel;
            this.facing = facing;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TableCombinationCacheKey that = (TableCombinationCacheKey) o;

            return (bakedBaseModel != null ? bakedBaseModel.equals(that.bakedBaseModel) : that.bakedBaseModel == null) && facing == that.facing;
        }

        @Override
        public int hashCode() {
            int result = (bakedBaseModel != null ? bakedBaseModel.hashCode() : 0);
            result = 31 * result + (facing != null ? facing.hashCode() : 0);
            return result;
        }
    }
}
