package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector4d;



















public class HermiteCubic
  extends BasisMatrixCubic
{
  private static final Matrix4d s_h = new Matrix4d(2.0D, -2.0D, 1.0D, 1.0D, -3.0D, 3.0D, -2.0D, -1.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D);
  
  public HermiteCubic(Vector4d g) { super(s_h, g); }
  
  public HermiteCubic(double g0, double g1, double g2, double g3) {
    this(new Vector4d(g0, g1, g2, g3));
  }
}
