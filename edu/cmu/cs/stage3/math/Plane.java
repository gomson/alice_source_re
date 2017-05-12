package edu.cmu.cs.stage3.math;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;



















public class Plane
  implements Cloneable
{
  private double m_a;
  private double m_b;
  private double m_c;
  private double m_d;
  
  public Plane() { this(0.0D, 0.0D, 0.0D, 0.0D); }
  
  public Plane(double a, double b, double c, double d) {
    m_a = a;
    m_b = b;
    m_c = c;
    m_d = d;
  }
  
  public Plane(double[] array) { this(array[0], array[1], array[2], array[3]); }
  
  public Plane(Vector3d position, Vector3d normal) {
    this(x, y, z, -(x * x + y * y + z * z));
  }
  
  public Plane(Vector3d v0, Vector3d v1, Vector3d v2) { this(v0, MathUtilities.normalizeV(MathUtilities.crossProduct(MathUtilities.normalizeV(MathUtilities.subtract(v2, v1)), MathUtilities.normalizeV(MathUtilities.subtract(v0, v1))))); }
  
  public synchronized Object clone()
  {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Plane))) {
      Plane plane = (Plane)o;
      return (m_a == m_a) && (m_b == m_b) && (m_c == m_c) && (m_d == m_d);
    }
    return false;
  }
  
  public double intersect(Ray ray)
  {
    Point3d p = ray.getOrigin();
    Vector3d d = ray.getDirection();
    
    double denom = m_a * x + m_b * y + m_c * z;
    if (denom == 0.0D) {
      return NaN.0D;
    }
    double numer = m_a * x + m_b * y + m_c * z + m_d;
    return -numer / denom;
  }
  


  public String toString() { return "edu.cmu.cs.stage3.math.Plane[a=" + m_a + ",b=" + m_b + ",c=" + m_c + ",d=" + m_d + "]"; }
  
  public static Plane valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Plane[a=", ",b=", ",c=", ",d=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new Plane(values);
  }
}
