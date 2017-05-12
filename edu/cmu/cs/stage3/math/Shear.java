package edu.cmu.cs.stage3.math;























public class Shear
{
  double xy = 1.0D;
  double xz = 1.0D;
  double yz = 1.0D;
  
  public Shear() {}
  
  public Shear(double xy, double xz, double yz) { this.xy = xy;
    this.xz = xz;
    this.yz = yz;
  }
  
  public Shear(double[] array) { this(array[0], array[1], array[2]); }
  



  public String toString() { return "edu.cmu.cs.stage3.math.Shear[xy=" + xy + ",xz=" + xz + ",yz=" + yz + "]"; }
  
  public static Shear valueOf(String s) {
    String[] markers = { "edu.cmu.cs.stage3.math.Shear[xy=", ",xz=", ",yz=", "]" };
    double[] values = new double[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Double.valueOf(s.substring(begin, end)).doubleValue();
    }
    return new Shear(values);
  }
}
