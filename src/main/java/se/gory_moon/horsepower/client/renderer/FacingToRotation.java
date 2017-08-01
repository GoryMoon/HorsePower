package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

@SideOnly(Side.CLIENT)
public enum FacingToRotation {
    DOWN	( new Vector3f(	0,		0,		0	) ), //NOOP
    UP		( new Vector3f(	0,		0,		0	) ), //NOOP
    NORTH	( new Vector3f(	0,		0,		0	) ),
    SOUTH	( new Vector3f(	0,		180,	    0	) ),
    WEST    ( new Vector3f(	0,		90,		0	) ),
    EAST	( new Vector3f(	0,		-90,	    0	) );

    private final Vector3f rot;
    private final Matrix4f mat;

    FacingToRotation( Vector3f rot )
    {
        this.rot = rot;
        this.mat = TRSRTransformation.toVecmath( new org.lwjgl.util.vector.Matrix4f().rotate( (float) Math.toRadians( rot.x ), new org.lwjgl.util.vector.Vector3f( 1, 0, 0 ) ).rotate( (float) Math.toRadians( rot.y ), new org.lwjgl.util.vector.Vector3f( 0, 1, 0 ) ).rotate( (float) Math.toRadians( rot.z ), new org.lwjgl.util.vector.Vector3f( 0, 0, 1 ) ) );
    }

    public Vector3f getRot()
    {
        return rot;
    }

    public Matrix4f getMat()
    {
        return new Matrix4f( this.mat );
    }

    public void glRotateCurrentMat()
    {
        GlStateManager.rotate( rot.x, 1, 0, 0 );
        GlStateManager.rotate( rot.y, 0, 1, 0 );
        GlStateManager.rotate( rot.z, 0, 0, 1 );
    }

    public EnumFacing rotate( EnumFacing facing )
    {
        return TRSRTransformation.rotate( mat, facing );
    }

    public EnumFacing resultingRotate( EnumFacing facing )
    {
        for( EnumFacing face : EnumFacing.values() )
        {
            if( rotate( face ) == facing )
            {
                return face;
            }
        }
        return null;
    }

    public static FacingToRotation get( EnumFacing forward)
    {
        return values()[forward.ordinal()];
    }

}