package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Color;





















public abstract class Light
  extends Affector
{
  public final ColorProperty lightColor = new ColorProperty(this, "lightColor", null);
  public final NumberProperty brightness = new NumberProperty(this, "brightness", new Double(1.0D));
  public final NumberProperty range = new NumberProperty(this, "range", new Double(256.0D));
  private edu.cmu.cs.stage3.alice.scenegraph.Light m_sgLight;
  
  public edu.cmu.cs.stage3.alice.scenegraph.Affector getSceneGraphAffector() {
    return getSceneGraphLight();
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.Light getSceneGraphLight() { return m_sgLight; }
  
  protected Light(edu.cmu.cs.stage3.alice.scenegraph.Light sgLight)
  {
    m_sgLight = sgLight;
    m_sgLight.setParent(getSceneGraphTransformable());
    m_sgLight.setBonus(this);
    color.set(m_sgLight.getColor());
    range.set(new Double(m_sgLight.getRange()));
  }
  
  protected void internalRelease(int pass) {
    switch (pass) {
    case 1: 
      m_sgLight.setParent(null);
      break;
    case 2: 
      m_sgLight.release();
      m_sgLight = null;
    }
    
    super.internalRelease(pass);
  }
  
  protected void nameValueChanged(String value)
  {
    super.nameValueChanged(value);
    String s = null;
    if (value != null) {
      s = value + ".m_sgLight";
    }
    m_sgLight.setName(s);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == color) {
      if (lightColor.getColorValue() == null) {
        m_sgLight.setColor((Color)value);
      }
      super.propertyChanged(property, value);
    } else if (property == lightColor) {
      if (value == null) {
        m_sgLight.setColor(color.getColorValue());
      }
    } else if (property == range) {
      double d = NaN.0D;
      if (value != null) {
        d = ((Number)value).doubleValue();
      }
      m_sgLight.setRange(d);
    } else if (property == brightness) {
      double d = NaN.0D;
      if (value != null) {
        d = ((Number)value).doubleValue();
      }
      m_sgLight.setBrightness(d);
    } else {
      super.propertyChanged(property, value);
    }
  }
}
