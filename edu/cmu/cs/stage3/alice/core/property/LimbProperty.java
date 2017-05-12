package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Limb;









public class LimbProperty
  extends EnumerableProperty
{
  public LimbProperty(Element owner, String name, Limb defaultValue)
  {
    super(owner, name, defaultValue, Limb.class);
  }
  
  public Limb getLimbValue() {
    return (Limb)getEnumerableValue();
  }
}
