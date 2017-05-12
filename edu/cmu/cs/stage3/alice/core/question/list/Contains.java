package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;



















public class Contains
  extends ListBooleanQuestion
{
  public Contains() {}
  
  public final ObjectProperty item = new ObjectProperty(this, "item", null, Object.class);
  
  protected boolean getValue(List listValue) {
    for (int i = 0; i < listValue.size(); i++) {
      if (listValue.itemAtIndex(i).equals(item.getValue())) {
        return true;
      }
    }
    return false;
  }
}
