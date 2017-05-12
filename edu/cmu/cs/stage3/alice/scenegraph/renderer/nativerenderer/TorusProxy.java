package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Torus;















public abstract class TorusProxy
  extends ShapeProxy
{
  public TorusProxy() {}
  
  protected abstract void onInnerRadiusChange(double paramDouble);
  
  protected abstract void onOuterRadiusChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == Torus.INNER_RADIUS_PROPERTY) {
      onInnerRadiusChange(((Double)value).doubleValue());
    } else if (property == Torus.OUTER_RADIUS_PROPERTY) {
      onOuterRadiusChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
