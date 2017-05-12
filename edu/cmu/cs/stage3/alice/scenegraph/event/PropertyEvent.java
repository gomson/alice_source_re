package edu.cmu.cs.stage3.alice.scenegraph.event;

import edu.cmu.cs.stage3.alice.scenegraph.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Property;
import edu.cmu.cs.stage3.lang.Messages;
import java.util.EventObject;


















public class PropertyEvent
  extends EventObject
{
  protected Property m_property;
  protected Object m_previousValue;
  protected boolean m_isPreviousValueValid;
  
  public PropertyEvent(Element source, Property property)
  {
    super(source);
    m_property = property;
    m_isPreviousValueValid = false;
  }
  
  public PropertyEvent(Element source, Property property, Object previousValue) { super(source);
    m_property = property;
    m_previousValue = previousValue;
    m_isPreviousValueValid = true;
  }
  
  public Property getProperty() { return m_property; }
  

  public boolean isPreviousValueValid() { return m_isPreviousValueValid; }
  
  public Object getPreviousValue() {
    if (m_isPreviousValueValid) {
      return m_previousValue;
    }
    throw new RuntimeException(Messages.getString("previous_value_in_not_valid"));
  }
}
