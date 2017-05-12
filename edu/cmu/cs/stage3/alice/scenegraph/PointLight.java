package edu.cmu.cs.stage3.alice.scenegraph;











































public class PointLight
  extends Light
{
  public static final Property CONSTANT_ATTENUATION_PROPERTY = new Property(PointLight.class, "CONSTANT_ATTENUATION");
  public static final Property LINEAR_ATTENUATION_PROPERTY = new Property(PointLight.class, "LINEAR_ATTENUATION");
  public static final Property QUADRATIC_ATTENUATION_PROPERTY = new Property(PointLight.class, "QUADRATIC_ATTENUATION");
  private double m_constantAttenuation = 1.0D;
  private double m_linearAttenuation = 0.0D;
  private double m_quadraticAttenuation = 0.0D;
  
  public PointLight() {}
  
  public double getConstantAttenuation() {
    return m_constantAttenuation;
  }
  






  public void setConstantAttenuation(double constantAttenuation)
  {
    if (m_constantAttenuation != constantAttenuation) {
      m_constantAttenuation = constantAttenuation;
      onPropertyChange(CONSTANT_ATTENUATION_PROPERTY);
    }
  }
  

  public double getLinearAttenuation()
  {
    return m_linearAttenuation;
  }
  






  public void setLinearAttenuation(double linearAttenuation)
  {
    if (m_linearAttenuation != linearAttenuation) {
      m_linearAttenuation = linearAttenuation;
      onPropertyChange(LINEAR_ATTENUATION_PROPERTY);
    }
  }
  

  public double getQuadraticAttenuation()
  {
    return m_quadraticAttenuation;
  }
  






  public void setQuadraticAttenuation(double quadraticAttenuation)
  {
    if (m_quadraticAttenuation != quadraticAttenuation) {
      m_quadraticAttenuation = quadraticAttenuation;
      onPropertyChange(QUADRATIC_ATTENUATION_PROPERTY);
    }
  }
}
