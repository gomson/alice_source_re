package edu.cmu.cs.stage3.alice.scenegraph;












public class Torus
  extends Shape
{
  public Torus() {}
  











  public static final Property INNER_RADIUS_PROPERTY = new Property(Torus.class, "INNER_RADIUS");
  public static final Property OUTER_RADIUS_PROPERTY = new Property(Torus.class, "OUTER_RADIUS");
  
  private double m_innerRadius = 1.0D;
  private double m_outerRadius = 2.0D;
  

  public double getInnerRadius() { return m_innerRadius; }
  
  public void setInnerRadius(double innerRadius) {
    if (m_innerRadius != innerRadius) {
      m_innerRadius = innerRadius;
      onPropertyChange(INNER_RADIUS_PROPERTY);
      onBoundsChange();
    }
  }
  

  public double getOuterRadius() { return m_outerRadius; }
  
  public void setOuterRadius(double outerRadius) {
    if (m_outerRadius != outerRadius) {
      m_outerRadius = outerRadius;
      onPropertyChange(OUTER_RADIUS_PROPERTY);
      onBoundsChange();
    }
  }
  
  protected void updateBoundingBox() {}
  
  protected void updateBoundingSphere() {}
}
