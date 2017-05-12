package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.camera.ProjectionCamera;

public class ProjectionViewVolumeDecorator extends ViewVolumeDecorator { public ProjectionViewVolumeDecorator() {}
  
  private ProjectionCamera m_projectionCamera = null;
  
  protected edu.cmu.cs.stage3.alice.core.Camera getCamera() {
    return getProjectionCamera();
  }
  
  public ProjectionCamera getProjectionCamera() { return m_projectionCamera; }
  
  public void setProjectionCamera(ProjectionCamera projectionCamera) {
    if (projectionCamera != m_projectionCamera) {
      m_projectionCamera = projectionCamera;
      markDirty();
      updateIfShowing();
    }
  }
  
  protected double[] getXYNearAndXYFar(double zNear, double zFar)
  {
    double angle = 0.5D;
    double aspect = 1.3333333333333333D;
    double yNear = zNear * Math.tan(angle);
    double yFar = zFar * Math.tan(angle);
    double xNear = aspect * yNear;
    double xFar = aspect * yFar;
    double[] r = { xNear, yNear, xFar, yFar };
    return r;
  }
}
