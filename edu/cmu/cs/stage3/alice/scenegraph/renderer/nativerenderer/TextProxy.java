package edu.cmu.cs.stage3.alice.scenegraph.renderer.nativerenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.Text;
import java.awt.Font;
import javax.vecmath.Vector3d;












public abstract class TextProxy
  extends GeometryProxy
{
  public TextProxy() {}
  
  protected abstract void onTextChange(String paramString);
  
  protected abstract void onFontChange(String paramString, int paramInt1, int paramInt2);
  
  protected abstract void onExtrusionChange(double paramDouble1, double paramDouble2, double paramDouble3);
  
  protected void changed(Property property, Object value)
  {
    if (property == Text.TEXT_PROPERTY) {
      onTextChange((String)value);
    } else if (property == Text.FONT_PROPERTY) {
      Font font = (Font)value;
      onFontChange(font.getName(), font.getStyle(), font.getSize());
    } else if (property == Text.EXTRUSION_PROPERTY) {
      Vector3d v = (Vector3d)value;
      onExtrusionChange(x, y, z);
    } else {
      super.changed(property, value);
    }
  }
}
