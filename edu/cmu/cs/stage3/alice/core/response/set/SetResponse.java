package edu.cmu.cs.stage3.alice.core.response.set;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Set;
import edu.cmu.cs.stage3.alice.core.property.SetProperty;



















public class SetResponse
  extends Response
{
  public final SetProperty set = new SetProperty(this, "set", null);
  public class RuntimeSetResponse extends Response.RuntimeResponse { public RuntimeSetResponse() { super(); }
    
    protected Set getSet() { return set.getSetValue(); }
  }
  
  public SetResponse() {}
}
