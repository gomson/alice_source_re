package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Box;
import edu.cmu.cs.stage3.alice.scenegraph.Property;














public abstract class BoxProxy
  extends ShapeProxy
{
  public BoxProxy() {}
  
  protected abstract void onWidthChange(double paramDouble);
  
  protected abstract void onHeightChange(double paramDouble);
  
  protected abstract void onDepthChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == Box.WIDTH_PROPERTY) {
      onWidthChange(((Double)value).doubleValue());
    } else if (property == Box.HEIGHT_PROPERTY) {
      onHeightChange(((Double)value).doubleValue());
    } else if (property == Box.DEPTH_PROPERTY) {
      onDepthChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
