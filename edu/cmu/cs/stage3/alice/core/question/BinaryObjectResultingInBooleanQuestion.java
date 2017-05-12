package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ClassProperty;
import edu.cmu.cs.stage3.alice.core.property.OverridableObjectProperty;



















public abstract class BinaryObjectResultingInBooleanQuestion
  extends BooleanQuestion
{
  public BinaryObjectResultingInBooleanQuestion() {}
  
  public final OverridableObjectProperty a = new OverridableObjectProperty(this, "a", null);
  public final OverridableObjectProperty b = new OverridableObjectProperty(this, "b", null);
  public final ClassProperty valueClass = new ClassProperty(this, "valueClass", null);
  
  protected abstract boolean getValue(Object paramObject1, Object paramObject2);
  
  protected void propertyChanged(Property property, Object value) { if (property == valueClass) {
      Class overrideValueClass = valueClass.getClassValue();
      a.setOverrideValueClass(overrideValueClass);
      b.setOverrideValueClass(overrideValueClass);
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public Object getValue() {
    if (getValue(a.getValue(), b.getValue())) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
