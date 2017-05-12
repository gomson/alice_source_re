package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Fog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;















public abstract class FogProxy
  extends AffectorProxy
{
  public FogProxy() {}
  
  protected abstract void onColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected void changed(Property property, Object value)
  {
    if (property == Fog.COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color != null) {
        onColorChange(red, green, blue, alpha);
      } else {
        onColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
    } else {
      super.changed(property, value);
    }
  }
}
