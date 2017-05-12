package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.ExponentialSquaredFog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
















public abstract class ExponentialSquaredFogProxy
  extends FogProxy
{
  public ExponentialSquaredFogProxy() {}
  
  protected abstract void onDensityChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == ExponentialSquaredFog.DENSITY_PROPERTY) {
      onDensityChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
