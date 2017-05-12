package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.LinearFog;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import javax.media.opengl.GL;















class LinearFogProxy
  extends FogProxy
{
  private float m_near;
  private float m_far;
  
  LinearFogProxy() {}
  
  public void setup(RenderContext context)
  {
    super.setup(context);
    gl.glFogi(2917, 9729);
    gl.glFogf(2915, m_near);
    gl.glFogf(2916, m_far);
  }
  
  protected void changed(Property property, Object value) {
    if (property == LinearFog.NEAR_DISTANCE_PROPERTY) {
      m_near = ((Number)value).floatValue();
    } else if (property == LinearFog.FAR_DISTANCE_PROPERTY) {
      m_far = ((Number)value).floatValue();
    } else {
      super.changed(property, value);
    }
  }
}
