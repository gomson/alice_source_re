package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;



















public class BezierQuadratic
  extends BasisMatrixQuadratic
{
  private static final Matrix3d s_h = new Matrix3d(1.0D, -2.0D, 1.0D, -2.0D, 2.0D, 0.0D, 1.0D, 0.0D, 0.0D);
  
  public BezierQuadratic(Vector3d g) { super(s_h, g); }
  
  public BezierQuadratic(double g0, double g1, double g2) {
    this(new Vector3d(g0, g1, g2));
  }
}
