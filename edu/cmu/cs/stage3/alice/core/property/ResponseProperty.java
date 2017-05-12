package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Response;





















public class ResponseProperty
  extends ElementProperty
{
  protected ResponseProperty(Element owner, String name, Response defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public ResponseProperty(Element owner, String name, Response defaultValue) { super(owner, name, defaultValue, Response.class); }
  
  public Response getResponseValue() {
    return (Response)getElementValue();
  }
}
