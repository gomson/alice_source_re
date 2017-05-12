package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.awt.Rectangle;
import javax.media.opengl.GL;

















class OrthographicCameraProxy
  extends CameraProxy
{
  private double[] m_plane = new double[4];
  
  OrthographicCameraProxy() {}
  
  protected Rectangle getActualLetterboxedViewport(int width, int height) { return new Rectangle(0, 0, width, height); }
  

  protected double[] getActualNearPlane(double[] ret, int width, int height, double near)
  {
    double minX = m_plane[0];
    double maxX = m_plane[2];
    double minY = m_plane[1];
    double maxY = m_plane[3];
    if ((Double.isNaN(minX)) || (Double.isNaN(maxX))) {
      if ((Double.isNaN(minY)) || (Double.isNaN(maxY))) {
        minY = -1.0D;
        maxY = 1.0D;
      }
      double factor = width / height;
      minX = factor * minY;
      maxX = factor * maxY;
    }
    else if ((Double.isNaN(minY)) || (Double.isNaN(maxY))) {
      double factor = height / width;
      minY = factor * minX;
      maxY = factor * maxY;
    }
    
    ret[0] = minX;
    ret[1] = minY;
    ret[2] = maxX;
    ret[3] = maxY;
    return ret; }
  
  private double[] reuse_actualNearPlane = new double[4];
  
  protected void projection(Context context, int width, int height, float near, float far) {
    getActualNearPlane(reuse_actualNearPlane, width, height);
    
    gl.glOrtho(reuse_actualNearPlane[0], reuse_actualNearPlane[2], reuse_actualNearPlane[1], reuse_actualNearPlane[3], near, far);
  }
  
  protected void changed(Property property, Object value) {
    if (property == OrthographicCamera.PLANE_PROPERTY) {
      double[] plane = (double[])value;
      System.arraycopy(plane, 0, m_plane, 0, m_plane.length);
    } else {
      super.changed(property, value);
    }
  }
}
