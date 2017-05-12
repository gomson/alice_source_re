package edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer;

import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Light;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
















abstract class LightProxy
  extends AffectorProxy
{
  LightProxy() {}
  
  private float[] m_colorTimesBrightness = new float[4];
  private float[] m_color = new float[4];
  private float m_brightness;
  private float m_range;
  
  private float[] reuse_position = new float[4];
  private float[] reuse_spotDirection = new float[3];
  
  private FloatBuffer reuse_positionBuffer = FloatBuffer.wrap(reuse_position);
  private FloatBuffer reuse_spotDirectionBuffer = FloatBuffer.wrap(reuse_spotDirection);
  
  private FloatBuffer m_colorTimesBrightnessBuffer = FloatBuffer.wrap(m_colorTimesBrightness);
  
  protected float[] getPosition(float[] rv) {
    rv[0] = 0.0F;
    rv[1] = 0.0F;
    rv[2] = -1.0F;
    rv[3] = 0.0F;
    return rv;
  }
  
  protected float[] getSpotDirection(float[] rv) { rv[0] = 0.0F;
    rv[1] = 0.0F;
    rv[2] = 1.0F;
    return rv;
  }
  
  protected float getSpotExponent() { return 0.0F; }
  
  protected float getSpotCutoff() {
    return 180.0F;
  }
  
  protected float getConstantAttenuation() { return 1.0F; }
  
  protected float getLinearAttenuation() {
    return 0.0F;
  }
  
  protected float getQuadraticAttenuation() { return 0.0F; }
  
  protected void setup(RenderContext context, int id)
  {
    gl.glEnable(id);
    


    gl.glLightfv(id, 4609, m_colorTimesBrightnessBuffer);
    

    gl.glLightfv(id, 4610, m_colorTimesBrightnessBuffer);
    
    getPosition(reuse_position);
    gl.glLightfv(id, 4611, reuse_positionBuffer);
    
    getSpotDirection(reuse_spotDirection);
    gl.glLightfv(id, 4612, reuse_spotDirectionBuffer);
    
    gl.glLightf(id, 4613, getSpotExponent());
    gl.glLightf(id, 4614, getSpotCutoff());
    gl.glLightf(id, 4615, getConstantAttenuation());
    gl.glLightf(id, 4616, getLinearAttenuation());
    gl.glLightf(id, 4617, getQuadraticAttenuation());
  }
  
  public void setup(RenderContext context)
  {
    if ((this instanceof AmbientLightProxy)) {
      context.addAmbient(m_colorTimesBrightness);
    } else {
      int id = context.getNextLightID();
      setup(context, id);
    }
  }
  
  private void updateColorTimesBrightness() {
    m_colorTimesBrightness[0] = (m_color[0] * m_brightness);
    m_colorTimesBrightness[1] = (m_color[1] * m_brightness);
    m_colorTimesBrightness[2] = (m_color[2] * m_brightness);
    m_colorTimesBrightness[3] = 1.0F;
  }
  
  protected void changed(Property property, Object value)
  {
    if (property == Light.COLOR_PROPERTY) {
      copy(m_color, (Color)value);
      updateColorTimesBrightness();
    } else if (property == Light.BRIGHTNESS_PROPERTY) {
      m_brightness = ((Number)value).floatValue();
      updateColorTimesBrightness();
    } else if (property == Light.RANGE_PROPERTY) {
      m_range = ((Number)value).floatValue();
    } else {
      super.changed(property, value);
    }
  }
}
