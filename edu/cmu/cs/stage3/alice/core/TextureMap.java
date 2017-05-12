package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.ImageProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import java.awt.Image;





















public class TextureMap
  extends Element
{
  public final ImageProperty image = new ImageProperty(this, "image", null);
  public final NumberProperty format = new NumberProperty(this, "format", new Integer(0));
  private edu.cmu.cs.stage3.alice.scenegraph.TextureMap m_sgTextureMap;
  
  public TextureMap() {
    m_sgTextureMap = new edu.cmu.cs.stage3.alice.scenegraph.TextureMap();
    m_sgTextureMap.setBonus(this);
  }
  
  protected void internalRelease(int pass) {
    switch (pass) {
    case 2: 
      m_sgTextureMap.release();
      m_sgTextureMap = null;
    }
    
    super.internalRelease(pass);
  }
  
  public edu.cmu.cs.stage3.alice.scenegraph.TextureMap getSceneGraphTextureMap() { return m_sgTextureMap; }
  
  protected void nameValueChanged(String value)
  {
    super.nameValueChanged(value);
    String s = null;
    if (value != null) {
      s = value + ".m_sgTextureMap";
    }
    m_sgTextureMap.setName(s);
  }
  
  private void imageValueChanged(Image value) { m_sgTextureMap.setImage(value); }
  
  private void formatValueChanged(Number value) {
    if (value != null) {
      m_sgTextureMap.setFormat(value.intValue());
    } else {
      m_sgTextureMap.setFormat(0);
    }
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == image) {
      imageValueChanged((Image)value);
    } else if (property == format) {
      formatValueChanged((Number)value);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public void touchImage() {
    m_sgTextureMap.touchImage();
  }
}
