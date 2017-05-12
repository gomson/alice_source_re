package edu.cmu.cs.stage3.math;

import java.awt.Color;
import javax.vecmath.Matrix4d;

















public class Interpolator
{
  public Interpolator() {}
  
  public static int interpolate(int a, int b, double portion)
  {
    return a + (int)((b - a) * portion);
  }
  
  public static double interpolate(double a, double b, double portion) { return a + (b - a) * portion; }
  

  public static Number interpolate(Number a, Number b, double portion) { return new Double(interpolate(a.doubleValue(), b.doubleValue(), portion)); }
  
  public static double[] interpolate(double[] a, double[] b, double portion) {
    if (a.length != b.length) return null;
    double[] v = new double[a.length];
    for (int i = 0; i < a.length; i++) {
      v[i] = interpolate(a[i], b[i], portion);
    }
    return v;
  }
  
  public static double[][] interpolate(double[][] a, double[][] b, double portion) { if (a.length != b.length) return null;
    double[][] m = new double[a.length][];
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return null;
      m[i] = new double[a[i].length];
      for (int j = 0; j < a[i].length; j++) {
        m[i][j] = interpolate(a[i][j], b[i][j], portion);
      }
    }
    return m;
  }
  
  public static Color interpolate(Color a, Color b, double portion) { return new Color(
      Math.max(0, Math.min(interpolate(a.getRed(), b.getRed(), portion), 255)), 
      Math.max(0, Math.min(interpolate(a.getGreen(), b.getGreen(), portion), 255)), 
      Math.max(0, Math.min(interpolate(a.getBlue(), b.getBlue(), portion), 255)));
  }
  
  public static Object interpolate(Object a, Object b, double portion) {
    if ((a instanceof Matrix4d)) { Interpolable ai;
      Interpolable ai;
      if ((a instanceof Matrix44)) {
        ai = (Matrix44)a;
      } else
        ai = new Matrix44((Matrix4d)a);
      Interpolable bi;
      Interpolable bi;
      if ((b instanceof Matrix44)) {
        bi = (Matrix44)b;
      } else {
        bi = new Matrix44((Matrix4d)b);
      }
      return ai.interpolate(bi, portion); }
    if ((a instanceof Interpolable))
      return ((Interpolable)a).interpolate((Interpolable)b, portion);
    if ((a instanceof Number))
      return interpolate((Number)a, (Number)b, portion);
    if ((a instanceof Color))
      return interpolate((Color)a, (Color)b, portion);
    if ((a instanceof double[]))
      return interpolate((double[])a, (double[])b, portion);
    if ((a instanceof double[][])) {
      return interpolate((double[][])a, (double[][])b, portion);
    }
    return b;
  }
}
