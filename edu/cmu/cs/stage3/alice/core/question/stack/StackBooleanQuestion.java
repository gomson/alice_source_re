package edu.cmu.cs.stage3.alice.core.question.stack;

import edu.cmu.cs.stage3.alice.core.Stack;
import edu.cmu.cs.stage3.alice.core.property.StackProperty;
import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;

















public abstract class StackBooleanQuestion
  extends BooleanQuestion
{
  public StackBooleanQuestion() {}
  
  protected abstract boolean getValue(Stack paramStack);
  
  public final StackProperty stack = new StackProperty(this, "stack", null);
  
  public Object getValue() {
    Stack stackValue = stack.getStackValue();
    if (stackValue != null) {
      if (getValue(stackValue)) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    
    return Boolean.FALSE;
  }
}
