package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.SpotLight;














public abstract class SpotLightProxy
  extends PointLightProxy
{
  public SpotLightProxy() {}
  
  protected abstract void onInnerBeamAngleChange(double paramDouble);
  
  protected abstract void onOuterBeamAngleChange(double paramDouble);
  
  protected abstract void onFalloffChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == SpotLight.INNER_BEAM_ANGLE_PROPERTY) {
      onInnerBeamAngleChange(((Double)value).doubleValue());
    } else if (property == SpotLight.OUTER_BEAM_ANGLE_PROPERTY) {
      onOuterBeamAngleChange(((Double)value).doubleValue());
    } else if (property == SpotLight.FALLOFF_PROPERTY) {
      onFalloffChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
