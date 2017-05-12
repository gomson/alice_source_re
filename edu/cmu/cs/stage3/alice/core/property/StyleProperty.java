package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Style;





















public class StyleProperty
  extends ObjectProperty
{
  public StyleProperty(Element owner, String name, Style defaultValue)
  {
    super(owner, name, defaultValue, Style.class);
  }
  
  public Style getStyleValue() { return (Style)getValue(); }
}
