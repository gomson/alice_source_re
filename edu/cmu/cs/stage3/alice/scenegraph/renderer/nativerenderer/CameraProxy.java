package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Camera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;













public abstract class CameraProxy
  extends ComponentProxy
{
  public CameraProxy() {}
  
  protected abstract void onNearClippingPlaneDistanceChange(double paramDouble);
  
  protected abstract void onFarClippingPlaneDistanceChange(double paramDouble);
  
  protected abstract void onBackgroundChange(BackgroundProxy paramBackgroundProxy);
  
  protected void changed(Property property, Object value)
  {
    if (property == Camera.NEAR_CLIPPING_PLANE_DISTANCE_PROPERTY) {
      onNearClippingPlaneDistanceChange(((Double)value).doubleValue());
    } else if (property == Camera.FAR_CLIPPING_PLANE_DISTANCE_PROPERTY) {
      onFarClippingPlaneDistanceChange(((Double)value).doubleValue());
    } else if (property == Camera.BACKGROUND_PROPERTY) {
      onBackgroundChange((BackgroundProxy)getProxyFor((Background)value));
    } else {
      super.changed(property, value);
    }
  }
}
