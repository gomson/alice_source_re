package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;



















public abstract class ListNumberQuestion
  extends NumberQuestion
{
  public ListNumberQuestion() {}
  
  public final ListProperty list = new ListProperty(this, "list", null);
  
  protected abstract int getValue(List paramList);
  
  public Object getValue() {
    List listValue = list.getListValue();
    if (listValue != null) {
      return new Integer(getValue(listValue));
    }
    return null;
  }
}
