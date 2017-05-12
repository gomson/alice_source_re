package edu.cmu.cs.stage3.alice.core.question.list;

import edu.cmu.cs.stage3.alice.core.List;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;



















public class FirstIndexOfItem
  extends ListNumberQuestion
{
  public FirstIndexOfItem() {}
  
  public final ObjectProperty item = new ObjectProperty(this, "item", null, Object.class);
  public final NumberProperty startFromIndex = new NumberProperty(this, "startFromIndex", null);
  private static Class[] s_supportedCoercionClasses = { LastIndexOfItem.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected int getValue(List listValue) {
    return listValue.firstIndexOfItemValue(item.getValue(), startFromIndex.intValue(0));
  }
}
