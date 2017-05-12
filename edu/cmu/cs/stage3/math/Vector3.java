package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector3d;















public class Vector3
  extends Vector3d
  implements Interpolable
{
  public static final Vector3 ZERO = new Vector3(0.0D, 0.0D, 0.0D);
  public static final Vector3 X_AXIS = new Vector3(1.0D, 0.0D, 0.0D);
  public static final Vector3 X_AXIS_NEGATIVE = new Vector3(-1.0D, 0.0D, 0.0D);
  public static final Vector3 Y_AXIS = new Vector3(0.0D, 1.0D, 0.0D);
  public static final Vector3 Y_AXIS_NEGATIVE = new Vector3(0.0D, -1.0D, 0.0D);
  public static final Vector3 Z_AXIS = new Vector3(0.0D, 0.0D, 1.0D);
  public static final Vector3 Z_AXIS_NEGATIVE = new Vector3(0.0D, 0.0D, -1.0D);
  
  public Vector3() {}
  
  public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vector3(double[] v) { this(v[0], v[1], v[2]); }
  
  public Vector3(Tuple3d t) {
    this(x, y, z);
  }
  
  public Vector3(Tuple4d t) { this(x / w, y / w, z / w); }
  
  public double getItem(int i) {
    switch (i) {
    case 0: 
      return x;
    case 1: 
      return y;
    case 2: 
      return z;
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
    }
    throw new IllegalArgumentException();
  }
  
  public double[] getArray() { double[] a = { x, y, z };
    return a;
  }
  
  public void setArray(double[] a) { x = a[0];
    y = a[1];
    z = a[2];
  }
  
  public void add(Vector3 v) { x += x;
    y += y;
    z += z;
  }
  
  public static Vector3 add(Vector3d a, Vector3d b) { return new Vector3(x + x, y + y, z + z); }
  
  public void subtract(Vector3 v) {
    x -= x;
    y -= y;
    z -= z;
  }
  
  public static Vector3 subtract(Vector3d a, Vector3d b) { return new Vector3(x - x, y - y, z - z); }
  

  public static Vector3 negate(Vector3d v) { return new Vector3(-x, -y, -z); }
  
  public void multiply(double scalar) {
    x *= scalar;
    y *= scalar;
    z *= scalar;
  }
  
  public void multiply(Vector3d scalar) { x *= x;
    y *= y;
    z *= z;
  }
  
  public static Vector3 multiply(Vector3d v, double scalar) { return new Vector3(x * scalar, y * scalar, z * scalar); }
  
  public static Vector3 multiply(Vector3d v, Vector3 scalar) {
    return new Vector3(x * x, y * y, z * z);
  }
  
  public void divide(double divisor) { multiply(1.0D / divisor); }
  
  public void divide(Vector3d divisor) {
    x /= x;
    y /= y;
    z /= z;
  }
  
  public static Vector3 divide(Vector3d v, double divisor) { return multiply(v, 1.0D / divisor); }
  
  public static Vector3 divide(Vector3d numerator, Vector3d divisor) {
    return new Vector3(x / x, y / y, z / z);
  }
  
  public void invert() {
    x = (1.0D / x);
    y = (1.0D / y);
    z = (1.0D / z);
  }
  
  public static Vector3 invert(Vector3d v) { return new Vector3(1.0D / x, 1.0D / y, 1.0D / z); }
  
  public static Vector3 normalizeV(Vector3d v) {
    Vector3 nv = new Vector3(x, y, z);
    nv.normalize();
    return nv;
  }
  

  public static double getLengthSquared(double x, double y, double z) { return x * x + y * y + z * z; }
  
  public static double getLength(double x, double y, double z) {
    double lengthSquared = getLengthSquared(x, y, z);
    if (lengthSquared == 1.0D) {
      return 1.0D;
    }
    return Math.sqrt(lengthSquared);
  }
  
  public double getLengthSquared() {
    return getLengthSquared(x, y, z);
  }
  
  public double getLength() { return getLength(x, y, z); }
  
  public static double dotProduct(Vector3d a, Vector3d b) {
    return x * x + y * y + z * z;
  }
  
  public static Vector3 crossProduct(Vector3d a, Vector3d b) { return new Vector3(y * z - z * y, z * x - x * z, x * y - y * x); }
  
  public static Vector3 interpolate(Vector3d a, Vector3d b, double portion) {
    return new Vector3(x + (x - x) * portion, y + (y - y) * portion, z + (z - z) * portion);
  }
  
  public Interpolable interpolate(Interpolable b, double portion) { return interpolate(this, (Vector3)b, portion); }
  
  public Vector3 projectOnto(Vector3d b)
  {
    return multiply(b, dotProduct(b, this) / dotProduct(b, b));
  }
  
  public static Vector3 projectOnto(Vector3d a, Vector3d b) { return multiply(b, dotProduct(b, a) / dotProduct(b, b)); }
  
  public static Vector3 multiply(Matrix3d a, Vector3d b) {
    double x = m00 * x + m01 * y + m02 * z;
    double y = m10 * x + m11 * y + m12 * z;
    double z = m20 * x + m21 * y + m22 * z;
    return new Vector3(x, y, z);
  }
  
  public static Vector3 multiply(Vector3d a, Matrix4d b) { Vector3 ab = new Vector3();
    x = (x * m00 + y * m10 + z * m20);
    y = (x * m01 + y * m11 + z * m21);
    z = (x * m02 + y * m12 + z * m22);
    return ab;
  }
  
  public static Vector3 combine(Vector3d a, Vector3d b, double asc1, double bsc1) {
    Vector3 ab = new Vector3();
    x = (asc1 * x + bsc1 * x);
    y = (asc1 * y + bsc1 * y);
    z = (asc1 * z + bsc1 * z);
    return ab;
  }
  


  public String toString() { return "edu.cmu.cs.stage3.math.Vector3[x=" + x + ",y=" + y + ",z=" + z + "]"; }
  
  public static Vector3 valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Vector3[x=", ",y=", ",z=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new Vector3(values);
  }
}
