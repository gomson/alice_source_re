package edu.cmu.cs.stage3.alice.core.question.stack;

import edu.cmu.cs.stage3.alice.core.Stack;

















public class IsEmpty
  extends StackBooleanQuestion
{
  public IsEmpty() {}
  
  protected boolean getValue(Stack stackValue)
  {
    return stackValue.isEmpty();
  }
}
