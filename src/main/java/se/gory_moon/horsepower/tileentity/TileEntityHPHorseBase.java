package se.gory_moon.horsepower.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import se.gory_moon.horsepower.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TileEntityHPHorseBase extends TileEntityHPBase implements ITickable{

    protected static double[][] path = {{-1.5, -1.5}, {0, -1.5}, {1, -1.5}, {1, 0}, {1, 1}, {0, 1}, {-1.5, 1}, {-1.5, 0}};
    protected AxisAlignedBB[] searchAreas = new AxisAlignedBB[8];
    protected List<BlockPos> searchPos = null;
    protected int origin = -1;
    protected int target = origin;

    protected boolean hasWorker = false;
    protected EntityCreature worker;
    protected NBTTagCompound nbtWorker;

    protected boolean valid = false;
    protected int validationTimer = 0;
    protected int locateHorseTimer = 0;
    protected boolean running = true;
    protected boolean wasRunning = false;

    public TileEntityHPHorseBase(int inventorySize) {
        super(inventorySize);
    }

    public abstract boolean validateArea();

    public abstract boolean targetReached();

    public abstract int getPositionOffset();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        target = compound.getInteger("target");
        origin = compound.getInteger("origin");
        hasWorker = compound.getBoolean("hasWorker");

        if (hasWorker && compound.hasKey("leash", 10)) {
            nbtWorker = compound.getCompoundTag("leash");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("target", target);
        compound.setInteger("origin", origin);
        compound.setBoolean("hasWorker", hasWorker);

        if (this.worker != null) {
            if (nbtWorker == null) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                UUID uuid = worker.getUniqueID();
                nbtTagCompound.setUniqueId("UUID", uuid);
                nbtWorker = nbtTagCompound;
            }

            compound.setTag("leash", nbtWorker);
        }

        return super.writeToNBT(compound);
    }

    public void setWorker(EntityCreature newWorker) {
        hasWorker = true;
        worker = newWorker;
        worker.setHomePosAndDistance(pos, 3);
        target = getClosestTarget();
        if (worker != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            UUID uuid = worker.getUniqueID();
            nbtTagCompound.setUniqueId("UUID", uuid);
            nbtWorker = nbtTagCompound;
        }
    }

    public void setWorkerToPlayer(EntityPlayer player) {
        if (hasWorker() && worker.canBeLeashedTo(player)) {
            hasWorker = false;
            worker.detachHome();
            worker.setLeashedToEntity(player, true);
            worker = null;
            nbtWorker = null;
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

    private Vec3d getPathPosition(int i) {
        double x = pos.getX() + path[i][0] * 2;
        double y = pos.getY() + getPositionOffset();
        double z = pos.getZ() + path[i][1] * 2;
        return new Vec3d(x, y, z);
    }

    protected int getClosestTarget() {
        if (hasWorker()) {
            double dist = Double.MAX_VALUE;
            int closest = 0;

            for (int i = 0; i < path.length; i++) {
                Vec3d pos = getPathPosition(i);
                double x = pos.x;
                double y = pos.y;
                double z = pos.z;

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

    @Override
    public void update() {
        validationTimer--;
        if (validationTimer <= 0) {
            valid = validateArea();
            if (valid)
                validationTimer = 220;
            else
                validationTimer = 60;
        }

        if (!hasWorker())
            locateHorseTimer--;
        if (!hasWorker() && nbtWorker != null && locateHorseTimer <= 0) {
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
        if (locateHorseTimer <= 0)
            locateHorseTimer = 220;

        boolean flag = false;

        if (!world.isRemote && valid) {
            if (!running && canWork()) {
                running = true;
            } else if (running && !canWork()){
                running = false;
            }

            if (running != wasRunning) {
                target = getClosestTarget();
                wasRunning = running;
            }

            if (hasWorker()) {
                if (running) {

                    Vec3d pos = getPathPosition(target);
                    double x = pos.x;
                    double y = pos.y;
                    double z = pos.z;

                    if (searchAreas[target] == null)
                        searchAreas[target] = new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D);

                    if (worker.getEntityBoundingBox().intersects(searchAreas[target])) {
                        int next = target + 1;
                        int previous = target -1;
                        if (next >= path.length)
                            next = 0;
                        if (previous < 0)
                            previous = path.length - 1;

                        if (origin != target && target != previous) {
                            origin = target;
                            flag = targetReached();
                        }
                        target = next;
                    }

                    if (worker instanceof AbstractHorse && ((AbstractHorse)worker).isEatingHaystack()) {
                        ((AbstractHorse)worker).setEatingHaystack(false);
                    }

                    if (target != -1 && worker.getNavigator().noPath()) {
                        pos = getPathPosition(target);
                        x = pos.x;
                        y = pos.y;
                        z = pos.z;

                        worker.getNavigator().tryMoveToXYZ(x, y, z, 1D);
                    }

                }
            }
        }

        if (flag) {
            markDirty();
        }
    }
}
