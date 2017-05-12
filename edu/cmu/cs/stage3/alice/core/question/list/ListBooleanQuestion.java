package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;



















public abstract class ListBooleanQuestion
  extends BooleanQuestion
{
  public ListBooleanQuestion() {}
  
  public final ListProperty list = new ListProperty(this, "list", null);
  
  protected abstract boolean getValue(List paramList);
  
  public Object getValue() { List listValue = list.getListValue();
    if (listValue != null) {
      if (getValue(listValue)) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    
    return Boolean.FALSE;
  }
}
