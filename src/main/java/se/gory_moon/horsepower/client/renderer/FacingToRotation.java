package se.gory_moon.horsepower.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

@OnlyIn(Dist.CLIENT)
public enum FacingToRotation {
    DOWN	( new Vector3f(	0,		0,		0	) ), //NOOP
    UP		( new Vector3f(	0,		0,		0	) ), //NOOP
    NORTH	( new Vector3f(	0,		0,		0	) ),
    SOUTH	( new Vector3f(	0,		180,	    0	) ),
    WEST    ( new Vector3f(	0,		90,		0	) ),
    EAST	( new Vector3f(	0,		-90,	    0	) );

    private final Vector3f rot;
    private final Matrix4f mat;

    FacingToRotation( Vector3f rot ) {
        this.rot = rot;
        this.mat = new Matrix4f();
        this.mat.rotX((float) Math.toRadians( rot.x ));
        this.mat.rotY((float) Math.toRadians( rot.y ));
        this.mat.rotZ((float) Math.toRadians( rot.z ));
    }

    public Vector3f getRot() {
        return rot;
    }

    public Matrix4f getMat() {
        return new Matrix4f( this.mat );
    }

    public void glRotateCurrentMat() {
        GlStateManager.rotatef( rot.x, 1, 0, 0 );
        GlStateManager.rotatef( rot.y, 0, 1, 0 );
        GlStateManager.rotatef( rot.z, 0, 0, 1 );
    }

    public Direction rotate(Direction facing ) {
        return TRSRTransformation.rotate( mat, facing );
    }

    public Direction resultingRotate(Direction facing ) {
        for( Direction face : Direction.values() ) {
            if( rotate( face ) == facing ) {
                return face;
            }
        }
        return null;
    }

    public static FacingToRotation get(Direction forward) {
        return values()[forward.ordinal()];
    }

}