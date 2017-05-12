package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.PointLight;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
















class PointLightProxy
  extends LightProxy
{
  private float m_constant;
  private float m_linear;
  private float m_quadratic;
  
  PointLightProxy() {}
  
  protected float[] getPosition(float[] rv)
  {
    double[] absolute = getAbsoluteTransformation();
    rv[0] = ((float)absolute[12]);
    rv[1] = ((float)absolute[13]);
    rv[2] = ((float)absolute[14]);
    rv[3] = 1.0F;
    return rv;
  }
  
  protected float getConstantAttenuation() {
    return m_constant;
  }
  
  protected float getLinearAttenuation() {
    return m_linear;
  }
  
  protected float getQuadraticAttenuation() {
    return m_quadratic;
  }
  
  protected void changed(Property property, Object value)
  {
    if (property == PointLight.CONSTANT_ATTENUATION_PROPERTY) {
      m_constant = ((Number)value).floatValue();
    } else if (property == PointLight.LINEAR_ATTENUATION_PROPERTY) {
      m_linear = ((Number)value).floatValue();
    } else if (property == PointLight.QUADRATIC_ATTENUATION_PROPERTY) {
      m_quadratic = ((Number)value).floatValue();
    } else {
      super.changed(property, value);
    }
  }
}
