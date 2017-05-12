package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.math.Sphere;
import javax.vecmath.Matrix4d;




















public class Sprite
  extends Geometry
{
  public Sprite() {}
  
  public static final Property RADIUS_PROPERTY = new Property(Sprite.class, "RADIUS");
  private double m_radius = 1.0D;
  
  public double getRadius() { return m_radius; }
  
  public void setRadius(double radius) {
    if (m_radius != radius) {
      m_radius = radius;
      m_boundingSphere.setRadius(m_radius);
      onPropertyChange(RADIUS_PROPERTY);
      onBoundsChange();
    }
  }
  
  protected void updateBoundingBox() {}
  
  protected void updateBoundingSphere() {}
  
  public void transform(Matrix4d trans) {}
}
