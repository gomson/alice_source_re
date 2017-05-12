package edu.cmu.cs.stage3.math;

import javax.vecmath.Vector3d;




















public class EulerAngles
  implements Interpolable, Cloneable
{
  public double pitch = 0.0D;
  public double yaw = 0.0D;
  public double roll = 0.0D;
  
  public EulerAngles() {}
  
  public EulerAngles(double pitch, double yaw, double roll) { this.pitch = pitch;
    this.yaw = yaw;
    this.roll = roll;
  }
  
  public EulerAngles(double[] a) { this(a[0], a[1], a[2]); }
  
  public EulerAngles(Matrix33 m) {
    setMatrix33(m);
  }
  
  public EulerAngles(AxisAngle aa) { setAxisAngle(aa); }
  
  public EulerAngles(Quaternion q) {
    setQuaternion(q);
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
    if ((o != null) && ((o instanceof EulerAngles))) {
      EulerAngles ea = (EulerAngles)o;
      return (yaw == yaw) && (pitch == pitch) && (roll == roll);
    }
    return false;
  }
  

  public Matrix33 getMatrix33() { return new Matrix33(this); }
  
  public void setMatrix33(Matrix33 m) {
    Vector3d row0 = MathUtilities.getRow(m, 0);
    Vector3d row1 = MathUtilities.getRow(m, 1);
    Vector3d row2 = MathUtilities.getRow(m, 2);
    Vector3d scale = new Vector3d();
    Shear shear = new Shear();
    
    x = row0.length();
    row0.normalize();
    xy = MathUtilities.dotProduct(row0, row1);
    row1 = MathUtilities.combine(row1, row0, 1.0D, -xy);
    
    y = row1.length();
    row1.normalize();
    xy /= y;
    xz = MathUtilities.dotProduct(row0, row2);
    row2 = MathUtilities.combine(row2, row0, 1.0D, -xz);
    
    yz = MathUtilities.dotProduct(row1, row2);
    row2 = MathUtilities.combine(row2, row1, 1.0D, -yz);
    
    z = row2.length();
    row2.normalize();
    xz /= z;
    yz /= z;
    
    double determinate = MathUtilities.dotProduct(row0, MathUtilities.crossProduct(row1, row2));
    if (determinate < 0.0D) {
      row0.negate();
      row1.negate();
      row2.negate();
      scale.scale(-1.0D);
    }
    yaw = Math.asin(-z);
    if (Math.cos(yaw) != 0.0D) {
      pitch = Math.atan2(z, z);
      roll = Math.atan2(y, x);
    } else {
      pitch = Math.atan2(x, y);
      roll = 0.0D;
    }
  }
  
  public AxisAngle getAxisAngle() { return new AxisAngle(this); }
  
  public void setAxisAngle(AxisAngle aa)
  {
    setMatrix33(aa.getMatrix33());
  }
  
  public Quaternion getQuaternion() { return new Quaternion(this); }
  


  public void setQuaternion(Quaternion q) { setMatrix33(q.getMatrix33()); }
  
  public static EulerAngles interpolate(EulerAngles a, EulerAngles b, double portion) {
    Quaternion q = Quaternion.interpolate(a.getQuaternion(), b.getQuaternion(), portion);
    return new EulerAngles(q);
  }
  
  public Interpolable interpolate(Interpolable b, double portion) { return interpolate(this, (EulerAngles)b, portion); }
  
  public String toString()
  {
    return "edu.cmu.cs.stage3.math.EulerAngles[pitch=" + pitch + ",yaw=" + yaw + ",roll=" + roll + "]";
  }
  
  public static EulerAngles revolutionsToRadians(EulerAngles ea) { return new EulerAngles(pitch / 0.15915494309189535D, yaw / 0.15915494309189535D, roll / 0.15915494309189535D); }
  
  public static EulerAngles radiansToRevolutions(EulerAngles ea) {
    return new EulerAngles(pitch * 0.15915494309189535D, yaw * 0.15915494309189535D, roll * 0.15915494309189535D);
  }
  
  public static EulerAngles valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.EulerAngles[pitch=", ",yaw=", ",roll=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new EulerAngles(values);
  }
}
