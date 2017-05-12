package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.camera.PerspectiveCamera;




















public class PerspectiveViewVolumeDecorator
  extends ViewVolumeDecorator
{
  public PerspectiveViewVolumeDecorator() {}
  
  private PerspectiveCamera m_perspectiveCamera = null;
  
  protected Camera getCamera() {
    return getPerspectiveCamera();
  }
  
  public PerspectiveCamera getPerspectiveCamera() { return m_perspectiveCamera; }
  
  public void setPerspectiveCamera(PerspectiveCamera perspectiveCamera) {
    if (perspectiveCamera != m_perspectiveCamera) {
      m_perspectiveCamera = perspectiveCamera;
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
