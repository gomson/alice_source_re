package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;


















public class Box
  implements Cloneable
{
  protected Vector3d m_minimum = null;
  protected Vector3d m_maximum = null;
  
  public Box() {}
  
  public Box(Vector3d minimum, Vector3d maximum) {
    setMinimum(minimum);
    setMaximum(maximum);
  }
  
  public Box(double minimumX, double minimumY, double minimumZ, double maximumX, double maximumY, double maximumZ) { setMinimum(new Vector3d(minimumX, minimumY, minimumZ));
    setMaximum(new Vector3d(maximumX, maximumY, maximumZ));
  }
  
  public synchronized Object clone() {
    try {
      Box box = (Box)super.clone();
      box.setMinimum(m_minimum);
      box.setMaximum(m_maximum);
      return box;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Box))) {
      Box box = (Box)o;
      return (m_minimum.equals(m_minimum)) && (m_maximum.equals(m_maximum));
    }
    return false;
  }
  
  public Vector3d[] getCorners()
  {
    Vector3d[] corners = new Vector3d[8];
    corners[0] = new Vector3d(m_minimum.x, m_minimum.y, m_minimum.z);
    corners[1] = new Vector3d(m_minimum.x, m_minimum.y, m_maximum.z);
    corners[2] = new Vector3d(m_minimum.x, m_maximum.y, m_minimum.z);
    corners[3] = new Vector3d(m_minimum.x, m_maximum.y, m_maximum.z);
    corners[4] = new Vector3d(m_maximum.x, m_minimum.y, m_minimum.z);
    corners[5] = new Vector3d(m_maximum.x, m_minimum.y, m_maximum.z);
    corners[6] = new Vector3d(m_maximum.x, m_maximum.y, m_minimum.z);
    corners[7] = new Vector3d(m_maximum.x, m_maximum.y, m_maximum.z);
    return corners;
  }
  
  public Vector3d getMinimum() { if (m_minimum != null) {
      return new Vector3d(m_minimum);
    }
    return null;
  }
  
  public void setMinimum(Vector3d minimum) {
    if (minimum != null) {
      m_minimum = new Vector3d(minimum);
    } else
      m_minimum = null;
  }
  
  public Vector3d getMaximum() {
    if (m_maximum != null) {
      return new Vector3d(m_maximum);
    }
    return null;
  }
  
  public void setMaximum(Vector3d maximum) {
    if (maximum != null) {
      m_maximum = new Vector3d(maximum);
    } else {
      m_maximum = null;
    }
  }
  
  public Vector3d getCenter() {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d((m_minimum.x + m_maximum.x) / 2.0D, (m_minimum.y + m_maximum.y) / 2.0D, (m_minimum.z + m_maximum.z) / 2.0D);
    }
    return null;
  }
  
  public Vector3d getCenterOfFrontFace()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d((m_minimum.x + m_maximum.x) / 2.0D, (m_minimum.y + m_maximum.y) / 2.0D, m_maximum.z);
    }
    return null;
  }
  
  public Vector3d getCenterOfBackFace()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d((m_minimum.x + m_maximum.x) / 2.0D, (m_minimum.y + m_maximum.y) / 2.0D, m_minimum.z);
    }
    return null;
  }
  
  public Vector3d getCenterOfLeftFace()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d(m_minimum.x, (m_minimum.y + m_maximum.y) / 2.0D, (m_minimum.z + m_maximum.z) / 2.0D);
    }
    return null;
  }
  
  public Vector3d getCenterOfRightFace()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d(m_maximum.x, (m_minimum.y + m_maximum.y) / 2.0D, (m_minimum.z + m_maximum.z) / 2.0D);
    }
    return null;
  }
  
  public Vector3d getCenterOfTopFace()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d((m_minimum.x + m_maximum.x) / 2.0D, m_maximum.y, (m_minimum.z + m_maximum.z) / 2.0D);
    }
    return null;
  }
  
  public Vector3d getCenterOfBottomFace()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return new Vector3d((m_minimum.x + m_maximum.x) / 2.0D, m_minimum.y, (m_minimum.z + m_maximum.z) / 2.0D);
    }
    return null;
  }
  
  public double getWidth()
  {
    if ((m_minimum != null) && (m_maximum != null)) {
      return m_maximum.x - m_minimum.x;
    }
    return 0.0D;
  }
  
  public double getHeight() {
    if ((m_minimum != null) && (m_maximum != null)) {
      return m_maximum.y - m_minimum.y;
    }
    return 0.0D;
  }
  
  public double getDepth() {
    if ((m_minimum != null) && (m_maximum != null)) {
      return m_maximum.z - m_minimum.z;
    }
    return 0.0D;
  }
  
  public void union(Box b) {
    if (b != null) {
      if (m_minimum != null) {
        if (m_minimum != null) {
          m_minimum.x = Math.min(m_minimum.x, m_minimum.x);
          m_minimum.y = Math.min(m_minimum.y, m_minimum.y);
          m_minimum.z = Math.min(m_minimum.z, m_minimum.z);
        } else {
          m_minimum = new Vector3d(m_minimum);
        }
      }
      if (m_maximum != null)
        if (m_maximum != null) {
          m_maximum.x = Math.max(m_maximum.x, m_maximum.x);
          m_maximum.y = Math.max(m_maximum.y, m_maximum.y);
          m_maximum.z = Math.max(m_maximum.z, m_maximum.z);
        } else {
          m_maximum = new Vector3d(m_maximum);
        }
    }
  }
  
  public void transform(Matrix4d m) {
    if ((m_minimum != null) && (m_maximum != null))
    {



















      m_minimum = MathUtilities.createVector3d(MathUtilities.multiply(m_minimum, 1.0D, m));
      m_maximum = MathUtilities.createVector3d(MathUtilities.multiply(m_maximum, 1.0D, m));
    }
  }
  
  public void scale(Matrix3d s) { if (s != null) {
      if (m_minimum != null) {
        s.transform(m_minimum);
      }
      if (m_maximum != null) {
        s.transform(m_maximum);
      }
    }
  }
  
  public String toString()
  {
    return "edu.cmu.cs.stage3.math.Box[minimum=" + m_minimum + ",maximum=" + m_maximum + "]";
  }
}
