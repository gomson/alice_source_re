package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.LinearFog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;















public abstract class LinearFogProxy
  extends FogProxy
{
  public LinearFogProxy() {}
  
  protected abstract void onNearDistanceChange(double paramDouble);
  
  protected abstract void onFarDistanceChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == LinearFog.NEAR_DISTANCE_PROPERTY) {
      onNearDistanceChange(((Double)value).doubleValue());
    } else if (property == LinearFog.FAR_DISTANCE_PROPERTY) {
      onFarDistanceChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
