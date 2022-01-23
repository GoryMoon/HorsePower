package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum FacingToRotation {
    DOWN(new Vector3f(0, 0, 0)), //NOOP
    UP(new Vector3f(0, 0, 0)), //NOOP
    NORTH(new Vector3f(0, 0, 0)),
    SOUTH(new Vector3f(0, 180, 0)),
    WEST(new Vector3f(0, 90, 0)),
    EAST(new Vector3f(0, -90, 0));

    private final Vector3f rot;

    FacingToRotation(Vector3f rot) {
        this.rot = rot;
    }

    public static FacingToRotation get(Direction forward) {
        return values()[forward.ordinal()];
    }

    public Vector3f getRot() {
        return rot;
    }

    public void glRotateCurrentMat() {
        GlStateManager.rotatef(rot.getX(), 1, 0, 0);
        GlStateManager.rotatef(rot.getY(), 0, 1, 0);
        GlStateManager.rotatef(rot.getZ(), 0, 0, 1);
    }


}