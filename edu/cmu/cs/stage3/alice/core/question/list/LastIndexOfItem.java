package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;



















public class LastIndexOfItem
  extends ListNumberQuestion
{
  public LastIndexOfItem() {}
  
  public final ObjectProperty item = new ObjectProperty(this, "item", null, Object.class);
  public final NumberProperty startFromIndex = new NumberProperty(this, "startFromIndex", null);
  private static Class[] s_supportedCoercionClasses = { FirstIndexOfItem.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected int getValue(List listValue) {
    int index = startFromIndex.intValue(listValue.size() - 1);
    return listValue.lastIndexOfItemValue(item.getValue(), index);
  }
}
