package edu.cmu.cs.stage3.alice.core.question.stack;

import edu.cmu.cs.stage3.alice.core.Stack;

















public class Size
  extends StackNumberQuestion
{
  public Size() {}
  
  public int getValue(Stack stackValue)
  {
    return stackValue.size();
  }
}
