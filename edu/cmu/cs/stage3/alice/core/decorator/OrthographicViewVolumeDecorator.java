package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera;




















public class OrthographicViewVolumeDecorator
  extends ViewVolumeDecorator
{
  public OrthographicViewVolumeDecorator() {}
  
  private OrthographicCamera m_orthographicCamera = null;
  
  protected Camera getCamera() {
    return getOrthographicCamera();
  }
  
  public OrthographicCamera getOrthographicCamera() { return m_orthographicCamera; }
  
  public void setOrthographicCamera(OrthographicCamera orthographicCamera) {
    if (orthographicCamera != m_orthographicCamera) {
      m_orthographicCamera = orthographicCamera;
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
