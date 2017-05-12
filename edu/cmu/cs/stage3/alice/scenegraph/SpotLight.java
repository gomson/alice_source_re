package edu.cmu.cs.stage3.alice.scenegraph;




























public class SpotLight
  extends PointLight
{
  public static final Property INNER_BEAM_ANGLE_PROPERTY = new Property(SpotLight.class, "INNER_BEAM_ANGLE");
  public static final Property OUTER_BEAM_ANGLE_PROPERTY = new Property(SpotLight.class, "OUTER_BEAM_ANGLE");
  public static final Property FALLOFF_PROPERTY = new Property(SpotLight.class, "FALLOFF");
  private double m_innerBeamAngle = 0.4D;
  private double m_outerBeamAngle = 0.5D;
  private double m_falloff = 1.0D;
  
  public SpotLight() {}
  
  public double getInnerBeamAngle() {
    return m_innerBeamAngle;
  }
  




  public void setInnerBeamAngle(double innerBeamAngle)
  {
    if (m_innerBeamAngle != innerBeamAngle) {
      m_innerBeamAngle = innerBeamAngle;
      onPropertyChange(INNER_BEAM_ANGLE_PROPERTY);
    }
  }
  

  public double getOuterBeamAngle()
  {
    return m_outerBeamAngle;
  }
  




  public void setOuterBeamAngle(double outerBeamAngle)
  {
    if (m_outerBeamAngle != outerBeamAngle) {
      m_outerBeamAngle = outerBeamAngle;
      onPropertyChange(OUTER_BEAM_ANGLE_PROPERTY);
    }
  }
  

  public double getFalloff()
  {
    return m_falloff;
  }
  




  public void setFalloff(double falloff)
  {
    if (m_falloff != falloff) {
      m_falloff = falloff;
      onPropertyChange(FALLOFF_PROPERTY);
    }
  }
}
