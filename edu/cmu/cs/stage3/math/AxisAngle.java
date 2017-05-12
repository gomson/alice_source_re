package edu.cmu.cs.stage3.math;

import javax.vecmath.Vector3d;




















public class AxisAngle
  implements Cloneable
{
  protected Vector3d m_axis = MathUtilities.createZAxis();
  protected double m_angle = 0.0D;
  
  public AxisAngle() {}
  
  public AxisAngle(double x, double y, double z, double angle) { this(new Vector3d(x, y, z), angle); }
  
  public AxisAngle(double[] axis, double angle) {
    this(new Vector3d(axis), angle);
  }
  
  public AxisAngle(double[] array) { this(array[0], array[1], array[2], array[3]); }
  
  public AxisAngle(Vector3d axis, double angle) {
    m_axis = axis;
    m_angle = angle;
  }
  
  public AxisAngle(Matrix33 m) { setMatrix33(m); }
  
  public AxisAngle(Quaternion q) {
    setQuaternion(q);
  }
  
  public AxisAngle(EulerAngles ea) { setEulerAngles(ea); }
  
  public double getAngle() {
    return m_angle;
  }
  
  public void setAngle(double angle) { m_angle = angle; }
  
  public Vector3d getAxis() {
    if (m_axis != null) {
      return (Vector3d)m_axis.clone();
    }
    return null;
  }
  
  public void setAxis(Vector3d axis) {
    m_axis = axis;
  }
  
  public synchronized Object clone() {
    try {
      AxisAngle axisAngle = (AxisAngle)super.clone();
      axisAngle.setAxis((Vector3d)m_axis.clone());
      return axisAngle;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof AxisAngle))) {
      AxisAngle aa = (AxisAngle)o;
      
      return (m_axis.equals(m_axis)) && (m_angle == m_angle);
    }
    return false;
  }
  
  public double[] getArray() {
    double[] a = { m_axis.x, m_axis.y, m_axis.z, m_angle };
    return a;
  }
  
  public void setArray(double[] a) { m_axis.x = a[0];
    m_axis.y = a[1];
    m_axis.z = a[2];
    m_angle = a[3];
  }
  
  public Quaternion getQuaternion() { return new Quaternion(this); }
  
  public void setQuaternion(Quaternion q) {
    m_angle = (2.0D * Math.acos(w));
    m_axis.x = (2.0D * Math.asin(x));
    m_axis.y = (2.0D * Math.asin(y));
    m_axis.z = (2.0D * Math.asin(z));
  }
  
  public EulerAngles getEulerAngles() { return new EulerAngles(this); }
  
  public void setEulerAngles(EulerAngles ea)
  {
    setQuaternion(ea.getQuaternion());
  }
  
  public Matrix33 getMatrix33() { return new Matrix33(this); }
  
  public void setMatrix33(Matrix33 m)
  {
    setQuaternion(m.getQuaternion());
  }
  

  public String toString() { return "edu.cmu.cs.stage3.math.AxisAngle[axis.x=" + m_axis.x + ",axis.y=" + m_axis.y + ",axis.z=" + m_axis.z + ",angle=" + m_angle + "]"; }
  
  public static AxisAngle valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.AxisAngle[axis.x=", ",axis.y=", ",axis.z=", ",angle=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new AxisAngle(values);
  }
}
