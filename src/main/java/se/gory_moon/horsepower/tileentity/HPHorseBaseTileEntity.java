package se.gory_moon.horsepower.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import se.gory_moon.horsepower.util.Utils;

public abstract class HPHorseBaseTileEntity extends HPBaseTileEntity implements ITickableTileEntity {

    protected static double[][] walkPath = { { -1, -1 }, { 0, -1 }, { 0.75, -1 }, { 0.75, 0 }, { 0.75, 0.75 }, { 0, 0.75 }, { -1, 0.75 }, { -0.75, 0 } };
    protected static double[][] searchPath = { { -1, -1 }, { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 } };
    public AxisAlignedBB[] searchAreas = new AxisAlignedBB[8];
    protected List<BlockPos> searchPos = null;
    protected int origin = -1;
    protected int target = origin;

    protected boolean hasWorker = false;
    protected CreatureEntity worker;
    protected CompoundNBT nbtWorker;

    protected boolean valid = false;
    protected int validationTimer = 0;
    protected int locateHorseTimer = 0;
    protected boolean running = true;
    protected boolean wasRunning = false;

    public HPHorseBaseTileEntity(int inventorySize, TileEntityType type) {
        super(inventorySize, type);
    }

    public abstract boolean validateArea();

    public abstract boolean targetReached();

    public abstract int getPositionOffset();

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        target = compound.getInt("target");
        origin = compound.getInt("origin");
        hasWorker = compound.getBoolean("hasWorker");

        if (hasWorker && compound.contains("leash", 10)) {
            nbtWorker = compound.getCompound("leash");
            findWorker();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("target", target);
        compound.putInt("origin", origin);
        compound.putBoolean("hasWorker", hasWorker);

        if (this.worker != null) {
            if (nbtWorker == null) {
                CompoundNBT nbtTagCompound = new CompoundNBT();
                UUID uuid = worker.getUniqueID();
                nbtTagCompound.putUniqueId("UUID", uuid);
                nbtWorker = nbtTagCompound;
            }

            compound.put("leash", nbtWorker);
        }

        return super.write(compound);
    }

    private boolean findWorker() {
        UUID uuid = nbtWorker.getUniqueId("UUID");
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (world != null) {
            ArrayList<Class<? extends CreatureEntity>> clazzes = Utils.getCreatureClasses();
            for (Class<? extends Entity> clazz : clazzes) {
                for (Object entity : world.getEntitiesWithinAABB(clazz, new AxisAlignedBB((double) x - 7.0D, (double) y - 7.0D, (double) z - 7.0D, (double) x + 7.0D, (double) y + 7.0D, (double) z + 7.0D))) {
                    if (entity instanceof CreatureEntity) {
                        CreatureEntity creature = (CreatureEntity) entity;
                        if (creature.getUniqueID().equals(uuid)) {
                            setWorker(creature);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setWorkerToPlayer(PlayerEntity player) {
        if (hasWorker() && worker.canBeLeashedTo(player)) {
            hasWorker = false;
            worker.detachHome();
            worker.setLeashHolder(player, true);
            worker = null;
            nbtWorker = null;
        }
    }

    public boolean hasWorker() {
        if (worker != null && worker.isAlive() && !worker.getLeashed() && worker.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 45) {
            return true;
        } else {
            if (worker != null) {
                worker = null;
                nbtWorker = null;
                if (!getWorld().isRemote)
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.LEAD));
            }
            hasWorker = false;
            return false;
        }
    }

    public CreatureEntity getWorker() {
        return worker;
    }

    public void setWorker(CreatureEntity newWorker) {
        hasWorker = true;
        worker = newWorker;
        worker.setHomePosAndDistance(pos, 3);
        target = getClosestTarget();
        if (worker != null) {
            CompoundNBT nbtTagCompound = new CompoundNBT();
            UUID uuid = worker.getUniqueID();
            nbtTagCompound.putUniqueId("UUID", uuid);
            nbtWorker = nbtTagCompound;
        }
        markDirty();
    }

    public boolean isInvalid() {
        return !valid;
    }

    private Vec3d getPathPosition(int i, boolean nav) {
        double x = pos.getX() + (nav ? walkPath: searchPath)[i][0] * (nav ? 3: 2.5);
        double y = pos.getY() + getPositionOffset();
        double z = pos.getZ() + (nav ? walkPath: searchPath)[i][1] * (nav ? 3: 2.5);
        return new Vec3d(x, y, z);
    }

    protected int getClosestTarget() {
        if (hasWorker()) {
            double dist = Double.MAX_VALUE;
            int closest = 0;

            for (int i = 0; i < walkPath.length; i++) {
                Vec3d pos = getPathPosition(i, false);

                double tmp = pos.distanceTo(worker.getPositionVector());
                if (tmp < dist) {
                    dist = tmp;
                    closest = i;
                }
            }

            return closest;
        }
        return 0;
    }

    @Override
    public void tick() {
        validationTimer--;
        if (validationTimer <= 0) {
            valid = validateArea();
            if (valid)
                validationTimer = 220;
            else
                validationTimer = 60;
        }
        boolean flag = false;

        if (!hasWorker())
            locateHorseTimer--;
        if (!hasWorker() && nbtWorker != null && locateHorseTimer <= 0) {
            flag = findWorker();
        }
        if (locateHorseTimer <= 0)
            locateHorseTimer = 120;
        if (getWorld() != null && !getWorld().isRemote && valid) {
            if (!running && canWork()) {
                running = true;
            } else if (running && !canWork()) {
                running = false;
            }

            if (running != wasRunning) {
                target = getClosestTarget();
                wasRunning = running;
            }

            if (hasWorker()) {
                if (running) {

                    Vec3d pos = getPathPosition(target, false);
                    double x = pos.x;
                    double y = pos.y;
                    double z = pos.z;

                    if (searchAreas[target] == null)
                        searchAreas[target] = new AxisAlignedBB(x - 0.5D, y - 1.0D, z - 0.5D, x + 1.5D, y + 1.0D, z + 1.5D);

                    if (worker.getBoundingBox().intersects(searchAreas[target])) {
                        int next = target + 1;
                        int previous = target - 1;
                        if (next >= walkPath.length)
                            next = 0;
                        if (previous < 0)
                            previous = walkPath.length - 1;

                        if (origin != target && target != previous) {
                            origin = target;
                            flag = targetReached();
                        }
                        target = next;
                    }

                    if (worker instanceof AbstractHorseEntity) {
                        AbstractHorseEntity horse = (AbstractHorseEntity) this.worker;
                        if (horse.isEatingHaystack())
                            horse.setEatingHaystack(false);
                        if (horse.isRearing())
                            horse.setRearing(false);
                    }

                    PathNavigator navigator = worker.getNavigator();
                    if (target != -1 && navigator.noPath()) {
                        pos = getPathPosition(target, true);

                        Path path = navigator.func_179680_a(new BlockPos(pos), 0); //was getPathToPos
                        navigator.setPath(path, 1D);
                    }
                }
            }
        }

        if (flag) {
            markDirty();
        }
    }
}
