package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Sprite;
















public abstract class SpriteProxy
  extends GeometryProxy
{
  public SpriteProxy() {}
  
  protected abstract void onRadiusChange(double paramDouble);
  
  protected void changed(Property property, Object value)
  {
    if (property == Sprite.RADIUS_PROPERTY) {
      onRadiusChange(((Double)value).doubleValue());
    } else {
      super.changed(property, value);
    }
  }
}
