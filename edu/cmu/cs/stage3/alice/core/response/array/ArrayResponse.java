package edu.cmu.cs.stage3.alice.core.response.array;

import edu.cmu.cs.stage3.alice.core.Array;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.ArrayProperty;



















public class ArrayResponse
  extends Response
{
  public final ArrayProperty array = new ArrayProperty(this, "array", null);
  public class RuntimeArrayResponse extends Response.RuntimeResponse { public RuntimeArrayResponse() { super(); }
    
    protected Array getArray() { return array.getArrayValue(); }
  }
  
  public ArrayResponse() {}
}
