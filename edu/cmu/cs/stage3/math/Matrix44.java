package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

















public class Matrix44
  extends Matrix4d
  implements Interpolable
{
  public static final Matrix44 IDENTITY = new Matrix44();
  
  public Matrix44() { this(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D); }
  
  public Matrix44(double rc00, double rc01, double rc02, double rc03, double rc10, double rc11, double rc12, double rc13, double rc20, double rc21, double rc22, double rc23, double rc30, double rc31, double rc32, double rc33) {
    m00 = rc00;m01 = rc01;m02 = rc02;m03 = rc03;
    m10 = rc10;m11 = rc11;m12 = rc12;m13 = rc13;
    m20 = rc20;m21 = rc21;m22 = rc22;m23 = rc23;
    m30 = rc30;m31 = rc31;m32 = rc32;m33 = rc33;
  }
  
  public Matrix44(double[] row0, double[] row1, double[] row2, double[] row3) { this(row0[0], row0[1], row0[2], row0[3], row1[0], row1[1], row1[2], row1[3], row2[0], row2[1], row2[2], row2[3], row3[0], row3[1], row3[2], row3[3]); }
  
  public Matrix44(double[] a) {
    this(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]);
  }
  
  public Matrix44(double[][] m) { this(m[0], m[1], m[2], m[3]); }
  
  public Matrix44(Matrix4d m)
  {
    if (m != null) {
      set(m);
    } else
      throw new NullPointerException();
  }
  
  public Matrix44(Matrix3d axes, Vector3d t) {
    setAxes(axes);
    setPosition(t);
    m33 = 1.0D;
  }
  
  public Matrix44(AxisAngle aa, Vector3 t) { this(aa.getMatrix33(), t); }
  
  public Matrix44(Quaternion q, Vector3 t) {
    this(q.getMatrix33(), t);
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Matrix44))) {
      Matrix44 m = (Matrix44)o;
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m03 == m03) && 
        (m10 == m10) && (m11 == m11) && (m12 == m12) && (m13 == m13) && 
        (m20 == m20) && (m21 == m21) && (m22 == m22) && (m23 == m23) && 
        (m30 == m30) && (m31 == m31) && (m32 == m32) && (m33 == m33);
    }
    return false;
  }
  
  public double getItem(int i, int j) {
    switch (i) {
    case 0: 
      switch (j) {
      case 0: 
        return m00;
      case 1: 
        return m01;
      case 2: 
        return m02;
      case 3: 
        return m03;
      }
      break;
    case 1: 
      switch (j) {
      case 0: 
        return m10;
      case 1: 
        return m11;
      case 2: 
        return m12;
      case 3: 
        return m13;
      }
      break;
    case 2: 
      switch (j) {
      case 0: 
        return m20;
      case 1: 
        return m21;
      case 2: 
        return m22;
      case 3: 
        return m23;
      }
      break;
    case 3: 
      switch (j) {
      case 0: 
        return m30;
      case 1: 
        return m31;
      case 2: 
        return m32;
      case 3: 
        return m33;
      }
      break;
    }
    throw new IllegalArgumentException();
  }
  
  public void setItem(int i, int j, double v) { switch (i) {
    case 0: 
      switch (j) {
      case 0: 
        m00 = v;
        return;
      case 1: 
        m01 = v;
        return;
      case 2: 
        m02 = v;
        return;
      case 3: 
        m03 = v;
        return;
      }
      break;
    case 1: 
      switch (j) {
      case 0: 
        m10 = v;
        return;
      case 1: 
        m11 = v;
        return;
      case 2: 
        m12 = v;
        return;
      case 3: 
        m13 = v;
        return;
      }
      break;
    case 2: 
      switch (j) {
      case 0: 
        m20 = v;
        return;
      case 1: 
        m21 = v;
        return;
      case 2: 
        m22 = v;
        return;
      case 3: 
        m23 = v;
        return;
      }
      break;
    case 3: 
      switch (j) {
      case 0: 
        m30 = v;
        return;
      case 1: 
        m31 = v;
        return;
      case 2: 
        m32 = v;
        return;
      case 3: 
        m33 = v; return;
      }
      
      break;
    }
    throw new IllegalArgumentException();
  }
  
  public Vector4 getRow(int i) { switch (i) {
    case 0: 
      return new Vector4(m00, m01, m02, m03);
    case 1: 
      return new Vector4(m10, m11, m12, m13);
    case 2: 
      return new Vector4(m20, m21, m22, m23);
    case 3: 
      return new Vector4(m30, m31, m32, m33);
    }
    return null;
  }
  
  public void setRow(int i, Vector4 v) {
    switch (i) {
    case 0: 
      m00 = x;
      m01 = y;
      m02 = z;
      m03 = w;
      break;
    case 1: 
      m10 = x;
      m11 = y;
      m12 = z;
      m13 = w;
      break;
    case 2: 
      m20 = x;
      m21 = y;
      m22 = z;
      m23 = w;
      break;
    case 3: 
      m30 = x;
      m31 = y;
      m32 = z;
      m33 = w;
      break;
    default: 
      throw new IndexOutOfBoundsException(); }
  }
  
  public Vector4 getColumn(int i) {
    switch (i) {
    case 0: 
      return new Vector4(m00, m10, m20, m30);
    case 1: 
      return new Vector4(m01, m11, m21, m31);
    case 2: 
      return new Vector4(m02, m12, m22, m32);
    case 3: 
      return new Vector4(m03, m13, m23, m33);
    }
    throw new IndexOutOfBoundsException();
  }
  
  public void setColumn(int i, Vector4 v) {
    switch (i) {
    case 0: 
      m00 = x;
      m10 = y;
      m20 = z;
      m30 = w;
      break;
    case 1: 
      m01 = x;
      m11 = y;
      m21 = z;
      m31 = w;
      break;
    case 2: 
      m02 = x;
      m12 = y;
      m22 = z;
      m32 = w;
      break;
    case 3: 
      m03 = x;
      m13 = y;
      m23 = z;
      m33 = w;
      break;
    default: 
      throw new IndexOutOfBoundsException(); }
  }
  
  public double[] getArray(boolean rowMajor) {
    if (rowMajor) {
      double[] a = { m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33 };
      return a;
    }
    double[] a = { m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33 };
    return a;
  }
  
  public void setArray(double[] a, boolean rowMajor) {
    if (rowMajor) {
      m00 = a[0];m01 = a[1];m02 = a[2];m03 = a[3];
      m10 = a[4];m11 = a[5];m12 = a[6];m13 = a[7];
      m20 = a[8];m21 = a[9];m22 = a[10];m23 = a[11];
      m30 = a[12];m31 = a[13];m32 = a[14];m33 = a[15];
    } else {
      m00 = a[0];m01 = a[4];m02 = a[8];m03 = a[12];
      m10 = a[1];m11 = a[5];m12 = a[9];m13 = a[13];
      m20 = a[2];m21 = a[6];m22 = a[10];m23 = a[14];
      m30 = a[3];m31 = a[7];m32 = a[11];m33 = a[15];
    }
  }
  
  public double[][] getMatrix() { double[][] m = { { m00, m01, m02, m03 }, { m10, m11, m12, m13 }, { m20, m21, m22, m23 }, { m30, m31, m32, m33 } };
    return m;
  }
  
  public void setMatrix(double[][] m) { m00 = m[0][0];m01 = m[0][1];m02 = m[0][2];m03 = m[0][3];
    m10 = m[1][0];m11 = m[1][1];m12 = m[1][2];m13 = m[1][3];
    m20 = m[2][0];m21 = m[2][1];m22 = m[2][2];m23 = m[2][3];
    m30 = m[3][0];m31 = m[3][1];m32 = m[3][2];m33 = m[3][3];
  }
  
  public void set(Matrix44 other) { m00 = m00;
    m01 = m01;
    m02 = m02;
    m03 = m03;
    m10 = m10;
    m11 = m11;
    m12 = m12;
    m13 = m13;
    m20 = m20;
    m21 = m21;
    m22 = m22;
    m23 = m23;
    m30 = m30;
    m31 = m31;
    m32 = m32;
    m33 = m33;
  }
  

  public Vector3 getPosition() { return new Vector3(m30, m31, m32); }
  
  public void setPosition(Vector3d position) {
    m30 = x;
    m31 = y;
    m32 = z;
  }
  
  public Matrix33 getAxes() { return new Matrix33(m00, m01, m02, m10, m11, m12, m20, m21, m22); }
  
  public void setAxes(Matrix3d axes) {
    m00 = m00;m01 = m01;m02 = m02;
    m10 = m10;m11 = m11;m12 = m12;
    m20 = m20;m21 = m21;m22 = m22;
  }
  








  public void translate(Vector3d vector)
  {
    if (x != 0.0D) {
      m00 += m03 * x;
      m10 += m13 * x;
      m20 += m23 * x;
      m30 += m33 * x;
    }
    if (y != 0.0D) {
      m01 += m03 * y;
      m11 += m13 * y;
      m21 += m23 * y;
      m31 += m33 * y;
    }
    if (z != 0.0D) {
      m02 += m03 * z;
      m12 += m13 * z;
      m22 += m23 * z;
      m32 += m33 * z;
    }
  }
  
  public void rotateX(double angle) { double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);
    for (int i = 0; i < 4; i++) {
      double tmp = getItem(i, 1);
      setItem(i, 1, tmp * cosAngle - getItem(i, 2) * sinAngle);
      setItem(i, 2, tmp * sinAngle + getItem(i, 2) * cosAngle);
    }
  }
  
  public void rotateY(double angle) { double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);
    for (int i = 0; i < 4; i++) {
      double tmp = getItem(i, 0);
      setItem(i, 0, tmp * cosAngle + getItem(i, 2) * sinAngle);
      setItem(i, 2, -tmp * sinAngle + getItem(i, 2) * cosAngle);
    }
  }
  
  public void rotateZ(double angle) { double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);
    for (int i = 0; i < 4; i++) {
      double tmp = getItem(i, 0);
      setItem(i, 0, tmp * cosAngle - getItem(i, 1) * sinAngle);
      setItem(i, 1, tmp * sinAngle + getItem(i, 1) * cosAngle);
    }
  }
  
  public void scale(Vector3d vector) { if (x != 1.0D) {
      m00 *= x;
      m10 *= x;
      m20 *= x;
      m30 *= x;
    }
    if (y != 1.0D) {
      m01 *= y;
      m11 *= y;
      m21 *= y;
      m31 *= y;
    }
    if (z != 1.0D) {
      m02 *= z;
      m12 *= z;
      m22 *= z;
      m32 *= z;
    }
  }
  
  public void transform(Matrix4d m) { set(multiply(this, m)); }
  
  public void rotate(Vector3d axis, double angle) {
    if (axis.equals(Vector3.X_AXIS)) {
      rotateX(angle);
    } else if (axis.equals(Vector3.Y_AXIS)) {
      rotateY(angle);
    } else if (axis.equals(Vector3.Z_AXIS)) {
      rotateZ(angle);
    } else if (axis.equals(Vector3.X_AXIS_NEGATIVE)) {
      rotateX(-angle);
    } else if (axis.equals(Vector3.Y_AXIS_NEGATIVE)) {
      rotateY(-angle);
    } else if (axis.equals(Vector3.Z_AXIS_NEGATIVE)) {
      rotateZ(-angle);
    } else {
      Matrix44 m = new Matrix44();
      double cosAngle = Math.cos(angle);
      double sinAngle = Math.sin(angle);
      
      m00 = (x * x + cosAngle * (1.0D - x * x));
      m01 = (x * y * (1.0D - cosAngle) + z * sinAngle);
      m02 = (z * x * (1.0D - cosAngle) - y * sinAngle);
      
      m10 = (x * y * (1.0D - cosAngle) - z * sinAngle);
      m11 = (y * y + cosAngle * (1.0D - y * y));
      m12 = (y * z * (1.0D - cosAngle) + x * sinAngle);
      
      m20 = (z * x * (1.0D - cosAngle) + y * sinAngle);
      m21 = (y * z * (1.0D - cosAngle) - x * sinAngle);
      m22 = (z * z + cosAngle * (1.0D - z * z));
      
      transform(m);
    }
  }
  
  public static Matrix44 multiply(Matrix4d a, Matrix4d b) {
    Matrix44 m = new Matrix44();
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
  
  public static Matrix44 transpose(Matrix44 m) { Matrix44 mTranspose = new Matrix44();
    m00 = m00;
    m01 = m10;
    m02 = m20;
    m03 = m30;
    m10 = m01;
    m11 = m11;
    m12 = m21;
    m13 = m31;
    m20 = m02;
    m21 = m12;
    m22 = m22;
    m23 = m32;
    m30 = m03;
    m31 = m13;
    m32 = m23;
    m33 = m33;
    return mTranspose;
  }
  
  public void divide(double denom) { m00 /= denom;
    m01 /= denom;
    m02 /= denom;
    m03 /= denom;
    m10 /= denom;
    m11 /= denom;
    m12 /= denom;
    m13 /= denom;
    m20 /= denom;
    m21 /= denom;
    m22 /= denom;
    m23 /= denom;
    m30 /= denom;
    m31 /= denom;
    m32 /= denom;
    m33 /= denom;
  }
  
  private static Vector3 ROW(Vector4 a, Vector4 b, Vector4 c, int index) {
    return new Vector3(a.getItem(index), b.getItem(index), c.getItem(index));
  }
  
  private static double DET(Vector4 a, Vector4 b, Vector4 c, int i, int j, int k) { return Vector3.dotProduct(ROW(a, b, c, i), Vector3.crossProduct(ROW(a, b, c, j), ROW(a, b, c, k))); }
  
  private static Vector4 cross(Vector4 a, Vector4 b, Vector4 c) {
    Vector4 result = new Vector4();
    x = DET(a, b, c, 1, 2, 3);
    y = (-DET(a, b, c, 0, 2, 3));
    z = DET(a, b, c, 0, 1, 3);
    w = (-DET(a, b, c, 0, 1, 2));
    return result;
  }
  
  public static Matrix44 adjoint(Matrix44 m) { Matrix44 mAdjoint = new Matrix44();
    mAdjoint.setRow(0, cross(m.getRow(1), m.getRow(2), m.getRow(3)));
    mAdjoint.setRow(1, Vector4.negate(cross(m.getRow(0), m.getRow(2), m.getRow(3))));
    mAdjoint.setRow(2, cross(m.getRow(0), m.getRow(1), m.getRow(3)));
    mAdjoint.setRow(3, Vector4.negate(cross(m.getRow(0), m.getRow(1), m.getRow(2))));
    return mAdjoint;
  }
  
  public static Matrix44 invert(Matrix44 m) { Matrix44 mInverse = new Matrix44();
    if ((Math.abs(m03) > 0.001D) || (Math.abs(m13) > 0.001D) || (Math.abs(m23) > 0.001D) || (Math.abs(m33 - 1.0D) > 0.001D))
    {

      Matrix44 adj = adjoint(m);
      double mDet = Vector4.dotProduct(adj.getRow(0), m.getRow(0));
      if (mDet == 0.0D) {
        throw new SingularityException();
      }
      mInverse = transpose(adj);
      mInverse.divide(mDet);
    }
    else {
      double fDetInv = 1.0D / (m00 * (m11 * m22 - m12 * m21) - 
        m01 * (m10 * m22 - m12 * m20) + 
        m02 * (m10 * m21 - m11 * m20));
      
      m00 = (fDetInv * (m11 * m22 - m12 * m21));
      m01 = (-fDetInv * (m01 * m22 - m02 * m21));
      m02 = (fDetInv * (m01 * m12 - m02 * m11));
      m03 = 0.0D;
      
      m10 = (-fDetInv * (m10 * m22 - m12 * m20));
      m11 = (fDetInv * (m00 * m22 - m02 * m20));
      m12 = (-fDetInv * (m00 * m12 - m02 * m10));
      m13 = 0.0D;
      
      m20 = (fDetInv * (m10 * m21 - m11 * m20));
      m21 = (-fDetInv * (m00 * m21 - m01 * m20));
      m22 = (fDetInv * (m00 * m11 - m01 * m10));
      m23 = 0.0D;
      
      m30 = (-(m30 * m00 + m31 * m10 + m32 * m20));
      m31 = (-(m30 * m01 + m31 * m11 + m32 * m21));
      m32 = (-(m30 * m02 + m31 * m12 + m32 * m22));
      m33 = 1.0D;
    }
    return mInverse;
  }
  
  public static Matrix44 interpolate(Matrix44 a, Matrix44 b, double portion) {
    Vector3 t = Vector3.interpolate(a.getPosition(), b.getPosition(), portion);
    Matrix33 m = Matrix33.interpolate(a.getAxes(), b.getAxes(), portion);
    return new Matrix44(m, t);
  }
  
  public Interpolable interpolate(Interpolable b, double portion) { return interpolate(this, (Matrix44)b, portion); }
  


  public String toString() { return "edu.cmu.cs.stage3.math.Matrix44[rc00=" + m00 + ",rc01=" + m01 + ",rc02=" + m02 + ",rc03=" + m03 + ",rc10=" + m10 + ",rc11=" + m11 + ",rc12=" + m12 + ",rc13=" + m13 + ",rc20=" + m20 + ",rc21=" + m21 + ",rc22=" + m22 + ",rc23=" + m23 + ",rc30=" + m30 + ",rc31=" + m31 + ",rc32=" + m32 + ",rc33=" + m33 + "]"; }
  
  public static Matrix44 valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Matrix44[rc00=", ",rc01=", ",rc02=", ",rc03=", ",rc10=", ",rc11=", ",rc12=", ",rc13=", ",rc20=", ",rc21=", ",rc22=", ",rc23=", ",rc30=", ",rc31=", ",rc32=", ",rc33=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      String v = s.substring(begin, end);
      Double d = Double.valueOf(v);
      values[i] = d.doubleValue();
    }
    return new Matrix44(values);
  }
}
