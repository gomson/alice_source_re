package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;





















public class UserDefinedResponseProperty
  extends ResponseProperty
{
  public UserDefinedResponseProperty(Element owner, String name, UserDefinedResponse defaultValue)
  {
    super(owner, name, defaultValue, UserDefinedResponse.class);
  }
  
  public UserDefinedResponse getUserDefinedResponseValue() { return (UserDefinedResponse)getResponseValue(); }
}
