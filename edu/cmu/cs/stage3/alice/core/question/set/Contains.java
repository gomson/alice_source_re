package edu.cmu.cs.stage3.alice.core.question.set;

import edu.cmu.cs.stage3.alice.core.Set;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;



















public class Contains
  extends SetBooleanQuestion
{
  public Contains() {}
  
  public final ObjectProperty item = new ObjectProperty(this, "item", null, Object.class);
  
  protected boolean getValue(Set setValue) { return setValue.containsValue(item.getValue()); }
}
