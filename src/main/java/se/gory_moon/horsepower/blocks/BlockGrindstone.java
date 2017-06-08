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
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.util.Colors;
import se.gory_moon.horsepower.util.Utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockGrindstone extends Block implements IProbeInfoAccessor {

    private static boolean keepInventory = false;
    public static final PropertyBool FILLED = PropertyBool.create("filled");
    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 8D/16D, 1.0D);
    private static final AxisAlignedBB BOUNDING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 13D/16D, 1.0D);

    public BlockGrindstone() {
        super(Material.ROCK);
        setHardness(1.5F);
        setResistance(10F);
        setSoundType(SoundType.STONE);
        setRegistryName(Constants.GRINDSTONE_BLOCK);
        setUnlocalizedName(Constants.GRINDSTONE_BLOCK);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityGrindstone();
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        if (!player.capabilities.isCreativeMode && !worldIn.isRemote) {
            TileEntityGrindstone te = getTileEntity(worldIn, pos);

            if (te != null) {
                InventoryHelper.dropInventoryItems(worldIn, pos, te);
                if (te.hasWorker())
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.LEAD));
            }
        }
    }

    private TileEntityGrindstone getTileEntity(World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityGrindstone ? (TileEntityGrindstone)tileentity : null;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FILLED});
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FILLED).booleanValue() ? 1: 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FILLED, meta == 1);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    public static void setState(boolean filled, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;
        worldIn.setBlockState(pos, ModBlocks.BLOCK_GRINDSTONE.getDefaultState().withProperty(FILLED, filled), 3);
        keepInventory = false;

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
            tileentity.validate();
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory && !worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityGrindstone) {
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntityGrindstone tileEntityGrindstone = (TileEntityGrindstone) worldIn.getTileEntity(pos);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        EntityCreature creature = null;
        ArrayList<Class<? extends EntityCreature>> clazzes = Utils.getCreatureClasses();
        search: for (Class<? extends Entity> clazz: clazzes) {
            for (Object entity : worldIn.getEntitiesWithinAABB(clazz, new AxisAlignedBB((double)x - 7.0D, (double)y - 7.0D, (double)z - 7.0D, (double)x + 7.0D, (double)y + 7.0D, (double)z + 7.0D))){
                if (entity instanceof EntityCreature) {
                    EntityCreature tmp = (EntityCreature) entity;
                    if ((tmp.getLeashed() && tmp.getLeashedToEntity() == playerIn)) {
                        creature = tmp;
                        break search;
                    }
                }
            }
        }
        if (stack.getItem() instanceof ItemLead && creature != null || creature != null) {
            if (!tileEntityGrindstone.hasWorker()) {
                creature.clearLeashed(true, false);
                tileEntityGrindstone.setWorker(creature);
                return true;
            } else {
                return false;
            }
        } else if (!stack.isEmpty() && tileEntityGrindstone.isItemValidForSlot(0, stack)) {
            ItemStack itemStack = tileEntityGrindstone.getStackInSlot(0);
            boolean flag = false;

            if (itemStack.isEmpty()) {
                tileEntityGrindstone.setInventorySlotContents(0, stack.copy());
                stack.setCount(0);
                flag = true;
            } else if (TileEntityGrindstone.canCombine(itemStack, stack)) {
                int i = stack.getMaxStackSize() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                flag = j > 0;
            }

            if (flag)
                return true;
        }

        ItemStack result = tileEntityGrindstone.removeStackFromSlot(1);
        if (result.isEmpty() && stack.isEmpty() && hand != EnumHand.OFF_HAND) {
            result = tileEntityGrindstone.removeStackFromSlot(0);
            BlockGrindstone.setState(false, worldIn, pos);
        }

        if (result.isEmpty()) {
            if (!stack.isEmpty())
                return false;

            tileEntityGrindstone.setWorkerToPlayer(playerIn);
        }

        if (stack.isEmpty()) {
            playerIn.setHeldItem(hand, result);
        } else if (playerIn.func_191521_c(result)) {
            playerIn.dropItem(result, false);
        }

        tileEntityGrindstone.markDirty();
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.ITEM.MILL.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.MILL.LOCATION.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.MILL.USE.translate());
    }

    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if (tileEntity instanceof TileEntityGrindstone) {
            TileEntityGrindstone te = (TileEntityGrindstone) tileEntity;
            probeInfo.progress((long) ((((double)te.getField(1)) / ((double)te.getField(0))) * 100L), 100L, new ProgressStyle().suffix("%"));
        }
    }
}
