package edu.cmu.cs.stage3.alice.core;








public class IllegalPropertyValueException
  extends RuntimeException
{
  private Property m_property;
  





  private Object m_value;
  






  public IllegalPropertyValueException(Property property, Object value, String detail)
  {
    super(detail);
    m_property = property;
    m_value = value;
  }
  
  public Property getProperty() { return m_property; }
  
  public Object getValue() {
    return m_value;
  }
}
