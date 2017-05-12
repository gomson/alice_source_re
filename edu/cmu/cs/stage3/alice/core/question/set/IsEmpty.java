package edu.cmu.cs.stage3.alice.core.question.set;

import edu.cmu.cs.stage3.alice.core.Set;

















public class IsEmpty
  extends SetBooleanQuestion
{
  public IsEmpty() {}
  
  protected boolean getValue(Set setValue)
  {
    return setValue.isEmpty();
  }
}
