package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Sphere;
















public abstract class SphereProxy
  extends ShapeProxy
{
  public SphereProxy() {}
  
  protected abstract void onRadiusChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == Sphere.RADIUS_PROPERTY) {
      onRadiusChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
