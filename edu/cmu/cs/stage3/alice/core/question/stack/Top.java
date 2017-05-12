package edu.cmu.cs.stage3.alice.core.question.stack;

import edu.cmu.cs.stage3.alice.core.Stack;

















public class Top
  extends StackObjectQuestion
{
  public Top() {}
  
  protected Object getValue(Stack stackValue)
  {
    return stackValue.topValue();
  }
}
