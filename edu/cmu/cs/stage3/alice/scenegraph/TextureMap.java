package edu.cmu.cs.stage3.alice.scenegraph;

import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget;
import java.awt.Image;




















public class TextureMap
  extends Element
{
  public TextureMap() {}
  
  public static final Property IMAGE_PROPERTY = new Property(TextureMap.class, "IMAGE");
  public static final Property FORMAT_PROPERTY = new Property(TextureMap.class, "FORMAT");
  
  public static final int RGB = 1;
  public static final int ALPHA = 2;
  public static final int RGBA = 3;
  public static final int LUMINANCE = 4;
  public static final int LUMINANCE_PLUS_ALPHA = 6;
  
  private int m_format = 1;
  private Image m_image = null;
  private RenderTarget m_renderTargetWithLatestImage = null;
  
  public Image getImage() {
    if (m_renderTargetWithLatestImage != null) {
      m_image = m_renderTargetWithLatestImage.getImage(this);
    }
    
    return m_image;
  }
  
  public void setImage(Image image) { if (m_image != image) {
      m_image = image;
      onPropertyChange(IMAGE_PROPERTY);
    }
    m_renderTargetWithLatestImage = null;
  }
  
  public void touchImage() {
    onPropertyChange(IMAGE_PROPERTY);
    m_renderTargetWithLatestImage = null;
  }
  
  public RenderTarget getRenderTargetWithLatestImage() {
    return m_renderTargetWithLatestImage;
  }
  
  public void setRenderTargetWithLatestImage(RenderTarget renderTargetWithLatestImage) { m_renderTargetWithLatestImage = renderTargetWithLatestImage; }
  


  public int getFormat() { return m_format; }
  
  public void setFormat(int format) {
    if (m_format != format) {
      m_format = format;
      onPropertyChange(FORMAT_PROPERTY);
    }
  }
}
