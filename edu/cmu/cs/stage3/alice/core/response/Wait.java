package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;












public class Wait
  extends Response
{
  public Wait() {}
  
  public class RuntimeWait
    extends Response.RuntimeResponse
  {
    public RuntimeWait()
    {
      super();
    }
  }
}
