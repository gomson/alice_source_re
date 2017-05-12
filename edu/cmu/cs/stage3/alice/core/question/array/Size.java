package edu.cmu.cs.stage3.alice.core.question.array;

import edu.cmu.cs.stage3.alice.core.Array;


















public class Size
  extends ArrayNumberQuestion
{
  public Size() {}
  
  public int getValue(Array arrayValue)
  {
    return arrayValue.size();
  }
}
