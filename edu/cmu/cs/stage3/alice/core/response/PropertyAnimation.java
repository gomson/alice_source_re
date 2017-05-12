package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.IllegalPropertyValueException;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.OverridableElementProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.ValueProperty;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Interpolator;
import edu.cmu.cs.stage3.util.HowMuch;

















public class PropertyAnimation
  extends Animation
{
  public PropertyAnimation() {}
  
  public final OverridableElementProperty element = new OverridableElementProperty(this, "element", null);
  public final StringProperty propertyName = new StringProperty(this, "propertyName", null);
  public final ValueProperty value = new ValueProperty(this, "value", null);
  public final ObjectProperty howMuch = new ObjectProperty(this, "howMuch", HowMuch.INSTANCE_AND_PARTS, HowMuch.class);
  
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
    } else
      super.propertyChanged(property, value);
  }
  
  public class RuntimePropertyAnimation extends Animation.RuntimeAnimation { public RuntimePropertyAnimation() { super(); }
    

    private Property m_property;
    
    private Object m_valueBegin;
    private Object m_valueEnd;
    private HowMuch m_howMuch;
    private Element m_element;
    private String m_propertyName;
    protected Property getProperty() { return m_property; }
    
    protected void set(Object value) {
      if (m_property != null) {
        if (howMuch != null) {
          m_property.set(value, m_howMuch);
        } else {
          m_property.set(value);
        }
      } else {
        m_element.setPropertyNamed(m_propertyName, value, m_howMuch);
      }
    }
    
    public void prologue(double t) {
      super.prologue(t);
      
      PropertyAnimation.this.updateOverrideValueClasses();
      m_element = element.getElementValue();
      m_propertyName = propertyName.getStringValue();
      if (m_element != null) {
        if (m_propertyName != null) {
          m_property = m_element.getPropertyNamed(m_propertyName);
          if (m_property != null) {
            m_valueBegin = m_property.getValue();
            m_valueEnd = value.getValue();
            if (m_property.isAcceptingOfHowMuch()) {
              m_howMuch = ((HowMuch)howMuch.getValue());
            } else {
              m_howMuch = HowMuch.INSTANCE;
            }
          } else {
            throw new IllegalPropertyValueException(propertyName, m_propertyName, m_element + " " + Messages.getString("does_not_have_property_named_") + m_propertyName);
          }
        } else {
          throw new IllegalPropertyValueException(propertyName, null, Messages.getString("propertyName_must_not_be_null_"));
        }
      } else {
        throw new IllegalPropertyValueException(element, null, Messages.getString("element_must_not_be_null_"));
      }
    }
    
    public void update(double t) {
      super.update(t);
      Object value;
      Object value; if ((m_valueBegin != null) && (m_valueEnd != null)) {
        value = Interpolator.interpolate(m_valueBegin, m_valueEnd, getPortion(t));
      } else {
        value = m_valueEnd;
      }
      set(value);
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      set(m_valueEnd);
    }
  }
}
