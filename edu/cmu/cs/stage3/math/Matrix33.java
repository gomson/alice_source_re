package edu.cmu.cs.stage3.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;


















public class Matrix33
  extends Matrix3d
  implements Interpolable
{
  public static final Matrix33 IDENTITY = new Matrix33();
  
  public Matrix33() { this(1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 1.0D); }
  
  public Matrix33(double rc00, double rc01, double rc02, double rc10, double rc11, double rc12, double rc20, double rc21, double rc22) {
    m00 = rc00;m01 = rc01;m02 = rc02;
    m10 = rc10;m11 = rc11;m12 = rc12;
    m20 = rc20;m21 = rc21;m22 = rc22;
  }
  
  public Matrix33(double[] row0, double[] row1, double[] row2) { this(row0[0], row0[1], row0[2], row1[0], row1[1], row1[2], row2[0], row2[1], row2[2]); }
  
  public Matrix33(Vector3d row0, Vector3d row1, Vector3d row2) {
    this(x, y, z, x, y, z, x, y, z);
  }
  
  public Matrix33(double[] a) { this(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]); }
  
  public Matrix33(double[][] m) {
    this(m[0], m[1], m[2]);
  }
  
  public Matrix33(Matrix3d m) { super(m); }
  
  public Matrix33(AxisAngle aa) {
    setAxisAngle(aa);
  }
  
  public Matrix33(Quaternion q) { setQuaternion(q); }
  
  public Matrix33(EulerAngles ea) {
    setEulerAngles(ea);
  }
  
  public boolean equals(Object o) {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Matrix33))) {
      Matrix33 m = (Matrix33)o;
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && 
        (m10 == m10) && (m11 == m11) && (m12 == m12) && 
        (m20 == m20) && (m21 == m21) && (m22 == m22);
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
        m22 = v; return;
      }
      
      break;
    }
    throw new IllegalArgumentException();
  }
  
  public Vector3 getRow(int i)
  {
    switch (i) {
    case 0: 
      return new Vector3(m00, m01, m02);
    case 1: 
      return new Vector3(m10, m11, m12);
    case 2: 
      return new Vector3(m20, m21, m22);
    }
    return null;
  }
  
  public void setRow(int i, Vector3 v) {
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
  
  public Vector3 getColumn(int i) {
    switch (i) {
    case 0: 
      return new Vector3(m00, m10, m20);
    case 1: 
      return new Vector3(m01, m11, m21);
    case 2: 
      return new Vector3(m02, m12, m22);
    }
    throw new IndexOutOfBoundsException();
  }
  
  public void setColumn(int i, Vector3 v) {
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
  
  public double[] getArray(boolean rowMajor) {
    if (rowMajor) {
      double[] a = { m00, m01, m02, m10, m11, m12, m20, m21, m22 };
      return a;
    }
    double[] a = { m00, m10, m20, m01, m11, m21, m02, m12, m22 };
    return a;
  }
  
  public void setArray(double[] a, boolean rowMajor) {
    if (rowMajor) {
      m00 = a[0];m01 = a[1];m02 = a[2];
      m10 = a[3];m11 = a[4];m12 = a[5];
      m20 = a[6];m21 = a[7];m22 = a[8];
    } else {
      m00 = a[0];m01 = a[3];m02 = a[6];
      m10 = a[1];m11 = a[4];m12 = a[7];
      m20 = a[2];m21 = a[5];m22 = a[8];
    }
  }
  
  public double[][] getMatrix() {
    double[][] m = { { m00, m01, m02 }, { m10, m11, m12 }, { m20, m21, m22 } };
    return m;
  }
  
  public void setMatrix(double[][] m) { m00 = m[0][0];m01 = m[0][1];m02 = m[0][2];
    m10 = m[1][0];m11 = m[1][1];m12 = m[1][2];
    m20 = m[2][0];m21 = m[2][1];m22 = m[2][2];
  }
  
  public Quaternion getQuaternion() { return new Quaternion(this); }
  
  public void setQuaternion(Quaternion q) {
    double xx = x * x;
    double xy = x * y;
    double xz = x * z;
    double yy = y * y;
    double yz = y * z;
    double zz = z * z;
    double wx = w * x;
    double wy = w * y;
    double wz = w * z;
    
    m00 = (1.0D - 2.0D * (yy + zz));
    m01 = (2.0D * (xy - wz));
    m02 = (2.0D * (xz + wy));
    
    m10 = (2.0D * (xy + wz));
    m11 = (1.0D - 2.0D * (xx + zz));
    m12 = (2.0D * (yz - wx));
    
    m20 = (2.0D * (xz - wy));
    m21 = (2.0D * (yz + wx));
    m22 = (1.0D - 2.0D * (xx + yy));
  }
  
  public AxisAngle getAxisAngle() { return new AxisAngle(this); }
  
  public void setAxisAngle(AxisAngle aa) {
    double theta = aa.getAngle();
    Vector3d axis = aa.getAxis();
    double cosTheta = Math.cos(theta);
    double sinTheta = Math.sin(theta);
    m00 = (x * x + cosTheta * (1.0D - x * x));
    m01 = (x * y * (1.0D - cosTheta) + z * sinTheta);
    m02 = (z * x * (1.0D - cosTheta) - y * sinTheta);
    m10 = (x * y * (1.0D - cosTheta) - z * sinTheta);
    m11 = (y * y + cosTheta * (1.0D - y * y));
    m12 = (y * z * (1.0D - cosTheta) + x * sinTheta);
    m20 = (z * x * (1.0D - cosTheta) + y * sinTheta);
    m21 = (y * z * (1.0D - cosTheta) - x * sinTheta);
    m22 = (z * z + cosTheta * (1.0D - z * z));
  }
  

  public EulerAngles getEulerAngles() { return new EulerAngles(this); }
  
  public void setEulerAngles(EulerAngles ea) {
    double c1 = Math.cos(pitch);
    double s1 = Math.sin(pitch);
    double c2 = Math.cos(yaw);
    double s2 = Math.sin(yaw);
    double c3 = Math.cos(roll);
    double s3 = Math.sin(roll);
    
    m00 = (c2 * c3);
    m01 = (s2 * s1 * c3 - c1 * s3);
    m02 = (s2 * c1 * c3 + s1 * s3);
    
    m10 = (c2 * s3);
    m11 = (s2 * s1 * s3 + c1 * c3);
    m12 = (c2 * s1);
    
    m20 = (-s2);
    m21 = (c2 * s1);
    m22 = (c2 * c1);
  }
  



















  public Vector3d[] getRows() { return new Vector3d[] { getRow(0), getRow(1), getRow(2) }; }
  
  public void setRows(Vector3d row0, Vector3d row1, Vector3d row2) {
    m00 = x;
    m01 = y;
    m02 = z;
    m10 = x;
    m11 = y;
    m12 = z;
    m20 = x;
    m21 = y;
    m22 = z;
  }
  
  public void setForwardUpGuide(Vector3d forward, Vector3d upGuide) { Vector3 row2 = Vector3.normalizeV(forward);
    Vector3 normalizedUpGuide;
    Vector3 normalizedUpGuide; if (upGuide != null) {
      normalizedUpGuide = Vector3.normalizeV(upGuide);
    } else {
      normalizedUpGuide = new Vector3(0.0D, 1.0D, 0.0D);
    }
    Vector3 row0 = Vector3.crossProduct(normalizedUpGuide, row2);
    Vector3 row1 = Vector3.crossProduct(row2, row0);
    setRows(row0, row1, row2);
  }
  
  public void rotateX(double angle) {
    double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);
    for (int i = 0; i < 3; i++) {
      double tmp = getItem(i, 1);
      setItem(i, 1, tmp * cosAngle - getItem(i, 2) * sinAngle);
      setItem(i, 2, tmp * sinAngle + getItem(i, 2) * cosAngle);
    }
  }
  
  public void rotateY(double angle) { double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);
    for (int i = 0; i < 3; i++) {
      double tmp = getItem(i, 0);
      setItem(i, 0, tmp * cosAngle + getItem(i, 2) * sinAngle);
      setItem(i, 2, -tmp * sinAngle + getItem(i, 2) * cosAngle);
    }
  }
  
  public void rotateZ(double angle) { double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);
    for (int i = 0; i < 3; i++) {
      double tmp = getItem(i, 0);
      setItem(i, 0, tmp * cosAngle - getItem(i, 1) * sinAngle);
      setItem(i, 1, tmp * sinAngle + getItem(i, 1) * cosAngle);
    }
  }
  
  public Vector3 getScaledSpace() {
    Vector3 row0 = getRow(0);
    Vector3 row1 = getRow(1);
    Vector3 row2 = getRow(2);
    Vector3 scale = new Vector3();
    Shear shear = new Shear();
    
    x = row0.getLength();
    row0.normalize();
    xy = Vector3.dotProduct(row0, row1);
    row1 = Vector3.combine(row1, row0, 1.0D, -xy);
    
    y = row1.getLength();
    row1.normalize();
    xy /= y;
    xz = Vector3.dotProduct(row0, row2);
    row2 = Vector3.combine(row2, row0, 1.0D, -xz);
    
    yz = Vector3.dotProduct(row1, row2);
    row2 = Vector3.combine(row2, row1, 1.0D, -yz);
    
    z = row2.getLength();
    row2.normalize();
    xz /= z;
    yz /= z;
    
    double determinate = Vector3.dotProduct(row0, Vector3.crossProduct(row1, row2));
    if (determinate < 0.0D) {
      row0.negate();
      row1.negate();
      row2.negate();
      scale.multiply(-1.0D);
    }
    return scale;
  }
  





  public static Matrix33 multiply(Matrix33 a, Matrix33 b)
  {
    Matrix33 m = new Matrix33();
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
  
  public static Matrix33 interpolate(Matrix33 a, Matrix33 b, double portion) {
    Quaternion q = Quaternion.interpolate(a.getQuaternion(), b.getQuaternion(), portion);
    return new Matrix33(q);
  }
  
  public Interpolable interpolate(Interpolable b, double portion) { return interpolate(this, (Matrix33)b, portion); }
  


  public String toString() { return "edu.cmu.cs.stage3.math.Matrix33[rc00=" + m00 + ",rc01=" + m01 + ",rc02=" + m02 + ",rc10=" + m10 + ",rc11=" + m11 + ",rc12=" + m12 + ",rc20=" + m20 + ",rc21=" + m21 + ",rc22=" + m22 + "]"; }
  
  public static Matrix33 valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Matrix33[rc00=", ",rc01=", ",rc02=", ",rc10=", ",rc11=", ",rc12=", ",rc20=", ",rc21=", ",rc22=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new Matrix33(values);
  }
}
