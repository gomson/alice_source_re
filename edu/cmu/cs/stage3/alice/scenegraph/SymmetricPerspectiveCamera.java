package edu.cmu.cs.stage3.alice.scenegraph;

import javax.vecmath.Matrix4d;
























public class SymmetricPerspectiveCamera
  extends Camera
{
  public static final Property VERTICAL_VIEWING_ANGLE_PROPERTY = new Property(SymmetricPerspectiveCamera.class, "VERTICAL_VIEWING_ANGLE");
  public static final Property HORIZONTAL_VIEWING_ANGLE_PROPERTY = new Property(SymmetricPerspectiveCamera.class, "HORIZONTAL_VIEWING_ANGLE");
  private double m_verticalViewingAngle = 0.5D;
  private double m_horizontalViewingAngle = NaN.0D;
  
  public SymmetricPerspectiveCamera() {}
  
  public double getHorizontalViewingAngle()
  {
    return m_horizontalViewingAngle;
  }
  






  public void setHorizontalViewingAngle(double horizontalViewingAngle)
  {
    if (m_horizontalViewingAngle != horizontalViewingAngle) {
      m_horizontalViewingAngle = horizontalViewingAngle;
      onPropertyChange(HORIZONTAL_VIEWING_ANGLE_PROPERTY);
    }
  }
  


  public double getVerticalViewingAngle()
  {
    return m_verticalViewingAngle;
  }
  






  public void setVerticalViewingAngle(double verticalViewingAngle)
  {
    if (m_verticalViewingAngle != verticalViewingAngle) {
      m_verticalViewingAngle = verticalViewingAngle;
      onPropertyChange(VERTICAL_VIEWING_ANGLE_PROPERTY);
    }
  }
  

  public Matrix4d getProjection()
  {
    Matrix4d m = new Matrix4d();
    double w = 1.0D / Math.tan(getHorizontalViewingAngle() * 0.5D);
    double h = 1.0D / Math.tan(getVerticalViewingAngle() * 0.5D);
    double farPlane = getFarClippingPlaneDistance();
    double nearPlane = getNearClippingPlaneDistance();
    double Q = farPlane / (farPlane - nearPlane);
    m00 = w;
    m11 = h;
    m22 = Q;
    m32 = (-Q * nearPlane);
    m23 = 1.0D;
    return m;
  }
}
