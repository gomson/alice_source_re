package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.MathUtilities;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.math.Matrix44;
import edu.cmu.cs.stage3.math.Quaternion;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.math.Vector4;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;























public abstract class ReferenceFrame
  extends Container
{
  private static Transformable s_getTransformationHelperOffset = new Transformable();
  
  static { s_getTransformationHelperOffset.setName("s_getTransformationHelperOffset");
    s_getTransformationHelperOffset.setIsHelper(true); }
  
  public ReferenceFrame() {}
  public Matrix44 getTransformation(Vector3d offset, ReferenceFrame asSeenBy) { synchronized (s_getTransformationHelperOffset) { ReferenceFrame actual;
      ReferenceFrame actual;
      if (offset != null) {
        s_getTransformationHelperOffset.setParent(this);
        Matrix44 m = new Matrix44();
        m30 = x;
        m31 = y;
        m32 = z;
        s_getTransformationHelperOffset.setLocalTransformation(m);
        actual = s_getTransformationHelperOffset;
      } else {
        actual = this; }
      Matrix44 m;
      Matrix44 m;
      if (asSeenBy != null) {
        m = Matrix44.multiply(actual.getAbsoluteTransformation(), asSeenBy.getInverseAbsoluteTransformation());
      } else {
        m = new Matrix44(actual.getAbsoluteTransformation());
      }
      if (offset != null) {
        s_getTransformationHelperOffset.setParent(null);
      }
      return m;
    }
  }
  
  public Matrix44 getTransformation(ReferenceFrame asSeenBy) { return getTransformation(null, asSeenBy); }
  
  public Vector3 getPosition(Vector3d offset, ReferenceFrame asSeenBy) {
    return getTransformation(asSeenBy).getPosition();
  }
  
  public Vector3 getPosition(ReferenceFrame asSeenBy) { return getPosition(null, asSeenBy); }
  

  public Matrix33 getAxes(ReferenceFrame asSeenBy) { return getTransformation(asSeenBy).getAxes(); }
  
  public Vector3[] getOrientation(ReferenceFrame asSeenBy) {
    Matrix33 axes = getAxes(asSeenBy);
    Vector3[] array = { axes.getRow(2), axes.getRow(1) };
    return array;
  }
  
  public Quaternion getQuaternion(ReferenceFrame asSeenBy) { return getAxes(asSeenBy).getQuaternion(); }
  
  public Vector4d transformTo(Vector4d xyzw, ReferenceFrame to)
  {
    return MathUtilities.multiply(xyzw, getTransformation(to));
  }
  
  public Vector3d transformTo(Vector3d xyz, ReferenceFrame to) { return new Vector3(transformTo(new Vector4(xyz, 1.0D), to)); }
}
