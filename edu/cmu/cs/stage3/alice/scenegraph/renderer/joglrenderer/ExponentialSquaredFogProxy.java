package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.ExponentialFog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import javax.media.opengl.GL;















class ExponentialSquaredFogProxy
  extends FogProxy
{
  private float m_density;
  
  ExponentialSquaredFogProxy() {}
  
  public void setup(RenderContext context)
  {
    super.setup(context);
    gl.glFogi(2917, 2049);
    gl.glFogf(2914, m_density);
  }
  
  protected void changed(Property property, Object value) {
    if (property == ExponentialFog.DENSITY_PROPERTY) {
      m_density = ((Number)value).floatValue();
    } else {
      super.changed(property, value);
    }
  }
}
