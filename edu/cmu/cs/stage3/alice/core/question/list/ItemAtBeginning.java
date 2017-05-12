package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;


















public class ItemAtBeginning
  extends ListObjectQuestion
{
  public ItemAtBeginning() {}
  
  private static Class[] s_supportedCoercionClasses = { ItemAtEnd.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  public Object getValue(List listValue) {
    return listValue.itemValueAtBeginning();
  }
}
