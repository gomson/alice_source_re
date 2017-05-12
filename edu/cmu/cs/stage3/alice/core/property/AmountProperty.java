package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Amount;
import edu.cmu.cs.stage3.alice.core.Element;










public class AmountProperty
  extends EnumerableProperty
{
  public AmountProperty(Element owner, String name, Amount defaultValue)
  {
    super(owner, name, defaultValue, Amount.class);
  }
  
  public Amount getAmountValue() { return (Amount)getEnumerableValue(); }
}
