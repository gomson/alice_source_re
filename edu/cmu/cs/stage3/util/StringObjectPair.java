package edu.cmu.cs.stage3.util;

import java.io.Serializable;



















public class StringObjectPair
  implements Serializable
{
  private String string;
  private Object object;
  
  public StringObjectPair(String string, Object object)
  {
    this.string = string;
    this.object = object;
  }
  
  public String getString() {
    return string;
  }
  
  public void setString(String string) { this.string = string; }
  
  public Object getObject()
  {
    return object;
  }
  
  public void setObject(Object object) { this.object = object; }
  

  public String toString()
  {
    return "edu.cmu.cs.stage3.util.StringObjectPair[string=" + string + ",object=" + object + "]";
  }
}
