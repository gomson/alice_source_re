package edu.cmu.cs.stage3.alice.core.question.array;

import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;


















public abstract class ArrayObjectQuestion
  extends Question
{
  public ArrayObjectQuestion() {}
  
  public final ArrayProperty array = new ArrayProperty(this, "array", null);
  
  protected abstract Object getValue(Array paramArray);
  
  public Class getValueClass() { return array.getArrayValue().valueClass.getClassValue(); }
  
  public Object getValue()
  {
    Array arrayValue = array.getArrayValue();
    if (arrayValue != null) {
      return getValue(arrayValue);
    }
    return null;
  }
}
