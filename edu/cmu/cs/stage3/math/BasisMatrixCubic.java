package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector4d;

















public abstract class BasisMatrixCubic
  implements Cubic
{
  private Matrix4d m_h;
  private Vector4d m_g;
  
  protected BasisMatrixCubic(Matrix4d h, Vector4d g)
  {
    m_h = h;
    m_g = g;
  }
  
  public double evaluate(double t) { double ttt = t * t * t;
    double tt = t * t;
    return (ttt * m_h.m00 + tt * m_h.m10 + t * m_h.m20 + m_h.m30) * m_g.x + 
      (ttt * m_h.m01 + tt * m_h.m11 + t * m_h.m21 + m_h.m31) * m_g.y + 
      (ttt * m_h.m02 + tt * m_h.m12 + t * m_h.m22 + m_h.m32) * m_g.z + 
      (ttt * m_h.m03 + tt * m_h.m13 + t * m_h.m23 + m_h.m33) * m_g.w;
  }
  
  public double evaluateDerivative(double t) { double tt3 = t * t * 3.0D;
    double t2 = t * 2.0D;
    return (tt3 * m_h.m00 + t2 * m_h.m10 + m_h.m20) * m_g.x + 
      (tt3 * m_h.m01 + t2 * m_h.m11 + m_h.m21) * m_g.y + 
      (tt3 * m_h.m02 + t2 * m_h.m12 + m_h.m22) * m_g.z + 
      (tt3 * m_h.m03 + t2 * m_h.m13 + m_h.m23) * m_g.w;
  }
}
