package edu.cmu.cs.stage3.alice.core.question.set;

import edu.cmu.cs.stage3.alice.core.Set;

















public class Size
  extends SetNumberQuestion
{
  public Size() {}
  
  public int getValue(Set setValue)
  {
    return setValue.size();
  }
}
