package edu.cmu.cs.stage3.alice.scenegraph;






































public class ExponentialFog
  extends Fog
{
  public static final Property DENSITY_PROPERTY = new Property(ExponentialFog.class, "DENSITY");
  private double m_density = 1.0D;
  
  public ExponentialFog() {}
  
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
