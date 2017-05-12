package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.TextureMap;





















public class TextureMapProperty
  extends ElementProperty
{
  public TextureMapProperty(Element owner, String name, TextureMap defaultValue)
  {
    super(owner, name, defaultValue, TextureMap.class);
  }
  
  public TextureMap getTextureMapValue() { return (TextureMap)getElementValue(); }
}
