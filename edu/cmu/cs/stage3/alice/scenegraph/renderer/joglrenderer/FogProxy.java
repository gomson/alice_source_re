package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Fog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;














abstract class FogProxy
  extends AffectorProxy
{
  FogProxy() {}
  
  private float[] m_color = new float[4];
  private FloatBuffer m_colorBuffer = FloatBuffer.wrap(m_color);
  
  protected void changed(Property property, Object value) {
    if (property == Fog.COLOR_PROPERTY) {
      copy(m_color, (Color)value);
    } else {
      super.changed(property, value);
    }
  }
  
  public void setup(RenderContext context) {
    context.setIsFogEnabled(true);
    gl.glFogfv(2918, m_colorBuffer);
  }
}
