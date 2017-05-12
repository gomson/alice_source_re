package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;


















public class ItemAtEnd
  extends ListObjectQuestion
{
  public ItemAtEnd() {}
  
  private static Class[] s_supportedCoercionClasses = { ItemAtBeginning.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  public Object getValue(List listValue) {
    return listValue.itemValueAtEnd();
  }
}
