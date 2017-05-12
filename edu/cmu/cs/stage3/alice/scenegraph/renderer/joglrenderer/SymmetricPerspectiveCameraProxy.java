package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;
import java.awt.Rectangle;
import javax.media.opengl.GL;












class SymmetricPerspectiveCameraProxy
  extends CameraProxy
{
  private double m_vertical;
  private double m_horizontal;
  private static final double DEFAULT_ACTUAL_VERTICAL = 0.5D;
  
  SymmetricPerspectiveCameraProxy() {}
  
  private static double radiansToDegrees(double radians)
  {
    return radians * 180.0D / 3.141592653589793D;
  }
  
  protected Rectangle getActualLetterboxedViewport(int width, int height) {
    if ((Double.isNaN(m_vertical)) || (Double.isNaN(m_vertical))) {
      return new Rectangle(0, 0, width, height);
    }
    double aspect = m_horizontal / m_vertical;
    double pixelAspect = width / height;
    if (aspect > pixelAspect) {
      int letterBoxedHeight = (int)(width / aspect + 0.5D);
      return new Rectangle(0, (height - letterBoxedHeight) / 2, width, letterBoxedHeight); }
    if (aspect < pixelAspect) {
      int letterBoxedWidth = (int)(height * aspect + 0.5D);
      return new Rectangle((width - letterBoxedWidth) / 2, 0, letterBoxedWidth, height);
    }
    return new Rectangle(0, 0, width, height);
  }
  


  public double getActualHorizontalViewingAngle(int width, int height)
  {
    if (Double.isNaN(m_horizontal)) {
      double aspect = width / height;
      if (Double.isNaN(m_vertical)) {
        return 0.5D * aspect;
      }
      return m_vertical * aspect;
    }
    
    return m_horizontal;
  }
  
  public double getActualVerticalViewingAngle(int width, int height) {
    if (Double.isNaN(m_vertical)) {
      double aspect = width / height;
      if (Double.isNaN(m_horizontal)) {
        return 0.5D;
      }
      return m_horizontal / aspect;
    }
    
    return m_vertical;
  }
  

  protected double[] getActualNearPlane(double[] ret, int width, int height, double near)
  {
    double vertical = m_vertical;
    double horizontal = m_horizontal;
    if (Double.isNaN(horizontal)) {
      if (Double.isNaN(vertical)) {
        vertical = 0.5D;
      }
      horizontal = vertical * width / height;
    }
    else if (Double.isNaN(vertical)) {
      vertical = horizontal * height / width;
    }
    

    double y = Math.tan(vertical * 0.5D) * near;
    double x = horizontal * y / vertical;
    ret[0] = (-x);
    ret[1] = (-y);
    ret[2] = x;
    ret[3] = y;
    return ret; }
  
  private double[] reuse_actualNearPlane = new double[4];
  
  protected void projection(Context context, int width, int height, float near, float far) {
    getActualNearPlane(reuse_actualNearPlane, width, height, near);
    gl.glFrustum(reuse_actualNearPlane[0], reuse_actualNearPlane[2], reuse_actualNearPlane[1], reuse_actualNearPlane[3], near, far);
  }
  

  protected void changed(Property property, Object value)
  {
    if (property == SymmetricPerspectiveCamera.VERTICAL_VIEWING_ANGLE_PROPERTY) {
      m_vertical = ((Number)value).doubleValue();
    } else if (property == SymmetricPerspectiveCamera.HORIZONTAL_VIEWING_ANGLE_PROPERTY) {
      m_horizontal = ((Number)value).doubleValue();
    } else {
      super.changed(property, value);
    }
  }
}
