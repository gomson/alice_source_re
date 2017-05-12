package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Light;
import edu.cmu.cs.stage3.alice.scenegraph.Property;













public abstract class LightProxy
  extends AffectorProxy
{
  public LightProxy() {}
  
  protected abstract void onColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected abstract void onBrightnessChange(double paramDouble);
  
  protected abstract void onRangeChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == Light.COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color != null) {
        onColorChange(red, green, blue, alpha);
      } else {
        onColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
    } else if (property == Light.BRIGHTNESS_PROPERTY) {
      onBrightnessChange(((Double)value).doubleValue());
    } else if (property == Light.RANGE_PROPERTY) {
      onRangeChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
