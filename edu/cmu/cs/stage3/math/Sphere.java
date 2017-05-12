package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;


















public class Sphere
  implements Cloneable
{
  protected double m_radius;
  protected Vector3d m_center;
  
  public Sphere() { this(null, NaN.0D); }
  
  public Sphere(Vector3d center, double radius) {
    setCenter(center);
    setRadius(radius);
  }
  
  public Sphere(double x, double y, double z, double radius) { setCenter(new Vector3d(x, y, z));
    setRadius(radius);
  }
  
  public synchronized Object clone() {
    try {
      Sphere sphere = (Sphere)super.clone();
      sphere.setCenter(m_center);
      return sphere;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Sphere))) {
      Sphere s = (Sphere)o;
      

      return (m_center.equals(m_center)) && (m_radius == m_radius);
    }
    return false;
  }
  
  public double getRadius() {
    return m_radius;
  }
  
  public void setRadius(double radius) { m_radius = radius; }
  
  public Vector3d getCenter() {
    if (m_center != null) {
      return new Vector3d(m_center);
    }
    return null;
  }
  
  public void setCenter(Vector3d center) {
    if (center != null) {
      m_center = new Vector3d(center);
    } else {
      m_center = null;
    }
  }
  
  public void union(Sphere s) {
    if ((s != null) && (m_center != null))
      if (m_center != null) {
        Vector3d diagonal = new Vector3d(m_center);
        diagonal.sub(m_center);
        diagonal.normalize();
        Vector3d[] points = new Vector3d[4];
        points[0] = MathUtilities.add(m_center, MathUtilities.multiply(diagonal, m_radius));
        points[1] = MathUtilities.subtract(m_center, MathUtilities.multiply(diagonal, m_radius));
        points[2] = MathUtilities.add(m_center, MathUtilities.multiply(diagonal, m_radius));
        points[3] = MathUtilities.subtract(m_center, MathUtilities.multiply(diagonal, m_radius));
        double maxDistanceSquared = 0.0D;
        int maxDistanceI = 0;
        int maxDistanceJ = 1;
        for (int i = 0; i < 4; i++) {
          for (int j = i + 1; j < 4; j++) {
            double d2 = MathUtilities.subtract(points[i], points[j]).lengthSquared();
            if (d2 > maxDistanceSquared) {
              maxDistanceSquared = d2;
              maxDistanceI = i;
              maxDistanceJ = j;
            }
          }
        }
        Vector3d temp = MathUtilities.divide(MathUtilities.add(points[maxDistanceI], points[maxDistanceJ]), 2.0D);
        if ((!Double.isNaN(x)) && (!Double.isNaN(y)) && (!Double.isNaN(z))) {
          m_center = temp;
          m_radius = (Math.sqrt(maxDistanceSquared) / 2.0D);
        }
      } else {
        m_center = s.getCenter();
        m_radius = s.getRadius();
      }
  }
  
  public void transform(Matrix4d m) {
    if ((m_center != null) && (!Double.isNaN(m_radius)))
    {
      m_center.add(new Vector3(m30, m31, m32));
    }
  }
  
  public void scale(Matrix3d s) {
    if (s != null) {
      if (m_center != null) {
        m_center = MathUtilities.multiply(s, m_center);
      }
      


      m_radius *= s.getScale();
    }
  }
  
  public String toString()
  {
    String s = "edu.cmu.cs.stage3.math.Sphere[radius=" + m_radius + ",center=";
    if (m_center != null) {
      s = s + m_center + "]";
    } else {
      s = s + "null]";
    }
    return s;
  }
}
