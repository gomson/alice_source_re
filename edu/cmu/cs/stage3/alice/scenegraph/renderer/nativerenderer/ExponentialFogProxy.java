package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.ExponentialFog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
















public abstract class ExponentialFogProxy
  extends FogProxy
{
  public ExponentialFogProxy() {}
  
  protected abstract void onDensityChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == ExponentialFog.DENSITY_PROPERTY) {
      onDensityChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
