package edu.cmu.cs.stage3.alice.scenegraph;

























public abstract class Fog
  extends Affector
{
  public static final Property COLOR_PROPERTY = new Property(Fog.class, "COLOR");
  private Color m_color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
  
  public Fog() {}
  
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
}
