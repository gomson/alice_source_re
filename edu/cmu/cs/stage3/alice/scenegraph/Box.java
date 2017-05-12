package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Sphere;





















public class Box
  extends Shape
{
  public Box() {}
  
  public static final Property WIDTH_PROPERTY = new Property(Box.class, "WIDTH");
  public static final Property HEIGHT_PROPERTY = new Property(Box.class, "HEIGHT");
  public static final Property DEPTH_PROPERTY = new Property(Box.class, "DEPTH");
  
  private double m_width = 1.0D;
  private double m_height = 1.0D;
  private double m_depth = 1.0D;
  

  public double getWidth() { return m_width; }
  
  public void setWidth(double width) {
    if (m_width != width) {
      m_width = width;
      onPropertyChange(WIDTH_PROPERTY);
      onBoundsChange();
    }
  }
  

  public double getHeight() { return m_height; }
  
  public void setHeight(double height) {
    if (m_height != height) {
      m_height = height;
      onPropertyChange(HEIGHT_PROPERTY);
      onBoundsChange();
    }
  }
  

  public double getDepth() { return m_depth; }
  
  public void setDepth(double depth) {
    if (m_depth != depth) {
      m_depth = depth;
      onPropertyChange(DEPTH_PROPERTY);
      onBoundsChange();
    }
  }
  
  protected void updateBoundingBox()
  {
    double halfWidth = m_width * 0.5D;
    double halfHeight = m_height * 0.5D;
    double halfDepth = m_depth * 0.5D;
    m_boundingBox = new edu.cmu.cs.stage3.math.Box(-halfWidth, -halfHeight, -halfDepth, halfWidth, halfHeight, halfDepth);
  }
  
  protected void updateBoundingSphere() {
    double halfWidth = m_width * 0.5D;
    double halfHeight = m_height * 0.5D;
    double halfDepth = m_depth * 0.5D;
    m_boundingSphere = new Sphere(0.0D, 0.0D, 0.0D, Math.max(Math.max(halfWidth, halfHeight), halfDepth));
  }
}
