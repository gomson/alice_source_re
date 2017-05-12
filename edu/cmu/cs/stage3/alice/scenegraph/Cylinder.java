package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Sphere;




















public class Cylinder
  extends Shape
{
  public Cylinder() {}
  
  public static final Property BASE_RADIUS_PROPERTY = new Property(Cylinder.class, "BASE_RADIUS");
  public static final Property TOP_RADIUS_PROPERTY = new Property(Cylinder.class, "TOP_RADIUS");
  public static final Property HEIGHT_PROPERTY = new Property(Cylinder.class, "HEIGHT");
  
  private double m_baseRadius = 1.0D;
  private double m_topRadius = 1.0D;
  private double m_height = 1.0D;
  

  public double getBaseRadius() { return m_baseRadius; }
  
  public void setBaseRadius(double baseRadius) {
    if (m_baseRadius != baseRadius) {
      m_baseRadius = baseRadius;
      onPropertyChange(BASE_RADIUS_PROPERTY);
      onBoundsChange();
    }
  }
  

  public double getTopRadius() { return m_topRadius; }
  
  public void setTopRadius(double topRadius) {
    if (m_topRadius != topRadius) {
      m_topRadius = topRadius;
      onPropertyChange(TOP_RADIUS_PROPERTY);
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
  
  protected void updateBoundingBox()
  {
    double radius = Math.max(m_baseRadius, m_topRadius);
    m_boundingBox = new Box(-radius, 0.0D, -radius, radius, m_height, radius);
  }
  
  protected void updateBoundingSphere() {
    double halfHeight = m_height * 0.5D;
    double radius = Math.max(halfHeight, Math.max(m_baseRadius, m_topRadius));
    m_boundingSphere = new Sphere(0.0D, halfHeight, 0.0D, radius);
  }
}
