package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector4d;

















public class Vector4
  extends Vector4d
{
  public Vector4() {}
  
  public Vector4(double x, double y, double z, double w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  
  public Vector4(double[] v) { this(v[0], v[1], v[2], v[3]); }
  
  public Vector4(Tuple3d v, double w) {
    this(x, y, z, w);
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Vector4))) {
      Vector4 v = (Vector4)o;
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
  
  public void set(Vector4 other) { x = x;
    y = y;
    z = z;
    w = w;
  }
  
  public boolean equals(Vector4 v) { return (x == x) && (y == y) && (z == z) && (w == w); }
  
  public double getItem(int i) {
    switch (i) {
    case 0: 
      return x;
    case 1: 
      return y;
    case 2: 
      return z;
    case 3: 
      return w;
    }
    throw new IllegalArgumentException();
  }
  
  public void setItem(int i, double v) { switch (i) {
    case 0: 
      x = v;
      return;
    case 1: 
      y = v;
      return;
    case 2: 
      z = v;
      return;
    case 3: 
      w = v;
      return;
    }
    throw new IllegalArgumentException();
  }
  
  public static Vector4 negate(Vector4 v) { return new Vector4(-x, -y, -z, -w); }
  
  public static double dotProduct(Vector4 a, Vector4 b) {
    return x * x + y * y + z * z + w * w;
  }
  
  public static Vector4 multiply(Vector4d a, Matrix4d b) {
    Vector4 ab = new Vector4();
    x = (x * m00 + y * m10 + z * m20 + w * m30);
    y = (x * m01 + y * m11 + z * m21 + w * m31);
    z = (x * m02 + y * m12 + z * m22 + w * m32);
    w = (x * m03 + y * m13 + z * m23 + w * m33);
    return ab;
  }
  
  public static Vector4 multiply(Matrix4d a, Vector4d b) { Vector4 ab = new Vector4();
    x = (x * m00 + y * m01 + z * m02 + w * m03);
    y = (x * m10 + y * m11 + z * m12 + w * m13);
    z = (x * m20 + y * m21 + z * m22 + w * m23);
    w = (x * m30 + y * m31 + z * m32 + w * m33);
    return ab;
  }
  
  public void transform(Matrix4d m) { set(multiply(this, m)); }
  


  public String toString() { return "edu.cmu.cs.stage3.math.Vector4[x=" + x + ",y=" + y + ",z=" + z + ",w=" + w + "]"; }
  
  public static Vector4 valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Vector4[x=", ",y=", ",z=", ",w=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new Vector4(values);
  }
}
