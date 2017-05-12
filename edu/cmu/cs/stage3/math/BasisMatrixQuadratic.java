package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

















public abstract class BasisMatrixQuadratic
  implements Quadratic
{
  private Matrix3d m_h;
  protected Vector3d m_g;
  
  protected BasisMatrixQuadratic(Matrix3d h, Vector3d g)
  {
    m_h = h;
    m_g = g;
  }
  
  public double evaluate(double t) { double tt = t * t;
    return (tt * m_h.m00 + t * m_h.m10 + m_h.m20) * m_g.x + 
      (tt * m_h.m01 + t * m_h.m11 + m_h.m21) * m_g.y + 
      (tt * m_h.m02 + t * m_h.m12 + m_h.m22) * m_g.z;
  }
  
  public double evaluateDerivative(double t) { double t2 = t * 2.0D;
    return (t2 * m_h.m00 + m_h.m10) * m_g.x + 
      (t2 * m_h.m01 + m_h.m11) * m_g.y + 
      (t2 * m_h.m02 + m_h.m12) * m_g.z;
  }
}
