package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;






















public class ElementProperty
  extends ObjectProperty
{
  public ElementProperty(Element owner, String name, Element defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public ElementProperty(Element owner, String name, Element defaultValue) { this(owner, name, defaultValue, Element.class); }
  
  public Element getElementValue() {
    return (Element)getValue();
  }
}
