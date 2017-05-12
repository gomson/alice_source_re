package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;




















public class ObjectProperty
  extends Property
{
  public ObjectProperty(Element owner, String name, Object defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
}
