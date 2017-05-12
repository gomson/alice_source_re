package edu.cmu.cs.stage3.alice.core;











public class IllegalNameValueException
  extends RuntimeException
{
  private String m_nameValue;
  









  public IllegalNameValueException(String nameValue, String detail)
  {
    super(detail);
    m_nameValue = nameValue;
  }
  
  public String getNameValue() { return m_nameValue; }
}
