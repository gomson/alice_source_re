package edu.cmu.cs.stage3.alice.core.question.stack;

import edu.cmu.cs.stage3.alice.core.Stack;
import edu.cmu.cs.stage3.alice.core.property.StackProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;



















public abstract class StackNumberQuestion
  extends NumberQuestion
{
  public StackNumberQuestion() {}
  
  public final StackProperty stack = new StackProperty(this, "stack", null);
  
  protected abstract int getValue(Stack paramStack);
  
  public Object getValue() { Stack stackValue = stack.getStackValue();
    if (stackValue != null) {
      return new Integer(getValue(stackValue));
    }
    return null;
  }
}
