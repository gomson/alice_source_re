package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.SymmetricPerspectiveCamera;















public abstract class SymmetricPerspectiveCameraProxy
  extends CameraProxy
{
  public SymmetricPerspectiveCameraProxy() {}
  
  protected abstract void onVerticalViewingAngleChange(double paramDouble);
  
  protected abstract void onHorizontalViewingAngleChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == SymmetricPerspectiveCamera.VERTICAL_VIEWING_ANGLE_PROPERTY) {
      onVerticalViewingAngleChange(((Double)value).doubleValue());
    } else if (property == SymmetricPerspectiveCamera.HORIZONTAL_VIEWING_ANGLE_PROPERTY) {
      onHorizontalViewingAngleChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
