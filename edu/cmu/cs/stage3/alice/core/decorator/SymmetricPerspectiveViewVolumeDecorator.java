package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;



















public class SymmetricPerspectiveViewVolumeDecorator
  extends ViewVolumeDecorator
{
  public SymmetricPerspectiveViewVolumeDecorator() {}
  
  private SymmetricPerspectiveCamera m_symmetricPerspectiveCamera = null;
  
  protected Camera getCamera() {
    return getSymmetricPerspectiveCamera();
  }
  
  public SymmetricPerspectiveCamera getSymmetricPerspectiveCamera() { return m_symmetricPerspectiveCamera; }
  
  public void setSymmetricPerspectiveCamera(SymmetricPerspectiveCamera symmetricPerspectiveCamera) {
    if (symmetricPerspectiveCamera != m_symmetricPerspectiveCamera) {
      m_symmetricPerspectiveCamera = symmetricPerspectiveCamera;
      markDirty();
      updateIfShowing();
    }
  }
  







  protected double[] getXYNearAndXYFar(double zNear, double zFar)
  {
    double angle = m_symmetricPerspectiveCamera.verticalViewingAngle.doubleValue(0.5D);
    



    double aspect = 1.3333333333333333D;
    
    double yNear = zNear * Math.tan(angle);
    double yFar = zFar * Math.tan(angle);
    double xNear = aspect * yNear;
    double xFar = aspect * yFar;
    double[] r = { xNear, yNear, xFar, yFar };
    return r;
  }
}
