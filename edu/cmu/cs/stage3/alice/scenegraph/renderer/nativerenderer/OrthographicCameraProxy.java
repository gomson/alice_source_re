package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
















public abstract class OrthographicCameraProxy
  extends CameraProxy
{
  public OrthographicCameraProxy() {}
  
  protected abstract void onPlaneChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected void changed(Property property, Object value)
  {
    if (property == OrthographicCamera.PLANE_PROPERTY) {
      double[] plane = (double[])value;
      if (plane != null) {
        onPlaneChange(plane[0], plane[1], plane[2], plane[3]);
      }
    } else {
      super.changed(property, value);
    }
  }
}
