package edu.cmu.cs.stage3.alice.core.question.set;

import edu.cmu.cs.stage3.alice.core.Set;
import edu.cmu.cs.stage3.alice.core.property.SetProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;



















public abstract class SetNumberQuestion
  extends NumberQuestion
{
  public SetNumberQuestion() {}
  
  public final SetProperty set = new SetProperty(this, "set", null);
  
  protected abstract int getValue(Set paramSet);
  
  public Object getValue() { Set setValue = set.getSetValue();
    if (setValue != null) {
      return new Integer(getValue(setValue));
    }
    return null;
  }
}
