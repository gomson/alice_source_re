package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import java.lang.reflect.Field;
import java.util.EventObject;















public class PropertyEvent
  extends EventObject
{
  private Object m_value;
  private Object o_value;
  
  public PropertyEvent(Property source, Object value)
  {
    super(source);
    m_value = value;
  }
  
  public Property getProperty() { return (Property)getSource(); }
  
  public Object getValue() {
    return m_value;
  }
  
  public Object getOldValue() { return o_value; }
  
  public void setOldValue(Object o) {
    o_value = o;
  }
  
  public boolean isSourceAlsoKnownAs(Class cls, String name) {
    Property property = getProperty();
    Element element = property.getOwner();
    if (cls.isAssignableFrom(element.getClass())) {
      try {
        Field field = cls.getField(name);
        Object o = field.get(element);
        return o == property;
      }
      catch (NoSuchFieldException localNoSuchFieldException) {}catch (IllegalAccessException localIllegalAccessException) {}
    }
    
    return false;
  }
}
