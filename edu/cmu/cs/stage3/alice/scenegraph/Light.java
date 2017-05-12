package edu.cmu.cs.stage3.alice.scenegraph;

























public abstract class Light
  extends Affector
{
  public static final Property COLOR_PROPERTY = new Property(Light.class, "COLOR");
  public static final Property BRIGHTNESS_PROPERTY = new Property(Light.class, "BRIGHTNESS");
  public static final Property RANGE_PROPERTY = new Property(Light.class, "RANGE");
  private Color m_color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
  private double m_brightness = 1.0D;
  private double m_range = 256.0D;
  
  public Light() {}
  
  public Color getColor()
  {
    return m_color;
  }
  





  public void setColor(Color color)
  {
    if (notequal(m_color, color)) {
      m_color = color;
      onPropertyChange(COLOR_PROPERTY);
    }
  }
  


  public double getBrightness()
  {
    return m_brightness;
  }
  





  public void setBrightness(double brightness)
  {
    if (m_brightness != brightness) {
      m_brightness = brightness;
      onPropertyChange(BRIGHTNESS_PROPERTY);
    }
  }
  


  public double getRange()
  {
    return m_range;
  }
  





  public void setRange(double range)
  {
    if (m_range != range) {
      m_range = range;
      onPropertyChange(RANGE_PROPERTY);
    }
  }
}
