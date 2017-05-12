package edu.cmu.cs.stage3.alice.core.question.stack;

import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.Stack;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.StackProperty;
















public abstract class StackObjectQuestion
  extends Question
{
  public StackObjectQuestion() {}
  
  protected abstract Object getValue(Stack paramStack);
  
  public final StackProperty stack = new StackProperty(this, "stack", null);
  

  public Class getValueClass() { return stack.getStackValue().valueClass.getClassValue(); }
  
  public Object getValue() {
    Stack stackValue = stack.getStackValue();
    if (stackValue != null) {
      return getValue(stackValue);
    }
    return null;
  }
}
