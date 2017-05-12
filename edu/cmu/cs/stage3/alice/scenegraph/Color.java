package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Interpolable;
import java.io.Serializable;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;





















public class Color
  implements Cloneable, Serializable, Interpolable
{
  public static final Color RED = new Color(java.awt.Color.red);
  public static final Color PINK = new Color(java.awt.Color.pink);
  public static final Color ORANGE = new Color(new java.awt.Color(255, 165, 0));
  public static final Color YELLOW = new Color(java.awt.Color.yellow);
  public static final Color GREEN = new Color(java.awt.Color.green);
  public static final Color BLUE = new Color(java.awt.Color.blue);
  public static final Color PURPLE = new Color(new java.awt.Color(128, 0, 128));
  public static final Color BROWN = new Color(new java.awt.Color(162, 42, 42));
  public static final Color WHITE = new Color(java.awt.Color.white);
  public static final Color LIGHT_GRAY = new Color(java.awt.Color.lightGray);
  public static final Color GRAY = new Color(java.awt.Color.gray);
  public static final Color DARK_GRAY = new Color(java.awt.Color.darkGray);
  public static final Color BLACK = new Color(java.awt.Color.black);
  public static final Color CYAN = new Color(java.awt.Color.cyan);
  public static final Color MAGENTA = new Color(java.awt.Color.magenta);
  public float red;
  public float green;
  public float blue;
  public float alpha;
  
  public Color()
  {
    this(0.0F, 0.0F, 0.0F, 1.0F);
  }
  
  public Color(float red, float green, float blue) { this(red, green, blue, 1.0F); }
  
  public Color(float red, float green, float blue, float alpha) {
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }
  
  public Color(float[] array) { alpha = 1.0F;
    switch (array.length) {
    case 4: 
      alpha = array[3];
    case 3: 
      blue = array[2];
      green = array[1];
      red = array[0];
      break;
    default: 
      throw new RuntimeException(); }
  }
  
  public Color(double red, double green, double blue) {
    this((float)red, (float)green, (float)blue);
  }
  
  public Color(double red, double green, double blue, double alpha) { this((float)red, (float)green, (float)blue, (float)alpha); }
  
  public Color(double[] array) {
    alpha = 1.0F;
    switch (array.length) {
    case 4: 
      alpha = ((float)array[3]);
    case 3: 
      blue = ((float)array[2]);
      green = ((float)array[1]);
      red = ((float)array[0]);
      break;
    default: 
      throw new RuntimeException(); }
  }
  
  public Color(Color color) {
    this(red, green, blue, alpha);
  }
  
  public Color(Color3f color) { this(x, y, z); }
  
  public Color(Color4f color) {
    this(x, y, z, w);
  }
  
  public Color(java.awt.Color color) { this(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F); }
  

  public boolean equals(Object o)
  {
    if (o == this) return true;
    if ((o != null) && ((o instanceof Color))) {
      Color c = (Color)o;
      return (red == red) && (green == green) && (blue == blue) && (alpha == alpha);
    }
    return false;
  }
  
  public java.awt.Color createAWTColor()
  {
    float r = (float)Math.max(Math.min(red, 1.0D), 0.0D);
    float g = (float)Math.max(Math.min(green, 1.0D), 0.0D);
    float b = (float)Math.max(Math.min(blue, 1.0D), 0.0D);
    return new java.awt.Color(r, g, b);
  }
  
  public Color3f createVecmathColor3f() { return new Color3f(red, green, blue); }
  
  public Color4f createVecmathColor4f() {
    return new Color4f(red, green, blue, alpha);
  }
  
  public float getRed() {
    return red;
  }
  
  public float getGreen() { return green; }
  
  public float getBlue() {
    return blue;
  }
  
  public float getAlpha() { return alpha; }
  
  public void setRed(float red)
  {
    this.red = red;
  }
  
  public void setGreen(float green) { this.green = green; }
  
  public void setBlue(float blue) {
    this.blue = blue;
  }
  
  public void setAlpha(float alpha) { this.alpha = alpha; }
  
  public synchronized Object clone()
  {
    try
    {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  
  public static Color interpolate(Color a, Color b, double portion) { return new Color(red + (red - red) * (float)portion, green + (green - green) * (float)portion, blue + (blue - blue) * (float)portion, alpha + (alpha - alpha) * (float)portion); }
  
  public Interpolable interpolate(Interpolable b, double portion) {
    return interpolate(this, (Color)b, portion);
  }
  

  public String toString() { return "edu.cmu.cs.stage3.alice.scenegraph.Color[r=" + red + ",g=" + green + ",b=" + blue + ",a=" + alpha + "]"; }
  
  public static Color valueOf(String s) {
    if (s == null) s = "edu.cmu.cs.stage3.alice.scenegraph.Color[r=0.0,g=0.30588236,b=0.59607846,a=1.0]";
    String[] markers = { "edu.cmu.cs.stage3.alice.scenegraph.Color[r=", ",g=", ",b=", ",a=", "]" };
    float[] values = new float[markers.length - 1];
    for (int i = 0; i < values.length; i++) {
      int begin = s.indexOf(markers[i]) + markers[i].length();
      int end = s.indexOf(markers[(i + 1)]);
      values[i] = Float.valueOf(s.substring(begin, end)).floatValue();
    }
    return new Color(values);
  }
}
