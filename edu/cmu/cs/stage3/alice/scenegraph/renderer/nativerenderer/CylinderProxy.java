package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Cylinder;
import edu.cmu.cs.stage3.alice.scenegraph.Property;














public abstract class CylinderProxy
  extends ShapeProxy
{
  public CylinderProxy() {}
  
  protected abstract void onBaseRadiusChange(double paramDouble);
  
  protected abstract void onTopRadiusChange(double paramDouble);
  
  protected abstract void onHeightChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == Cylinder.BASE_RADIUS_PROPERTY) {
      onBaseRadiusChange(((Double)value).doubleValue());
    } else if (property == Cylinder.TOP_RADIUS_PROPERTY) {
      onTopRadiusChange(((Double)value).doubleValue());
    } else if (property == Cylinder.HEIGHT_PROPERTY) {
      onHeightChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
