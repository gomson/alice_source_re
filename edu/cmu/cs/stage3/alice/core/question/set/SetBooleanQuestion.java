package edu.cmu.cs.stage3.alice.core.question.set;

import edu.cmu.cs.stage3.alice.core.Set;
import edu.cmu.cs.stage3.alice.core.property.SetProperty;
import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;

















public abstract class SetBooleanQuestion
  extends BooleanQuestion
{
  public SetBooleanQuestion() {}
  
  protected abstract boolean getValue(Set paramSet);
  
  public final SetProperty set = new SetProperty(this, "set", null);
  
  public Object getValue() {
    Set setValue = set.getSetValue();
    if (setValue != null) {
      if (getValue(setValue)) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    
    return Boolean.FALSE;
  }
}
