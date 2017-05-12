package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.math.Quaternion;





















public class QuaternionProperty
  extends ObjectProperty
{
  public QuaternionProperty(Element owner, String name, Quaternion defaultValue)
  {
    super(owner, name, defaultValue, Quaternion.class);
  }
  
  public Quaternion getQuaternionValue() { return (Quaternion)getValue(); }
}
