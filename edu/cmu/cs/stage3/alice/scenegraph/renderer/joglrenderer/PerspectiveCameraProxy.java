package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.PerspectiveCamera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.awt.Rectangle;
import javax.media.opengl.GL;

















class PerspectiveCameraProxy
  extends CameraProxy
{
  private double[] m_plane = new double[4];
  
  PerspectiveCameraProxy() {}
  
  protected Rectangle getActualLetterboxedViewport(int width, int height) { return new Rectangle(0, 0, width, height); }
  

  protected double[] getActualNearPlane(double[] ret, int width, int height, double near)
  {
    ret[0] = m_plane[0];
    ret[1] = m_plane[1];
    ret[2] = m_plane[2];
    ret[3] = m_plane[3];
    return ret; }
  
  private double[] reuse_actualNearPlane = new double[4];
  
  protected void projection(Context context, int width, int height, float near, float far) {
    getActualNearPlane(reuse_actualNearPlane, width, height, near);
    gl.glFrustum(reuse_actualNearPlane[0], reuse_actualNearPlane[2], reuse_actualNearPlane[1], reuse_actualNearPlane[3], near, far);
  }
  
  protected void changed(Property property, Object value) {
    if (property == PerspectiveCamera.PLANE_PROPERTY) {
      double[] plane = (double[])value;
      System.arraycopy(plane, 0, m_plane, 0, m_plane.length);
    } else {
      super.changed(property, value);
    }
  }
}
