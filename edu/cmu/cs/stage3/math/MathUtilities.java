package edu.cmu.cs.stage3.math;
import javax.vecmath.Vector3d;

public class MathUtilities { public MathUtilities() {} private static final javax.vecmath.Matrix4d IDENTITY_MATRIX_4D = new javax.vecmath.Matrix4d();
  private static final javax.vecmath.Matrix3d IDENTITY_MATRIX_3D = new javax.vecmath.Matrix3d();
  private static final Vector3d X_AXIS = new Vector3d(1.0D, 0.0D, 0.0D);
  private static final Vector3d Y_AXIS = new Vector3d(0.0D, 1.0D, 0.0D);
  private static final Vector3d Z_AXIS = new Vector3d(0.0D, 0.0D, 1.0D);
  private static final Vector3d NEGATIVE_X_AXIS = new Vector3d(-1.0D, 0.0D, 0.0D);
  private static final Vector3d NEGATIVE_Y_AXIS = new Vector3d(0.0D, -1.0D, 0.0D);
  private static final Vector3d NEGATIVE_Z_AXIS = new Vector3d(0.0D, 0.0D, -1.0D);
  
  public static javax.vecmath.Matrix4d getIdentityMatrix4d() {
    IDENTITY_MATRIX_4D.setIdentity();
    return IDENTITY_MATRIX_4D;
  }
  
  public static javax.vecmath.Matrix3d getIdentityMatrix3d() { IDENTITY_MATRIX_3D.setIdentity();
    return IDENTITY_MATRIX_3D;
  }
  
  public static Vector3d getXAxis() {
    X_AXISx = 1.0D;
    X_AXISy = (X_AXIS.z = 0.0D);
    return X_AXIS;
  }
  
  public static Vector3d getYAxis() { Y_AXISy = 1.0D;
    Y_AXISx = (Y_AXIS.z = 0.0D);
    return Y_AXIS;
  }
  
  public static Vector3d getZAxis() { Z_AXISz = 1.0D;
    Z_AXISx = (Z_AXIS.y = 0.0D);
    return Z_AXIS;
  }
  
  public static Vector3d getNegativeXAxis() {
    NEGATIVE_X_AXISx = -1.0D;
    NEGATIVE_X_AXISy = (NEGATIVE_X_AXIS.z = 0.0D);
    return NEGATIVE_X_AXIS;
  }
  
  public static Vector3d getNegativeYAxis() { NEGATIVE_Y_AXISy = -1.0D;
    NEGATIVE_Y_AXISx = (NEGATIVE_Y_AXIS.z = 0.0D);
    return NEGATIVE_Y_AXIS;
  }
  
  public static Vector3d getNegativeZAxis() { NEGATIVE_Z_AXISz = 1.0D;
    NEGATIVE_Z_AXISx = (Z_AXIS.y = 0.0D);
    return NEGATIVE_Z_AXIS;
  }
  
  public static javax.vecmath.Matrix4d createIdentityMatrix4d() {
    javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
    m.setIdentity();
    return m;
  }
  
  public static javax.vecmath.Matrix3d createIdentityMatrix3d() { javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();
    m.setIdentity();
    return m;
  }
  
  public static Vector3d createXAxis() { return new Vector3d(1.0D, 0.0D, 0.0D); }
  
  public static Vector3d createYAxis() {
    return new Vector3d(0.0D, 1.0D, 0.0D);
  }
  
  public static Vector3d createZAxis() { return new Vector3d(0.0D, 0.0D, 1.0D); }
  
  public static Vector3d createNegativeXAxis() {
    return new Vector3d(-1.0D, 0.0D, 0.0D);
  }
  
  public static Vector3d createNegativeYAxis() { return new Vector3d(0.0D, -1.0D, 0.0D); }
  
  public static Vector3d createNegativeZAxis() {
    return new Vector3d(0.0D, 0.0D, -1.0D);
  }
  
  public static Vector3d createVector3d(javax.vecmath.Tuple4d t) {
    return new Vector3d(x / w, y / w, z / w);
  }
  
  public static javax.vecmath.Vector4d createVector4d(javax.vecmath.Tuple3d t, double tW) { return new javax.vecmath.Vector4d(x, y, z, tW); }
  
  public static javax.vecmath.Point3d createPoint3d(javax.vecmath.Tuple4d t) {
    return new javax.vecmath.Point3d(x / w, y / w, z / w);
  }
  
  public static javax.vecmath.Point4d createPoint4d(javax.vecmath.Tuple3d t, double tW) { return new javax.vecmath.Point4d(x, y, z, tW); }
  
  public static double getItem(Vector3d vector, int i)
  {
    switch (i) {
    case 0: 
      return x;
    case 1: 
      return y;
    case 2: 
      return z;
    }
    throw new IndexOutOfBoundsException();
  }
  
  public static void setItem(Vector3d vector, int i, double value) {
    switch (i) {
    case 0: 
      x = value;
      return;
    case 1: 
      y = value;
      return;
    case 2: 
      z = value;
      return;
    }
    throw new IndexOutOfBoundsException();
  }
  
  public static Vector3d add(Vector3d a, Vector3d b) {
    return new Vector3d(x + x, y + y, z + z);
  }
  
  public static Vector3d subtract(Vector3d a, Vector3d b) { return new Vector3d(x - x, y - y, z - z); }
  
  public static Vector3d negate(Vector3d v) {
    return new Vector3d(-x, -y, -z);
  }
  
  public static Vector3d multiply(Vector3d v, double scalar) { return new Vector3d(x * scalar, y * scalar, z * scalar); }
  
  public static Vector3d multiply(Vector3d v, Vector3d scalar) {
    return new Vector3d(x * x, y * y, z * z);
  }
  
  public static Vector3d divide(Vector3d v, double divisor) { return multiply(v, 1.0D / divisor); }
  
  public static Vector3d divide(Vector3d numerator, Vector3d divisor) {
    return new Vector3d(x / x, y / y, z / z);
  }
  

  public static Vector3d invert(Vector3d v) { return new Vector3d(1.0D / x, 1.0D / y, 1.0D / z); }
  
  public static Vector3d normalizeV(Vector3d v) {
    Vector3d nv = new Vector3d(v);
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
  
  public static double getLengthSquared(Vector3d v) {
    return getLengthSquared(x, y, z);
  }
  
  public static double getLength(Vector3d v) { return getLength(x, y, z); }
  
  public static double dotProduct(Vector3d a, Vector3d b)
  {
    return x * x + y * y + z * z;
  }
  
  public static Vector3d crossProduct(Vector3d a, Vector3d b) { return new Vector3d(y * z - z * y, z * x - x * z, x * y - y * x); }
  
  public static Vector3d interpolate(Vector3d a, Vector3d b, double portion) {
    return new Vector3d(x + (x - x) * portion, y + (y - y) * portion, z + (z - z) * portion);
  }
  

  public static Vector3d projectOnto(Vector3d a, Vector3d b) { return multiply(b, dotProduct(b, a) / dotProduct(b, b)); }
  
  public static Vector3d multiply(javax.vecmath.Matrix3d a, Vector3d b) {
    double x = m00 * x + m01 * y + m02 * z;
    double y = m10 * x + m11 * y + m12 * z;
    double z = m20 * x + m21 * y + m22 * z;
    return new Vector3d(x, y, z);
  }
  
  public static Vector3d multiply(Vector3d a, javax.vecmath.Matrix4d b) { Vector3d ab = new Vector3d();
    x = (x * m00 + y * m10 + z * m20);
    y = (x * m01 + y * m11 + z * m21);
    z = (x * m02 + y * m12 + z * m22);
    return ab;
  }
  
  public static Vector3d combine(Vector3d a, Vector3d b, double asc1, double bsc1) {
    Vector3d ab = new Vector3d();
    x = (asc1 * x + bsc1 * x);
    y = (asc1 * y + bsc1 * y);
    z = (asc1 * z + bsc1 * z);
    return ab;
  }
  
  public static double getItem(javax.vecmath.Vector4d vector, int i)
  {
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
    throw new IndexOutOfBoundsException();
  }
  
  public static void setItem(javax.vecmath.Vector4d vector, int i, double value) {
    switch (i) {
    case 0: 
      x = value;
      return;
    case 1: 
      y = value;
      return;
    case 2: 
      z = value;
      return;
    case 3: 
      w = value;
      return;
    }
    throw new IndexOutOfBoundsException();
  }
  
  public static javax.vecmath.Vector4d negate(javax.vecmath.Vector4d v) {
    return new javax.vecmath.Vector4d(-x, -y, -z, -w);
  }
  
  public static double dotProduct(javax.vecmath.Vector4d a, javax.vecmath.Vector4d b) { return x * x + y * y + z * z + w * w; }
  
  public static javax.vecmath.Vector4d multiply(double aX, double aY, double aZ, double aW, javax.vecmath.Matrix4d b)
  {
    javax.vecmath.Vector4d ab = new javax.vecmath.Vector4d();
    x = (aX * m00 + aY * m10 + aZ * m20 + aW * m30);
    y = (aX * m01 + aY * m11 + aZ * m21 + aW * m31);
    z = (aX * m02 + aY * m12 + aZ * m22 + aW * m32);
    w = (aX * m03 + aY * m13 + aZ * m23 + aW * m33);
    return ab;
  }
  
  public static javax.vecmath.Vector4d multiply(javax.vecmath.Vector4d a, javax.vecmath.Matrix4d b) { return multiply(x, y, z, w, b); }
  
  public static javax.vecmath.Vector4d multiply(Vector3d a, double aW, javax.vecmath.Matrix4d b) {
    return multiply(x, y, z, aW, b);
  }
  
  public static javax.vecmath.Vector4d multiply(javax.vecmath.Matrix4d a, double bX, double bY, double bZ, double bW) {
    javax.vecmath.Vector4d ab = new javax.vecmath.Vector4d();
    x = (bX * m00 + bY * m01 + bZ * m02 + bW * m03);
    y = (bX * m10 + bY * m11 + bZ * m12 + bW * m13);
    z = (bX * m20 + bY * m21 + bZ * m22 + bW * m23);
    w = (bX * m30 + bY * m31 + bZ * m32 + bW * m33);
    return ab;
  }
  
  public static javax.vecmath.Vector4d multiply(javax.vecmath.Matrix4d a, javax.vecmath.Vector4d b) { return multiply(a, x, y, z, w); }
  
  public static javax.vecmath.Vector4d multiply(javax.vecmath.Matrix4d a, Vector3d b, double bW) {
    return multiply(a, x, y, z, bW);
  }
  
  public static Vector3d getRow(javax.vecmath.Matrix3d m, int i)
  {
    switch (i) {
    case 0: 
      return new Vector3d(m00, m01, m02);
    case 1: 
      return new Vector3d(m10, m11, m12);
    case 2: 
      return new Vector3d(m20, m21, m22);
    }
    throw new IndexOutOfBoundsException();
  }
  
  public static void setRow(javax.vecmath.Matrix3d m, int i, Vector3d v) {
    switch (i) {
    case 0: 
      m00 = x;
      m01 = y;
      m02 = z;
      break;
    case 1: 
      m10 = x;
      m11 = y;
      m12 = z;
      break;
    case 2: 
      m20 = x;
      m21 = y;
      m22 = z;
      break;
    default: 
      throw new IndexOutOfBoundsException(); }
  }
  
  public static Vector3d getColumn(javax.vecmath.Matrix3d m, int i) {
    switch (i) {
    case 0: 
      return new Vector3d(m00, m10, m20);
    case 1: 
      return new Vector3d(m01, m11, m21);
    case 2: 
      return new Vector3d(m02, m12, m22);
    }
    throw new IndexOutOfBoundsException();
  }
  
  public static void setColumn(javax.vecmath.Matrix3d m, int i, Vector3d v) {
    switch (i) {
    case 0: 
      m00 = x;
      m10 = y;
      m20 = z;
      break;
    case 1: 
      m01 = x;
      m11 = y;
      m21 = z;
      break;
    case 2: 
      m02 = x;
      m12 = y;
      m22 = z;
      break;
    default: 
      throw new IndexOutOfBoundsException();
    }
  }
  
  public static javax.vecmath.Matrix3d multiply(javax.vecmath.Matrix3d a, javax.vecmath.Matrix3d b)
  {
    javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();
    m00 = (m00 * m00 + m01 * m10 + m02 * m20);
    m01 = (m00 * m01 + m01 * m11 + m02 * m21);
    m02 = (m00 * m02 + m01 * m12 + m02 * m22);
    
    m10 = (m10 * m00 + m11 * m10 + m12 * m20);
    m11 = (m10 * m01 + m11 * m11 + m12 * m21);
    m12 = (m10 * m02 + m11 * m12 + m12 * m22);
    
    m20 = (m20 * m00 + m21 * m10 + m22 * m20);
    m21 = (m20 * m01 + m21 * m11 + m22 * m21);
    m22 = (m20 * m02 + m21 * m12 + m22 * m22);
    return m;
  }
  
  public static javax.vecmath.Matrix4d multiply(javax.vecmath.Matrix4d a, javax.vecmath.Matrix4d b) {
    javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
    m00 = (m00 * m00 + m01 * m10 + m02 * m20 + m03 * m30);
    m01 = (m00 * m01 + m01 * m11 + m02 * m21 + m03 * m31);
    m02 = (m00 * m02 + m01 * m12 + m02 * m22 + m03 * m32);
    m03 = (m00 * m03 + m01 * m13 + m02 * m23 + m03 * m33);
    
    m10 = (m10 * m00 + m11 * m10 + m12 * m20 + m13 * m30);
    m11 = (m10 * m01 + m11 * m11 + m12 * m21 + m13 * m31);
    m12 = (m10 * m02 + m11 * m12 + m12 * m22 + m13 * m32);
    m13 = (m10 * m03 + m11 * m13 + m12 * m23 + m13 * m33);
    
    m20 = (m20 * m00 + m21 * m10 + m22 * m20 + m23 * m30);
    m21 = (m20 * m01 + m21 * m11 + m22 * m21 + m23 * m31);
    m22 = (m20 * m02 + m21 * m12 + m22 * m22 + m23 * m32);
    m23 = (m20 * m03 + m21 * m13 + m22 * m23 + m23 * m33);
    
    m30 = (m30 * m00 + m31 * m10 + m32 * m20 + m33 * m30);
    m31 = (m30 * m01 + m31 * m11 + m32 * m21 + m33 * m31);
    m32 = (m30 * m02 + m31 * m12 + m32 * m22 + m33 * m32);
    m33 = (m30 * m03 + m31 * m13 + m32 * m23 + m33 * m33);
    return m;
  }
  
  public static boolean contains(javax.vecmath.Tuple3d t, double d) {
    if (Double.isNaN(d)) {
      return (Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isNaN(z));
    }
    return (x == d) || (y == d) || (z == d);
  }
}
