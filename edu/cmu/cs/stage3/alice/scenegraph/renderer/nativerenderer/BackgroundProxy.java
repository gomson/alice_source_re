package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.TextureMap;
import java.awt.Rectangle;











public abstract class BackgroundProxy
  extends ElementProxy
{
  public BackgroundProxy() {}
  
  protected abstract void onColorChange(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  protected abstract void onTextureMapChange(TextureMapProxy paramTextureMapProxy);
  
  protected abstract void onTextureMapSourceRectangleChange(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  protected void changed(Property property, Object value)
  {
    if (property == Background.COLOR_PROPERTY) {
      Color color = (Color)value;
      if (color != null) {
        onColorChange(red, green, blue, alpha);
      } else {
        onColorChange(0.0D, 0.0D, 0.0D, 0.0D);
      }
    } else if (property == Background.TEXTURE_MAP_PROPERTY) {
      onTextureMapChange((TextureMapProxy)getProxyFor((TextureMap)value));
    } else if (property == Background.TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY) {
      Rectangle rect = (Rectangle)value;
      if (rect != null) {
        onTextureMapSourceRectangleChange(x, y, width, height);
      } else {
        onTextureMapSourceRectangleChange(0, 0, 1, 1);
      }
    } else {
      super.changed(property, value);
    }
  }
}
