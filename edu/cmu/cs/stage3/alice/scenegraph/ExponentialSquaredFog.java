package edu.cmu.cs.stage3.alice.scenegraph;







































public class ExponentialSquaredFog
  extends Fog
{
  public static final Property DENSITY_PROPERTY = new Property(ExponentialSquaredFog.class, "DENSITY");
  private double m_density = 1.0D;
  
  public ExponentialSquaredFog() {}
  
  public double getDensity() {
    return m_density;
  }
  




  public void setDensity(double density)
  {
    if (m_density != density) {
      m_density = density;
      onPropertyChange(DENSITY_PROPERTY);
    }
  }
}
