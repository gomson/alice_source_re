package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.util.StringObjectPair;





















public class ResponsePrototype
  extends ElementPrototype
{
  public ResponsePrototype(Class responseClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties)
  {
    super(responseClass, knownPropertyValues, desiredProperties);
  }
  
  public Response createNewResponse() {
    return (Response)createNewElement();
  }
  
  public Class getResponseClass() {
    return super.getElementClass();
  }
  

  protected ElementPrototype createInstance(Class elementClass, StringObjectPair[] knownPropertyValues, String[] desiredProperties)
  {
    return new ResponsePrototype(elementClass, knownPropertyValues, desiredProperties);
  }
}
