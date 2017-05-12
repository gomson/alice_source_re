package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.PointLight;
import edu.cmu.cs.stage3.alice.scenegraph.Property;














public abstract class PointLightProxy
  extends LightProxy
{
  public PointLightProxy() {}
  
  protected abstract void onConstantAttenuationChange(double paramDouble);
  
  protected abstract void onLinearAttenuationChange(double paramDouble);
  
  protected abstract void onQuadraticAttenuationChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == PointLight.CONSTANT_ATTENUATION_PROPERTY) {
      onConstantAttenuationChange(((Double)value).doubleValue());
    } else if (property == PointLight.LINEAR_ATTENUATION_PROPERTY) {
      onLinearAttenuationChange(((Double)value).doubleValue());
    } else if (property == PointLight.QUADRATIC_ATTENUATION_PROPERTY) {
      onQuadraticAttenuationChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
