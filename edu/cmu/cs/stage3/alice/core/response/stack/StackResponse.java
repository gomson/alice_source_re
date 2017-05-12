package edu.cmu.cs.stage3.alice.core.response.stack;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Stack;
import edu.cmu.cs.stage3.alice.core.property.StackProperty;



















public class StackResponse
  extends Response
{
  public final StackProperty stack = new StackProperty(this, "stack", null);
  public class RuntimeStackResponse extends Response.RuntimeResponse { public RuntimeStackResponse() { super(); }
    
    protected Stack getStack() { return stack.getStackValue(); }
  }
  
  public StackResponse() {}
}
