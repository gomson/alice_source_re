package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.ProjectionCamera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.awt.Rectangle;
import java.nio.DoubleBuffer;
import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;















class ProjectionCameraProxy
  extends CameraProxy
{
  private double[] m_projection = new double[16];
  private DoubleBuffer m_projectionBuffer = DoubleBuffer.wrap(m_projection);
  
  ProjectionCameraProxy() {}
  
  protected Rectangle getActualLetterboxedViewport(int width, int height) { return new Rectangle(0, 0, width, height); }
  

  protected double[] getActualNearPlane(double[] ret, int width, int height, double near)
  {
    ret[0] = NaN.0D;
    ret[1] = NaN.0D;
    ret[2] = NaN.0D;
    ret[3] = NaN.0D;
    return ret;
  }
  
  protected void projection(Context context, int width, int height, float near, float far) {
    gl.glLoadMatrixd(m_projectionBuffer);
  }
  
  protected void changed(Property property, Object value) {
    if (property == ProjectionCamera.PROJECTION_PROPERTY) {
      copy(m_projection, (Matrix4d)value);
    } else {
      super.changed(property, value);
    }
  }
}
