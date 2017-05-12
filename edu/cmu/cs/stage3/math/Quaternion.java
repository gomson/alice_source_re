package edu.cmu.cs.stage3.math;

import javax.vecmath.Vector3d;




















public class Quaternion
  implements Cloneable, Interpolable
{
  public double x = 0.0D;
  public double y = 0.0D;
  public double z = 0.0D;
  public double w = 1.0D;
  
  public Quaternion() {}
  
  public Quaternion(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  
  public Quaternion(double[] v) { this(v[0], v[1], v[2], v[3]); }
  
  public Quaternion(Matrix33 m) {
    setMatrix33(m);
  }
  
  public Quaternion(AxisAngle aa) { setAxisAngle(aa); }
  
  public Quaternion(EulerAngles ea) {
    setEulerAngles(ea);
  }
  
  public synchronized Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Quaternion))) {
      Quaternion q = (Quaternion)o;
      return (x == x) && (y == y) && (z == z) && (w == w);
    }
    return false;
  }
  
  public double[] getArray() {
    double[] a = { x, y, z, w };
    return a;
  }
  
  public void setArray(double[] a) { x = a[0];
    y = a[1];
    z = a[2];
    w = a[3];
  }
  
  public boolean equals(Quaternion q) { return (x == x) && (y == y) && (z == z) && (w == w); }
  

  public AxisAngle getAxisAngle() { return new AxisAngle(this); }
  
  public void setAxisAngle(AxisAngle aa) {
    double halfAngle = aa.getAngle() * 0.5D;
    double cosHalfAngle = Math.cos(halfAngle);
    double sinHalfAngle = Math.sin(halfAngle);
    Vector3d normalizedAxis = Vector3.normalizeV(aa.getAxis());
    w = cosHalfAngle;
    x = (sinHalfAngle * m_axis.x);
    y = (sinHalfAngle * m_axis.y);
    z = (sinHalfAngle * m_axis.z);
  }
  
  public EulerAngles getEulerAngles() { return new EulerAngles(this); }
  
  public void setEulerAngles(EulerAngles ea) {
    Matrix33 m = new Matrix33();
    m.rotateX(pitch);
    m.rotateY(yaw);
    m.rotateZ(roll);
    setMatrix33(m);
  }
  












  public Matrix33 getMatrix33()
  {
    Matrix33 m = new Matrix33();
    m.setQuaternion(this);
    return m;
  }
  

  public void setMatrix33(Matrix33 m)
  {
    double tr = m00 + m11 + m22;
    if (tr > 0.0D) {
      double s = Math.sqrt(tr + 1.0D);
      w = (s * 0.5D);
      s = 0.5D / s;
      x = ((m21 - m12) * s);
      y = ((m02 - m20) * s);
      z = ((m10 - m01) * s);
    } else {
      int[] nxt = { 1, 2 };
      double[][] a = m.getMatrix();
      int i = 0;
      if (a[1][1] > a[0][0])
        i = 1;
      if (a[2][2] > a[i][i])
        i = 2;
      int j = nxt[i];
      int k = nxt[j];
      
      double s = Math.sqrt(a[i][i] - (a[j][j] + a[k][k]) + 1.0D);
      
      double[] q = new double[4];
      q[i] = (s * 0.5D);
      
      if (s != 0.0D) { s = 0.5D / s;
      }
      q[3] = ((a[k][j] - a[j][k]) * s);
      q[j] = ((a[j][i] + a[i][j]) * s);
      q[k] = ((a[k][i] + a[i][k]) * s);
      setArray(q);
    }
  }
  
  public void normalize() { double lengthSquared = x * x + y * y + z * z + w * w;
    if (lengthSquared != 1.0D) {
      double length = Math.sqrt(lengthSquared);
      x /= length;
      y /= length;
      z /= length;
      w /= length;
    }
  }
  
  public static Quaternion multiply(Quaternion a, Quaternion b) {
    double A = (w + x) * (w + x);
    double B = (z - y) * (y - z);
    double C = (x - w) * (y - z);
    double D = (y + z) * (x - w);
    double E = (x + z) * (x + y);
    double F = (x - z) * (x - y);
    double G = (w + y) * (w - z);
    double H = (w - y) * (w + z);
    
    Quaternion q = new Quaternion();
    w = (B + (-E - F + G + H) / 2.0D);
    x = (A - (E + F + G + H) / 2.0D);
    y = (-C + (E - F + G - H) / 2.0D);
    z = (-D + (E - F - G + H) / 2.0D);
    return q;
  }
  
  public static Quaternion interpolate(Quaternion a, Quaternion b, double portion) { if (portion <= 0.0D) {
      return (Quaternion)a.clone();
    }
    if (portion >= 1.0D) {
      return (Quaternion)b.clone();
    }
    

    double cosom = x * x + y * y + z * z + w * w;
    
    Quaternion b1;
    Quaternion b1;
    if (cosom < 0.0D) {
      cosom = -cosom;
      b1 = new Quaternion(-x, -y, -z, -w);
    } else {
      b1 = b;
    }
    
    double scale1;
    double scale0;
    double scale1;
    if (1.0D - cosom > Double.MIN_VALUE)
    {
      double omega = Math.acos(cosom);
      double sinom = Math.sin(omega);
      double scale0 = Math.sin((1.0D - portion) * omega) / sinom;
      scale1 = Math.sin(portion * omega) / sinom;
    }
    else
    {
      scale0 = 1.0D - portion;
      scale1 = portion;
    }
    
    Quaternion q = new Quaternion();
    x = (scale0 * x + scale1 * x);
    y = (scale0 * y + scale1 * y);
    z = (scale0 * z + scale1 * z);
    w = (scale0 * w + scale1 * w);
    return q;
  }
  
  public Interpolable interpolate(Interpolable b, double portion) { return interpolate(this, (Quaternion)b, portion); }
  


  public String toString() { return "edu.cmu.cs.stage3.math.Quaternion[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]"; }
  
  public static Quaternion valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Quaternion[x=", ",y=", ",z=", ",w=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new Quaternion(values);
  }
}
