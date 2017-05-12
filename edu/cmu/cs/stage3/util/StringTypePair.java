package edu.cmu.cs.stage3.util;

import java.io.Serializable;



















public class StringTypePair
  implements Serializable
{
  private String string;
  private Class type;
  
  public StringTypePair(String string, Class type)
  {
    this.string = string;
    this.type = type;
  }
  
  public String getString() {
    return string;
  }
  
  public void setString(String string) { this.string = string; }
  
  public Class getType()
  {
    return type;
  }
  
  public void setType(Class type) { this.type = type; }
  

  public String toString()
  {
    return "edu.cmu.cs.stage3.util.StringTypePair[string=" + string + ",type=" + type + "]";
  }
}
