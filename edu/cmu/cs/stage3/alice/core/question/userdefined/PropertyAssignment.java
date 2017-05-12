package edu.cmu.cs.stage3.alice.core.question.userdefined;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;



















public class PropertyAssignment
  extends Component
{
  public PropertyAssignment() {}
  
  public final OverridableElementProperty element = new OverridableElementProperty(this, "element", null);
  public final StringProperty propertyName = new StringProperty(this, "propertyName", null);
  public final ValueProperty value = new ValueProperty(this, "value", null);
  
  private void updateOverrideValueClasses() { Class elementOverrideValueClass = null;
    Class valueOverrideValueClass = null;
    String propertyNameValue = propertyName.getStringValue();
    if (propertyNameValue != null) {
      Element elementValue = element.getElementValue();
      if (elementValue != null) {
        Property property = elementValue.getPropertyNamed(propertyNameValue);
        if (property != null) {
          elementOverrideValueClass = property.getDeclaredClass();
          valueOverrideValueClass = property.getValueClass();
        }
        else if ((elementValue instanceof Expression)) {
          Class cls = ((Expression)elementValue).getValueClass();
          if (cls != null) {
            elementOverrideValueClass = cls;
            valueOverrideValueClass = Element.getValueClassForPropertyNamed(elementOverrideValueClass, propertyNameValue);
          }
        }
      }
    }
    
    element.setOverrideValueClass(elementOverrideValueClass);
    value.setOverrideValueClass(valueOverrideValueClass);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (property == element) {
      updateOverrideValueClasses();
    } else if (property == propertyName) {
      updateOverrideValueClasses();
    } else {
      super.propertyChanged(property, value);
    }
  }
  
  public Object[] execute() {
    element.getElementValue().getPropertyNamed(propertyName.getStringValue()).set(value.getValue());
    return null;
  }
}
