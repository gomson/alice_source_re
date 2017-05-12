package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;






















public class IntegerProperty
  extends ObjectProperty
{
  public IntegerProperty(Element owner, String name, Integer defaultValue)
  {
    super(owner, name, defaultValue, Integer.class);
  }
  
  public Integer getIntegerValue() { return (Integer)getValue(); }
  
  public int intValue(int valueIfNull) {
    Integer integer = getIntegerValue();
    if (integer != null) {
      return integer.intValue();
    }
    return valueIfNull;
  }
  
  public int intValue() {
    return intValue(0);
  }
}
