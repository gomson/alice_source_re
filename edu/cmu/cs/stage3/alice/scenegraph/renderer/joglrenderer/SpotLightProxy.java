package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.alice.scenegraph.SpotLight;


















class SpotLightProxy
  extends PointLightProxy
{
  private float m_inner;
  private float m_outer;
  private float m_falloff;
  private static final float RADIANS_TO_DEGREES_FACTOR = 57.29578F;
  
  SpotLightProxy() {}
  
  protected float[] getSpotDirection(float[] rv)
  {
    double[] absolute = getAbsoluteTransformation();
    rv[0] = ((float)absolute[8]);
    rv[1] = ((float)absolute[9]);
    rv[2] = ((float)absolute[10]);
    return rv;
  }
  



  protected float getSpotCutoff()
  {
    return m_outer * 57.29578F;
  }
  
  protected void changed(Property property, Object value) {
    if (property == SpotLight.INNER_BEAM_ANGLE_PROPERTY) {
      m_inner = ((Number)value).floatValue();
    } else if (property == SpotLight.OUTER_BEAM_ANGLE_PROPERTY) {
      m_outer = ((Number)value).floatValue();
    } else if (property == SpotLight.FALLOFF_PROPERTY) {
      m_falloff = ((Number)value).floatValue();
    } else {
      super.changed(property, value);
    }
  }
}
