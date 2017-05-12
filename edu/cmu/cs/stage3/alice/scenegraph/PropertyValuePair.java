package edu.cmu.cs.stage3.alice.scenegraph;








public class PropertyValuePair
{
  protected Property m_property;
  






  protected Object m_value;
  






  public PropertyValuePair(Property property, Object value)
  {
    m_property = property;
    m_value = value;
  }
  
  public Property getProperty() { return m_property; }
  
  public Object getValue() {
    return m_value;
  }
}
