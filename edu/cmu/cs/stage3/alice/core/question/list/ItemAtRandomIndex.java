package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;


















public class ItemAtRandomIndex
  extends ListObjectQuestion
{
  public ItemAtRandomIndex() {}
  
  public Object getValue(List listValue)
  {
    return listValue.itemValueAtRandomIndex();
  }
}
