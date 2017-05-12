package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

















public class Ray
  implements Cloneable
{
  protected Point3d m_origin = null;
  protected Vector3d m_direction = null;
  

  public Ray() { this(new Point3d(0.0D, 0.0D, 0.0D), new Vector3d(0.0D, 0.0D, 1.0D)); }
  
  public Ray(Point3d origin, Vector3d direction) {
    setOrigin(origin);
    setDirection(direction);
  }
  
  public synchronized Object clone() {
    try {
      Ray ray = (Ray)super.clone();
      ray.setOrigin(new Point3d(m_origin));
      ray.setDirection(new Vector3d(m_direction));
      return ray;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Ray))) {
      Ray ray = (Ray)o;
      return (m_origin.equals(m_origin)) && (m_direction.equals(m_direction));
    }
    return false;
  }
  
  public Point3d getOrigin() {
    if (m_origin != null) {
      return new Point3d(m_origin);
    }
    return null;
  }
  
  public void setOrigin(Point3d origin) {
    if (origin != null) {
      m_origin = new Point3d(origin);
    } else
      m_origin = null;
  }
  
  public Vector3d getDirection() {
    if (m_direction != null) {
      return new Vector3d(m_direction);
    }
    return null;
  }
  
  public void setDirection(Vector3d direction) {
    if (direction != null) {
      m_direction = new Vector3d(direction);
    } else {
      m_direction = null;
    }
  }
  
  public Vector3d getPoint(double t) {
    Vector3d p = new Vector3d(m_direction);
    p.scale(t);
    p.add(m_origin);
    return p;
  }
  
  public void transform(Matrix4d m) { Vector4d transformedOrigin = MathUtilities.multiply(m_origin.x, m_origin.y, m_origin.z, 1.0D, m);
    m_origin = MathUtilities.createPoint3d(transformedOrigin);
    Vector4d transformedDirection = MathUtilities.multiply(m_direction, 0.0D, m);
    w = 1.0D;
    m_direction = MathUtilities.createVector3d(transformedDirection);
  }
  
  public String toString() {
    return "edu.cmu.cs.stage3.math.Ray[origin=" + m_origin + ",direction=" + m_direction + "]";
  }
}
