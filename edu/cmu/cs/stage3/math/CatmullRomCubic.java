package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector4d;



















public class CatmullRomCubic
  extends BasisMatrixCubic
{
  private static final Matrix4d s_h = new Matrix4d(-0.5D, 1.5D, -1.5D, 0.5D, 1.0D, -2.5D, 2.0D, -0.5D, -0.5D, 0.0D, 0.5D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
  
  public CatmullRomCubic(Vector4d g) { super(s_h, g); }
  
  public CatmullRomCubic(double g0, double g1, double g2, double g3) {
    this(new Vector4d(g0, g1, g2, g3));
  }
}
