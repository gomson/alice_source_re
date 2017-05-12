package edu.cmu.cs.stage3.alice.core;











public class IllegalArrayPropertyValueException
  extends IllegalPropertyValueException
{
  private int m_index;
  









  public IllegalArrayPropertyValueException(Property property, int index, Object object, String detail)
  {
    super(property, object, detail);
    m_index = index;
  }
  
  public int getIndex() { return m_index; }
}
