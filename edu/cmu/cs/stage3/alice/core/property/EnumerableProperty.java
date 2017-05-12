package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.util.Enumerable;





















public class EnumerableProperty
  extends ObjectProperty
{
  public EnumerableProperty(Element owner, String name, Enumerable defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public Enumerable getEnumerableValue() { return (Enumerable)getValue(); }
}
