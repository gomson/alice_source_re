package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.lang.Messages;
import javax.vecmath.Matrix4d;
























public abstract class Camera
  extends Component
{
  public static final Property NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY = new Property(Camera.class, "NEAR_CLIPPING_PLANE_DISTANCE");
  public static final Property FAR_CLIPPING_PLANE_DISTANCE_PROPERTY = new Property(Camera.class, "FAR_CLIPPING_PLANE_DISTANCE");
  public static final Property BACKGROUND_PROPERTY = new Property(Camera.class, "BACKGROUND");
  private double m_nearClippingPlaneDistance = 0.125D;
  private double m_farClippingPlaneDistance = 256.0D;
  private Background m_background = null;
  
  public Camera() {}
  
  protected void releasePass1() { if (m_background != null) {
      warnln(Messages.getString("WARNING__released_camera_") + this + " " + Messages.getString("still_has_background_") + m_background + ".");
      setBackground(null);
    }
    super.releasePass1();
  }
  

  public double getNearClippingPlaneDistance()
  {
    return m_nearClippingPlaneDistance;
  }
  







  public void setNearClippingPlaneDistance(double nearClippingPlaneDistance)
  {
    if (m_nearClippingPlaneDistance != nearClippingPlaneDistance) {
      m_nearClippingPlaneDistance = nearClippingPlaneDistance;
      onPropertyChange(NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY);
    }
  }
  

  public double getFarClippingPlaneDistance()
  {
    return m_farClippingPlaneDistance;
  }
  







  public void setFarClippingPlaneDistance(double farClippingPlaneDistance)
  {
    if (m_farClippingPlaneDistance != farClippingPlaneDistance) {
      m_farClippingPlaneDistance = farClippingPlaneDistance;
      onPropertyChange(FAR_CLIPPING_PLANE_DISTANCE_PROPERTY);
    }
  }
  


  public Background getBackground()
  {
    return m_background;
  }
  



  public void setBackground(Background background)
  {
    if (m_background != background) {
      m_background = background;
      onPropertyChange(BACKGROUND_PROPERTY);
    }
  }
  
  public abstract Matrix4d getProjection();
}
