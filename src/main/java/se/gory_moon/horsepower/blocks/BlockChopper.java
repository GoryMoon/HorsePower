package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.property.PropertyUnlistedString;
import se.gory_moon.horsepower.client.renderer.modelvariants.ChopperModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.tileentity.TileEntityChopper;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockChopper extends BlockHPBase implements IProbeInfoAccessor {

    //TODO add in future, causing bug when reenter ", Arrays.asList(EnumFacing.HORIZONTALS)"
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyEnum<ChopperModels> PART = PropertyEnum.create("part", ChopperModels.class);
    public static final PropertyUnlistedString SIDE_TEXTURE = new PropertyUnlistedString("side_texture");
    public static final PropertyUnlistedString TOP_TEXTURE = new PropertyUnlistedString("top_texture");

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);

    public BlockChopper() {
        super(Material.WOOD);
        setHardness(2.0F);
        setResistance(5.0F);
        setSoundType(SoundType.WOOD);
        setRegistryName(Constants.CHOPPER_BLOCK);
        setUnlocalizedName(Constants.CHOPPER_BLOCK);
        setCreativeTab(HorsePowerMod.creativeTab);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityChopper();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {PART, FACING}, new IUnlistedProperty[]{SIDE_TEXTURE, TOP_TEXTURE});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;

        TileEntityHPBase tile = getTileEntity(world, pos);
        if (tile != null && tile instanceof TileEntityChopper) {
            return ((TileEntityChopper)tile).getExtendedState(extendedState);
        }

        return super.getExtendedState(state, world, pos);
    }

    private void writeDataOntoItemstack(@Nonnull ItemStack item, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean inventorySave) {
        // get block data from the block
        TileEntity te = world.getTileEntity(pos);
        if(te != null && te instanceof TileEntityChopper) {
            TileEntityChopper table = (TileEntityChopper) te;
            NBTTagCompound tag = item.hasTagCompound() ? item.getTagCompound(): new NBTTagCompound();

            // texture
            NBTTagCompound data = table.getTextureBlock();

            if (!data.hasNoTags()) {
                tag.setTag("textureBlock", data);
            }

            if (!tag.hasNoTags()) {
                item.setTagCompound(tag);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        List<ItemStack> drops = new ArrayList<>();
        Item item = this.getItemDropped(state, world.rand, 0);
        if (item != Items.AIR) {
            drops.add(new ItemStack(item, 1, this.damageDropped(state)));
        }

        if(drops.size() > 0) {
            ItemStack stack = drops.get(0);
            writeDataOntoItemstack(stack, world, pos, state, false);
            return stack;
        }

        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {

            List<ItemStack> items = this.getDrops(worldIn, pos, state, fortune);
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get());

            for(ItemStack item : items) {
                // save the data from the block onto the item
                if(item.getItem() == Item.getItemFromBlock(this)) {
                    writeDataOntoItemstack(item, worldIn, pos, state, chance >= 1f);
                }
            }

            for(ItemStack item : items) {
                if(worldIn.rand.nextFloat() <= chance) {
                    spawnAsEntity(worldIn, pos, item);
                }
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            EnumFacing enumfacing = state.getValue(FACING);
            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing).withProperty(PART, ChopperModels.BASE), 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(PART, ChopperModels.BASE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, enumfacing).withProperty(PART, ChopperModels.BASE);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound(): new NBTTagCompound();
        TileEntityHPBase tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        NBTTagCompound baseTag = tag != null ? tag.getCompoundTag("textureBlock"): new NBTTagCompound();
        ((TileEntityChopper)tile).setTextureBlock(baseTag);
        tile.setForward(placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    public static ItemStack createItemStack(BlockChopper table, Block block, int blockMeta) {
        ItemStack stack = new ItemStack(table, 1);

        if(block != null) {
            ItemStack blockStack = new ItemStack(block, 1, blockMeta);
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound subTag = new NBTTagCompound();
            blockStack.writeToNBT(subTag);
            tag.setTag("textureBlock", subTag);
            stack.setTagCompound(tag);
        }

        return stack;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
        for(ItemStack stack : OreDictionary.getOres("logWood")) {
            Block block = getBlockFromItem(stack.getItem());
            int blockMeta = stack.getItemDamage();

            if(blockMeta == OreDictionary.WILDCARD_VALUE) {
                NonNullList<ItemStack> subBlocks = NonNullList.create();
                block.getSubBlocks(stack.getItem(), null, subBlocks);

                for(ItemStack subBlock : subBlocks) {
                    list.add(createItemStack(this, getBlockFromItem(subBlock.getItem()), subBlock.getItemDamage()));
                }
            }
            else {
                list.add(createItemStack(this, block, blockMeta));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.LOCATION.translate());
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.USE.translate());
    }

    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if (tileEntity instanceof TileEntityChopper) {
            TileEntityChopper te = (TileEntityChopper) tileEntity;
            double totalWindup = Configs.pointsForWindup > 0 ? Configs.pointsForWindup: 1;
            probeInfo.progress((long) ((((double)te.getField(2)) / totalWindup) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.WINDUP_PROGRESS.translate() + " ").suffix("%"));
            if (te.getField(0) > 1)
                probeInfo.progress((long) ((((double)te.getField(1)) / ((double)te.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
        }
    }
}
