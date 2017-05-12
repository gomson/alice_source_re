package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import java.lang.reflect.Field;


















public class PropertyValue
  extends Question
{
  public PropertyValue() {}
  
  private boolean m_ignorePropertyChanges = false;
  public final OverridableElementProperty element = new OverridableElementProperty(this, "element", null);
  public final StringProperty propertyName = new StringProperty(this, "propertyName", null);
  
  private void updateOverrideValueClass() { Class elementOverrideValueClass = null;
    String propertyNameValue = propertyName.getStringValue();
    if (propertyNameValue != null) {
      Element elementValue = element.getElementValue();
      if (elementValue != null) {
        Property property = elementValue.getPropertyNamed(propertyNameValue);
        if (property != null) {
          elementOverrideValueClass = property.getDeclaredClass();
        }
        else if ((elementValue instanceof Expression)) {
          Class cls = ((Expression)elementValue).getValueClass();
          if (cls != null) {
            elementOverrideValueClass = cls;
          }
        }
      }
    }
    
    element.setOverrideValueClass(elementOverrideValueClass);
  }
  
  protected void propertyChanged(Property property, Object value) {
    if (m_ignorePropertyChanges) {
      return;
    }
    if (property == element) {
      updateOverrideValueClass();
    } else if (property == propertyName) {
      updateOverrideValueClass();
    } else
      super.propertyChanged(property, value);
  }
  
  private Property getPropertyValue() {
    if (element.getOverrideValueClass() == null) {
      updateOverrideValueClass();
    }
    Element elementValue = element.getElementValue();
    String propertyNameValue = propertyName.getStringValue();
    if ((elementValue != null) && (propertyNameValue != null)) {
      return elementValue.getPropertyNamed(propertyNameValue);
    }
    












    return null;
  }
  

  public Object getValue()
  {
    Property property = getPropertyValue();
    if (property != null) {
      return property.getValue();
    }
    throw new RuntimeException();
  }
  

  public Class getValueClass()
  {
    Property property = getPropertyValue();
    if (property != null) {
      return property.getValueClass();
    }
    String propertyNameValue = propertyName.getStringValue();
    if (propertyNameValue != null) {
      Class cls = element.getValueClass();
      if (Element.class.isAssignableFrom(cls)) {
        try {
          Field field = cls.getField(propertyNameValue);
          if (field != null) {
            return Element.getValueClassForPropertyNamed(field.getDeclaringClass(), propertyNameValue);
          }
        }
        catch (NoSuchFieldException localNoSuchFieldException) {}catch (SecurityException localSecurityException) {}
      }
    }
    


    return Object.class;
  }
}
