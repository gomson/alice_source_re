package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import java.lang.reflect.Array;


















public class Return
  extends Component
{
  public Return() {}
  
  public final ValueProperty value = new ValueProperty(this, "value", null);
  public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
  
  protected void propertyChanged(Property property, Object o) {
    if (property != value)
    {
      if (property == valueClass) {
        value.setOverrideValueClass((Class)o);
      } else
        super.propertyChanged(property, o);
    }
  }
  
  public Object[] execute() {
    Class valueClassValue = valueClass.getClassValue();
    Object[] returnArray = (Object[])Array.newInstance(valueClass.getClassValue(), 1);
    returnArray[0] = value.getValue();
    return returnArray;
  }
}
