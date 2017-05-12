package edu.cmu.cs.stage3.alice.scenegraph;




































public class LinearFog
  extends Fog
{
  public static final Property NEAR_DISTANCE_PROPERTY = new Property(LinearFog.class, "NEAR_DISTANCE");
  public static final Property FAR_DISTANCE_PROPERTY = new Property(LinearFog.class, "FAR_DISTANCE");
  private double m_nearDistance = 1.0D;
  private double m_farDistance = 256.0D;
  
  public LinearFog() {}
  
  public double getNearDistance() {
    return m_nearDistance;
  }
  





  public void setNearDistance(double nearDistance)
  {
    if (m_nearDistance != nearDistance) {
      m_nearDistance = nearDistance;
      onPropertyChange(NEAR_DISTANCE_PROPERTY);
    }
  }
  

  public double getFarDistance()
  {
    return m_farDistance;
  }
  





  public void setFarDistance(double farDistance)
  {
    if (m_farDistance != farDistance) {
      m_farDistance = farDistance;
      onPropertyChange(FAR_DISTANCE_PROPERTY);
    }
  }
}
