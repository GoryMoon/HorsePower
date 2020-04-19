package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
import se.gory_moon.horsepower.util.RenderUtils;
import se.gory_moon.horsepower.util.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockHPChoppingBase extends HPBaseBlock { //TODO Rename

//    public static final PropertyUnlistedString SIDE_TEXTURE = new PropertyUnlistedString("side_texture");
//    public static final PropertyUnlistedString TOP_TEXTURE = new PropertyUnlistedString("top_texture");

    public BlockHPChoppingBase(Properties builder) {
        super(builder);
    }

//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState extendedState = (IExtendedBlockState) state;
//
//        HPBaseTileEntity tile = getTileEntity(world, pos);
//        if (tile != null) {
//            return getExtendedState(tile, tile.getExtendedState(extendedState));
//        }
//
//        return super.getExtendedState(state, world, pos);
//    }

//    private void writeDataOntoItemstack(@Nonnull ItemStack item, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean inventorySave) {
//        // get block data from the block
//        TileEntity te = world.getTileEntity(pos);
//        if(te != null && (te instanceof TileEntityChopper || te instanceof TileEntityManualChopper)) {
//            CompoundNBT tag = item.hasTag() ? item.getTag(): new CompoundNBT();
//
//            // texture
//            CompoundNBT data = te.getTileData().getCompound("textureBlock");
//
//            if (!data.isEmpty()) {
//                tag.put("textureBlock", data);
//            }
//
//            if (!tag.isEmpty()) {
//                item.setTag(tag);
//            }
//        }
//    }

//    @Override
//    public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player) {
//        List<ItemStack> drops = new ArrayList<>();
//        Item item = this.getItemDropped(state, world.rand, 0);
//        if (item != Items.AIR) {
//            drops.add(new ItemStack(item, 1, this.damageDropped(state)));
//        }
//
//        if(drops.size() > 0) {
//            ItemStack stack = drops.get(0);
//            writeDataOntoItemstack(stack, world, pos, state, false);
//            return stack;
//        }
//
//        return super.getPickBlock(state, target, world, pos, player);
//    }

//    @Override
//    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, BlockState state, float chance, int fortune) {
//        if(!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {
//
//            List<ItemStack> items = this.getDrops(worldIn, pos, state, fortune);
//            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get());
//
//            for(ItemStack item : items) {
//                // save the data from the block onto the item
//                if(item.getItem() == Item.getItemFromBlock(this)) {
//                    writeDataOntoItemstack(item, worldIn, pos, state, chance >= 1f);
//                }
//            }
//
//            for(ItemStack item : items) {
//                if(worldIn.rand.nextFloat() <= chance) {
//                    spawnAsEntity(worldIn, pos, item);
//                }
//            }
//        }
//    }

//    @Override
//    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, EntityLivingBase placer, ItemStack stack) {
//        CompoundNBT tag = stack.hasTag() ? stack.getTag(): new CompoundNBT();
//        HPBaseTileEntity tile = getTileEntity(worldIn, pos);
//        if (tile == null)
//            return;
//        CompoundNBT baseTag = tag != null ? tag.getCompound("textureBlock"): new CompoundNBT();
//        tile.getTileData().setTag("textureBlock", baseTag);
//    }

//    public static ItemStack createItemStack(BlockHPChoppingBase table, int amount, ItemStack blockItem) {
//        ItemStack stack = new ItemStack(table, amount);
//        Block block = Block.getBlockFromItem(blockItem.getItem());
//
//        if(block != Blocks.AIR) {
//            ItemStack blockStack = new ItemStack(block, 1, blockItem.getDamage());
//            CompoundNBT tag = new CompoundNBT();
//            CompoundNBT subTag = new CompoundNBT();
//            if (block instanceof BlockHPChoppingBase) {
//                subTag = blockItem.getSubCompound("textureBlock");
//                subTag = subTag != null ? subTag: new CompoundNBT();
//            } else {
//                blockStack.writeToNBT(subTag);
//            }
//            tag.setTag("textureBlock", subTag);
//            stack.setTag(tag);
//        }
//
//        return stack;
//    }

//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        List<ItemStack> stacks = Utils.getCraftingItems(this);
//        for(ItemStack stack : stacks) {
//            if (!Configs.general.useDynamicDisplay && !"minecraft".equals(stack.getItem().getRegistryName().getResourceDomain()))
//                continue;
//            Block block = getBlockFromItem(stack.getItem());
//            int blockMeta = stack.getItemDamage();
//
//            if(blockMeta == OreDictionary.WILDCARD_VALUE) {
//                NonNullList<ItemStack> subBlocks = NonNullList.create();
//                block.getSubBlocks(null, subBlocks);
//
//                for(ItemStack subBlock : subBlocks) {
//                    list.add(createItemStack(this, 1, subBlock));
//                }
//            }
//            else {
//                list.add(createItemStack(this, 1, stack));
//            }
//        }
//    }

//    public static IExtendedBlockState getExtendedState(HPBaseTileEntity te, IExtendedBlockState state) {
//        String side_texture = te.getTileData().getString("side_texture");
//        String top_texture = te.getTileData().getString("top_texture");
//
//        if (side_texture.isEmpty() || top_texture.isEmpty()) {
//            ItemStack stack = new ItemStack(te.getTileData().getCompoundTag("textureBlock"));
//            if (!stack.isEmpty() && te.getWorld().isRemote) {
//                Block block = Block.getBlockFromItem(stack.getItem());
//                BlockState state1 = block.getStateFromMeta(stack.getMetadata());
//                side_texture = RenderUtils.getTextureFromBlockstate(state1).getIconName();
//                top_texture = RenderUtils.getTopTextureFromBlockstate(state1).getIconName();
//                te.getTileData().setString("side_texture", side_texture);
//                te.getTileData().setString("top_texture", top_texture);
//            }
//        }
//
//        if (!side_texture.isEmpty())
//            state = state.withProperty(SIDE_TEXTURE, side_texture);
//        if (!top_texture.isEmpty())
//            state = state.withProperty(TOP_TEXTURE, top_texture);
//
//        return state;
//    }
}
