package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.math.EulerAngles;





















public class EulerAnglesProperty
  extends ObjectProperty
{
  public EulerAnglesProperty(Element owner, String name, EulerAngles defaultValue)
  {
    super(owner, name, defaultValue, EulerAngles.class);
  }
  
  public EulerAngles getEulerAnglesValue() { return (EulerAngles)getValue(); }
}
