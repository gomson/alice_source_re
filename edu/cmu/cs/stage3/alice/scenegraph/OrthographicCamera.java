package edu.cmu.cs.stage3.alice.scenegraph;

import javax.vecmath.Matrix4d;























public class OrthographicCamera
  extends Camera
{
  public static final Property PLANE_PROPERTY = new Property(OrthographicCamera.class, "PLANE");
  private double[] m_plane = { -1.0D, -1.0D, 1.0D, 1.0D };
  
  public OrthographicCamera() {}
  
  public double[] getPlane()
  {
    return m_plane;
  }
  





  public void setPlane(double[] plane)
  {
    if (notequal(m_plane, plane)) {
      m_plane = plane;
      onPropertyChange(PLANE_PROPERTY);
    }
  }
  
  public void setPlane(double minX, double minY, double maxX, double maxY) { setPlane(new double[] { minX, minY, maxX, maxY }); }
  


  public Matrix4d getProjection()
  {
    Matrix4d m = new Matrix4d();
    return m;
  }
}
