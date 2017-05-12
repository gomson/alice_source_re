package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Vector3;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;

















/**
 * @deprecated
 */
public class Vertex
  extends Vertex3d
{
  public static final int POSITION_XYZ = 1;
  public static final int NORMAL_IJK = 2;
  public static final int DIFFUSE_RGBA = 4;
  public static final int SPECULAR_RGBA = 8;
  public static final int TEXTURE_COORDINATE_UV0 = 16;
  
  public Vertex(int format)
  {
    super(format);
  }
  
  public static Vertex createXYZVertex(double x, double y, double z) {
    Vertex vertex = new Vertex(1);
    vertex.setX(x);
    vertex.setY(y);
    vertex.setZ(z);
    return vertex;
  }
  
  public static Vertex createXYZDiffuseVertex(double x, double y, double z, Color color) { Vertex vertex = new Vertex(5);
    vertex.setX(x);
    vertex.setY(y);
    vertex.setZ(z);
    vertex.setR(color.getRed());
    vertex.setG(color.getGreen());
    vertex.setB(color.getBlue());
    vertex.setA(color.getAlpha());
    return vertex;
  }
  
  public static Vertex createXYZIJKUVVertex(double x, double y, double z, double i, double j, double k, double u, double v) { Vertex vertex = new Vertex(19);
    vertex.setX(x);
    vertex.setY(y);
    vertex.setZ(z);
    vertex.setI(i);
    vertex.setJ(j);
    vertex.setK(k);
    vertex.setU(u);
    vertex.setV(v);
    return vertex;
  }
  
  public double[] getArray() { byte xyzOffset = Byte.MIN_VALUE;
    byte ijkOffset = Byte.MIN_VALUE;
    byte uvOffset = Byte.MIN_VALUE;
    byte rgbaOffset = Byte.MIN_VALUE;
    byte size = 0;
    int format = getFormat();
    if ((format & 0x1) != 0) {
      xyzOffset = size;
      size = (byte)(size + 3);
    }
    if ((format & 0x2) != 0) {
      ijkOffset = size;
      size = (byte)(size + 3);
    }
    if ((format & 0x4) != 0) {
      rgbaOffset = size;
      size = (byte)(size + 4);
    }
    if ((format & 0x8) != 0) {
      rgbaOffset = size;
      size = (byte)(size + 4);
    }
    if ((format & 0x10) != 0) {
      uvOffset = size;
      size = (byte)(size + 2);
    }
    double[] array = new double[size];
    if ((format & 0x1) != 0) {
      array[xyzOffset] = position.x;
      array[(xyzOffset + 1)] = position.y;
      array[(xyzOffset + 2)] = position.z;
    }
    if ((format & 0x2) != 0) {
      array[xyzOffset] = normal.x;
      array[(xyzOffset + 1)] = normal.y;
      array[(xyzOffset + 2)] = normal.z;
    }
    if ((format & 0x4) != 0) {
      array[xyzOffset] = diffuseColor.red;
      array[(xyzOffset + 1)] = diffuseColor.green;
      array[(xyzOffset + 2)] = diffuseColor.blue;
      array[(xyzOffset + 3)] = diffuseColor.alpha;
    }
    if ((format & 0x8) != 0) {
      array[xyzOffset] = specularHighlightColor.red;
      array[(xyzOffset + 1)] = specularHighlightColor.green;
      array[(xyzOffset + 2)] = specularHighlightColor.blue;
      array[(xyzOffset + 3)] = specularHighlightColor.alpha;
    }
    if ((format & 0x10) != 0) {
      array[xyzOffset] = textureCoordinate0.x;
      array[(xyzOffset + 1)] = textureCoordinate0.y;
    }
    return array;
  }
  
  public Vector3 getXYZ() { return new Vector3(getX(), getY(), getZ()); }
  
  public void setXYZ(Vector3 v) {
    setX(x);
    setY(y);
    setZ(z);
  }
  
  public Vector3 getIJK() { return new Vector3(getI(), getJ(), getK()); }
  
  public void setIJK(Vector3 v) {
    setI(x);
    setJ(y);
    setK(z);
  }
  
  public double getX() { if ((getFormat() & 0x1) != 0) {
      return position.x;
    }
    return NaN.0D;
  }
  
  public double getY() {
    if ((getFormat() & 0x1) != 0) {
      return position.y;
    }
    return NaN.0D;
  }
  
  public double getZ() {
    if ((getFormat() & 0x1) != 0) {
      return position.z;
    }
    return NaN.0D;
  }
  
  public double getI() {
    if ((getFormat() & 0x2) != 0) {
      return normal.x;
    }
    return NaN.0D;
  }
  
  public double getJ() {
    if ((getFormat() & 0x2) != 0) {
      return normal.y;
    }
    return NaN.0D;
  }
  
  public double getK() {
    if ((getFormat() & 0x2) != 0) {
      return normal.z;
    }
    return NaN.0D;
  }
  
  public double getU() {
    if ((getFormat() & 0x10) != 0) {
      return textureCoordinate0.x;
    }
    return NaN.0D;
  }
  
  public double getV() {
    if ((getFormat() & 0x10) != 0) {
      return textureCoordinate0.y;
    }
    return NaN.0D;
  }
  
  public double getR() {
    if ((getFormat() & 0x4) != 0) {
      return diffuseColor.red;
    }
    return NaN.0D;
  }
  
  public double getG() {
    if ((getFormat() & 0x4) != 0) {
      return diffuseColor.green;
    }
    return NaN.0D;
  }
  
  public double getB() {
    if ((getFormat() & 0x4) != 0) {
      return diffuseColor.blue;
    }
    return NaN.0D;
  }
  
  public double getA() {
    if ((getFormat() & 0x4) != 0) {
      return diffuseColor.alpha;
    }
    return NaN.0D;
  }
  
  public void setX(double x) {
    if ((getFormat() & 0x1) != 0) {
      position.x = x;
    }
    else if (!Double.isNaN(x)) {
      throw new RuntimeException();
    }
  }
  
  public void setY(double y) {
    if ((getFormat() & 0x1) != 0) {
      position.y = y;
    }
    else if (!Double.isNaN(y)) {
      throw new RuntimeException();
    }
  }
  
  public void setZ(double z) {
    if ((getFormat() & 0x1) != 0) {
      position.z = z;
    }
    else if (!Double.isNaN(z)) {
      throw new RuntimeException();
    }
  }
  
  public void setI(double i) {
    if ((getFormat() & 0x2) != 0) {
      normal.x = i;
    }
    else if (!Double.isNaN(i)) {
      throw new RuntimeException();
    }
  }
  
  public void setJ(double j) {
    if ((getFormat() & 0x2) != 0) {
      normal.y = j;
    }
    else if (!Double.isNaN(j)) {
      throw new RuntimeException();
    }
  }
  
  public void setK(double k) {
    if ((getFormat() & 0x2) != 0) {
      normal.z = k;
    }
    else if (!Double.isNaN(k)) {
      throw new RuntimeException();
    }
  }
  
  public void setU(double u) {
    if ((getFormat() & 0x10) != 0) {
      textureCoordinate0.x = ((float)u);
    }
    else if (!Double.isNaN(u)) {
      throw new RuntimeException();
    }
  }
  
  public void setV(double v) {
    if ((getFormat() & 0x10) != 0) {
      textureCoordinate0.y = ((float)v);
    }
    else if (!Double.isNaN(v)) {
      throw new RuntimeException();
    }
  }
  
  public void setR(double r) {
    if ((getFormat() & 0x4) != 0) {
      diffuseColor.red = ((float)r);
    }
    else if (!Double.isNaN(r)) {
      throw new RuntimeException();
    }
  }
  
  public void setG(double g) {
    if ((getFormat() & 0x4) != 0) {
      diffuseColor.green = ((float)g);
    }
    else if (!Double.isNaN(g)) {
      throw new RuntimeException();
    }
  }
  
  public void setB(double b) {
    if ((getFormat() & 0x4) != 0) {
      diffuseColor.blue = ((float)b);
    }
    else if (!Double.isNaN(b)) {
      throw new RuntimeException();
    }
  }
  
  public void setA(double a) {
    if ((getFormat() & 0x4) != 0) {
      diffuseColor.alpha = ((float)a);
    }
    else if (!Double.isNaN(a)) {
      throw new RuntimeException();
    }
  }
  


  public String toString() { return "edu.cmu.cs.stage3.alice.scenegraph.Vertex[format=" + getFormat() + ",x=" + getX() + ",y=" + getY() + ",z=" + getZ() + ",i=" + getI() + ",j=" + getJ() + ",k=" + getK() + ",u=" + getU() + ",v=" + getV() + ",r=" + getR() + ",g=" + getG() + ",b=" + getB() + ",a=" + getA() + "]"; }
  
  public static Vertex valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.alice.scenegraph.Vertex[format=", ",x=", ",y=", ",z=", ",z=", ",i=", ",j=", ",k=", ",u=", ",v=", ",r=", ",g=", ",b=", ",a=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    Vertex v = new Vertex((int)values[0]);
    v.setX(values[1]);
    v.setY(values[2]);
    v.setZ(values[3]);
    v.setI(values[4]);
    v.setJ(values[5]);
    v.setK(values[6]);
    v.setU(values[7]);
    v.setV(values[8]);
    v.setR(values[9]);
    v.setG(values[10]);
    v.setB(values[11]);
    v.setA(values[12]);
    return v;
  }
}
