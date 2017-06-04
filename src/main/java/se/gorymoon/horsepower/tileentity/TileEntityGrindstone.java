package se.gorymoon.horsepower.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import se.gorymoon.horsepower.blocks.BlockGrindstone;
import se.gorymoon.horsepower.recipes.GrindstoneRecipes;
import se.gorymoon.horsepower.util.Localization;
import se.gorymoon.horsepower.util.Utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TileEntityGrindstone extends TileEntity implements ITickable, ISidedInventory {

    private static final int[] SLOTS_TOP = new int[] {0};
    private static final int[] SLOTS_BOTTOM = new int[] {1};

    private NonNullList<ItemStack> millItemStacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);

    private static double[][] path = {{-1.5, -1.5}, {0, -1.5}, {1, -1.5}, {1, 0}, {1, 1}, {0, 1}, {-1.5, 1}, {-1.5, 0}};
    private AxisAlignedBB[] searchAreas = new AxisAlignedBB[8];
    private List<BlockPos> searchPos = null;
    private int origin = -1;
    private int target = origin;

    private boolean hasWorker = false;
    private EntityCreature worker;
    private NBTTagCompound nbtWorker;

    private int currentItemMillTime;
    private int totalItemMillTime;
    private boolean running = true;
    private boolean wasRunning = false;

    private boolean valid = false;
    private int validationTimer = 0;

    public void setWorker(EntityCreature newWorker) {
        hasWorker = true;
        worker = newWorker;
        worker.setHomePosAndDistance(pos, 3);
        target = getClosestTarget();
    }

    public void setWorkerToPlayer(EntityPlayer player) {
        if (hasWorker() && worker.canBeLeashedTo(player)) {
            hasWorker = false;
            worker.detachHome();
            worker.setLeashedToEntity(player, true);
            worker = null;
        }
    }

    public boolean hasWorker() {
        if (worker != null && !worker.isDead && !worker.getLeashed() && worker.getDistanceSq(pos) < 45) {
            return true;
        } else {
            if (worker != null) {
                worker = null;
                if (!getWorld().isRemote)
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.LEAD));
            }
            hasWorker = false;
            return false;
        }
    }

    public EntityCreature getWorker() {
        return worker;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        if (valid)
            return super.getDisplayName();
        else
            return new TextComponentTranslation(Localization.INFO.GRINDSTONE_INVALID.key()).setStyle(new Style().setColor(TextFormatting.RED));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        ItemStackHelper.saveAllItems(compound, millItemStacks);
        compound.setInteger("millTime", currentItemMillTime);
        compound.setInteger("totalMillTime", totalItemMillTime);

        compound.setInteger("target", target);
        compound.setInteger("origin", origin);
        compound.setBoolean("hasWorker", hasWorker);

        if (this.worker != null)
        {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            UUID uuid = worker.getUniqueID();
            nbtTagCompound.setUniqueId("UUID", uuid);

            compound.setTag("leash", nbtTagCompound);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        millItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, millItemStacks);
        currentItemMillTime = compound.getInteger("millTime");
        totalItemMillTime = compound.getInteger("totalMillTime");

        target = compound.getInteger("target");
        origin = compound.getInteger("origin");
        hasWorker = compound.getBoolean("hasWorker");

        if (hasWorker && compound.hasKey("leash", 10)) {
            nbtWorker = compound.getCompoundTag("leash");
        }
    }

    public void notifyUpdate() {
        getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), -999, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        notifyUpdate();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
       readFromNBT(tag);
       markDirty();
    }

    private boolean validateArea() {
        if (searchPos == null) {
            searchPos = Lists.newArrayList();

            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && z == 0)
                        continue;
                    searchPos.add(getPos().add(x, 0, z));
                    searchPos.add(getPos().add(x, -1, z));
                }
            }
        }

        for (BlockPos pos: searchPos) {
            if (!getWorld().isAirBlock(pos))
                return false;
        }
        return true;
    }

    @Override
    public void update() {
        boolean flag = false;

        validationTimer--;
        if (validationTimer <= 0) {
            valid = validateArea();
            if (valid)
                validationTimer = 220;
            else
                validationTimer = 60;
        }

        if (nbtWorker != null) {
            if (hasWorker) {
                UUID uuid = nbtWorker.getUniqueId("UUID");
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();

                ArrayList<Class<? extends EntityCreature>> clazzes = Utils.getCreatureClasses();
                search: for (Class<? extends Entity> clazz: clazzes) {
                    for (Object entity : world.getEntitiesWithinAABB(clazz, new AxisAlignedBB((double)x - 7.0D, (double)y - 7.0D, (double)z - 7.0D, (double)x + 7.0D, (double)y + 7.0D, (double)z + 7.0D))){
                        if (entity instanceof EntityCreature) {
                            EntityCreature creature = (EntityCreature) entity;
                            if (creature.getUniqueID().equals(uuid)) {
                                setWorker(creature);
                                break search;
                            }
                        }
                    }
                }
            }
            nbtWorker = null;
        }

        if (!world.isRemote && valid) {
            if (!running && canMill()) {
                running = true;
            } else if (running && !canMill()){
                running = false;
            }

            if (running != wasRunning) {
                target = getClosestTarget();
                wasRunning = running;
            }

            if (hasWorker()) {
                if (running) {

                    double x = pos.getX() + path[target][0] * 2;
                    double y = pos.getY() - 1;
                    double z = pos.getZ() + path[target][1] * 2;

                    if (searchAreas[target] == null)
                        searchAreas[target] = new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D);

                    if (worker.getEntityBoundingBox().intersectsWith(searchAreas[target])) {
                        int next = target + 1;
                        int previous = target -1;
                        if (next >= path.length)
                            next = 0;
                        if (previous < 0)
                            previous = path.length - 1;

                        if (origin != target && target != previous) {
                            origin = target;
                            currentItemMillTime++;

                            if (currentItemMillTime == totalItemMillTime) {
                                currentItemMillTime = 0;

                                totalItemMillTime = GrindstoneRecipes.instance().getGrindstoneTime(millItemStacks.get(0));
                                millItem();
                                flag = true;
                            }
                        }
                        target = next;
                    }

                    if (worker instanceof AbstractHorse && ((AbstractHorse)worker).isEatingHaystack()) {
                        ((AbstractHorse)worker).setEatingHaystack(false);
                    }

                    if (target != -1 && worker.getNavigator().noPath()) {
                        x = pos.getX() + path[target][0] * 2;
                        y = pos.getY() - 1;
                        z = pos.getZ() + path[target][1] * 2;

                        worker.getNavigator().tryMoveToXYZ(x, y, z, 1D);
                    }

                }
            }
        }

        if (flag) {
            markDirty();
        }
    }

    private int getClosestTarget() {
        if (hasWorker()) {
            double dist = Double.MAX_VALUE;
            int closest = 0;

            for (int i = 0; i < path.length; i++) {
                double x = pos.getX() + path[i][0] * 2;
                double y = pos.getY() - 1;
                double z = pos.getZ() + path[i][1] * 2;

                double tmp = worker.getDistance(x, y, z);
                if (tmp < dist) {
                    dist = tmp;
                    closest = i;
                }
            }

            return closest;
        }
        return 0;
    }

    private void millItem() {
        if (canMill()) {
            ItemStack input = millItemStacks.get(0);
            ItemStack result = GrindstoneRecipes.instance().getGrindstoneResult(millItemStacks.get(0));
            ItemStack output = millItemStacks.get(1);

            if (output.isEmpty())
            {
                millItemStacks.set(1, result.copy());
            }
            else if (output.getItem() == result.getItem())
            {
                output.grow(result.getCount());
            }

            input.shrink(1);
            BlockGrindstone.setState(true, world, pos);
        }
    }

    private boolean canMill() {
        if (millItemStacks.get(0).isEmpty()) {
            return false;
        } else {
            ItemStack itemstack = GrindstoneRecipes.instance().getGrindstoneResult(millItemStacks.get(0));

            if (itemstack.isEmpty())
            {
                return false;
            }
            else
            {
                ItemStack output = millItemStacks.get(1);
                if (output.isEmpty()) return true;
                if (!output.isItemEqual(itemstack)) return false;
                int result = output.getCount() + itemstack.getCount();
                return result <= getInventoryStackLimit() && result <= output.getMaxStackSize();
            }
        }
    }

    public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == stack2.getMetadata() && (stack1.getCount() <= stack1.getMaxStackSize() && ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : new int[0]);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction == EnumFacing.DOWN && index == 1;
    }

    @Override
    public int getSizeInventory() {
        return millItemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : millItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return millItemStacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(millItemStacks, index, count);
        if (index == 1 && millItemStacks.get(1).isEmpty())
            BlockGrindstone.setState(false, world, pos);
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(millItemStacks, index);
        if (index == 1 && millItemStacks.get(1).isEmpty())
            BlockGrindstone.setState(false, world, pos);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = millItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        millItemStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 1 && millItemStacks.get(1).isEmpty()) {
            BlockGrindstone.setState(false, world, pos);
            markDirty();
        }

        if (index == 0 && !flag) {
            totalItemMillTime = GrindstoneRecipes.instance().getGrindstoneTime(stack);
            currentItemMillTime = 0;
            markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != 1 && index == 0 && GrindstoneRecipes.instance().hasRecipe(stack);
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.totalItemMillTime;
            case 1:
                return this.currentItemMillTime;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.totalItemMillTime = value;
                break;
            case 1:
                this.currentItemMillTime = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        millItemStacks.clear();
    }

    @Override
    public String getName() {
        return "container.mill";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    private IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    private IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
        return super.getCapability(capability, facing);
    }
}
