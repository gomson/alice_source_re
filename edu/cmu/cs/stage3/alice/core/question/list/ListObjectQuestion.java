package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;


















public abstract class ListObjectQuestion
  extends Question
{
  public ListObjectQuestion() {}
  
  public final ListProperty list = new ListProperty(this, "list", null);
  
  protected abstract Object getValue(List paramList);
  
  public Class getValueClass() { List listValue = list.getListValue();
    if (listValue != null) {
      return valueClass.getClassValue();
    }
    
    return Object.class;
  }
  
  public Object getValue()
  {
    List listValue = list.getListValue();
    if (listValue != null) {
      return getValue(listValue);
    }
    return null;
  }
}
