package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;





















public class NumberProperty
  extends ObjectProperty
{
  public NumberProperty(Element owner, String name, Number defaultValue)
  {
    super(owner, name, defaultValue, Number.class);
  }
  
  public Number getNumberValue() { return (Number)getValue(); }
  
  public double doubleValue(double valueIfNull) {
    Number number = getNumberValue();
    if (number != null) {
      return number.doubleValue();
    }
    return valueIfNull;
  }
  

  public double doubleValue() { return doubleValue(NaN.0D); }
  
  public int intValue(int valueIfNull) {
    Number number = getNumberValue();
    if (number != null) {
      return number.intValue();
    }
    return valueIfNull;
  }
  
  public int intValue() {
    return intValue(0);
  }
}
