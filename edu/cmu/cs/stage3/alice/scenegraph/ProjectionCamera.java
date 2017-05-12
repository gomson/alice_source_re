package edu.cmu.cs.stage3.alice.scenegraph;

import javax.vecmath.Matrix4d;























public class ProjectionCamera
  extends Camera
{
  public static final Property PROJECTION_PROPERTY = new Property(ProjectionCamera.class, "PROJECTION");
  private Matrix4d m_projection = null;
  

  public ProjectionCamera() {}
  
  public Matrix4d getProjection()
  {
    return m_projection;
  }
  


  public void setProjection(Matrix4d projection)
  {
    if (notequal(m_projection, projection)) {
      m_projection = projection;
      onPropertyChange(PROJECTION_PROPERTY);
    }
  }
}
