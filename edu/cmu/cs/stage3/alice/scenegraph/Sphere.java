package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Box;





















public class Sphere
  extends Shape
{
  public Sphere() {}
  
  public static final Property RADIUS_PROPERTY = new Property(Sphere.class, "RADIUS");
  
  private double m_radius = 1.0D;
  

  public double getRadius() { return m_radius; }
  
  public void setRadius(double radius) {
    if (m_radius != radius) {
      m_radius = radius;
      onPropertyChange(RADIUS_PROPERTY);
      onBoundsChange();
    }
  }
  
  protected void updateBoundingBox()
  {
    m_boundingBox = new Box(-m_radius, -m_radius, -m_radius, m_radius, m_radius, m_radius);
  }
  
  protected void updateBoundingSphere() {
    m_boundingSphere = new edu.cmu.cs.stage3.math.Sphere(0.0D, 0.0D, 0.0D, m_radius);
  }
}
