package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Background;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import javax.media.opengl.GL;

















class BackgroundProxy
  extends ElementProxy
{
  BackgroundProxy() {}
  
  private float[] m_color = new float[4];
  
  public void clear(RenderContext context) { gl.glClearColor(m_color[0], m_color[1], m_color[2], m_color[3]);
    gl.glClear(16640);
  }
  
  protected void changed(Property property, Object value) {
    if (property == Background.COLOR_PROPERTY) {
      copy(m_color, (Color)value);
    } else if (property != Background.TEXTURE_MAP_PROPERTY)
    {
      if (property != Background.TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY)
      {

        super.changed(property, value);
      }
    }
  }
}
