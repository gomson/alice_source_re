package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;



















public class ItemAtIndex
  extends ListObjectQuestion
{
  public ItemAtIndex() {}
  
  public final NumberProperty index = new NumberProperty(this, "index", new Integer(-1));
  
  public Object getValue(List listValue) {
    int i = index.intValue();
    return listValue.itemValueAtIndex(i);
  }
}
