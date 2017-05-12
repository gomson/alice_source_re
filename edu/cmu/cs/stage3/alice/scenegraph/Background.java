package edu.cmu.cs.stage3.alice.scenegraph;

import java.awt.Rectangle;























public class Background
  extends Element
{
  public static final Property COLOR_PROPERTY = new Property(Background.class, "COLOR");
  public static final Property TEXTURE_MAP_PROPERTY = new Property(Background.class, "TEXTURE_MAP");
  public static final Property TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY = new Property(Background.class, "TEXTURE_MAP_SOURCE_RECTANGLE");
  private Color m_color = new Color(0.0F, 0.0F, 1.0F, 1.0F);
  private TextureMap m_textureMap = null;
  private Rectangle m_textureMapSourceRectangle = null;
  
  public Background() {}
  
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
  

  public TextureMap getTextureMap()
  {
    return m_textureMap;
  }
  








  public void setTextureMap(TextureMap textureMap)
  {
    if (notequal(m_textureMap, textureMap)) {
      m_textureMap = textureMap;
      onPropertyChange(TEXTURE_MAP_PROPERTY);
    }
  }
  


  public Rectangle getTextureMapSourceRectangle()
  {
    return m_textureMapSourceRectangle;
  }
  






  public void setTextureMapSourceRectangle(Rectangle textureMapSourceRectangle)
  {
    if (notequal(m_textureMapSourceRectangle, textureMapSourceRectangle)) {
      m_textureMapSourceRectangle = textureMapSourceRectangle;
      onPropertyChange(TEXTURE_MAP_SOURCE_RECTANGLE_PROPERTY);
    }
  }
}
