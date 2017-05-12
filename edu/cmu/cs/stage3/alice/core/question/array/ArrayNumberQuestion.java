package edu.cmu.cs.stage3.alice.core.question.array;

import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.property.ArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;



















public abstract class ArrayNumberQuestion
  extends NumberQuestion
{
  public ArrayNumberQuestion() {}
  
  public final ArrayProperty array = new ArrayProperty(this, "array", null);
  
  protected abstract int getValue(Array paramArray);
  
  public Object getValue() {
    Array arrayValue = array.getArrayValue();
    if (arrayValue != null) {
      return new Integer(getValue(arrayValue));
    }
    return null;
  }
}
