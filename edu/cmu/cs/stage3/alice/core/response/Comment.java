package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;



















public class Comment
  extends Response
{
  public final StringProperty text = new StringProperty(this, "text", "");
  
  public Comment() { duration.set(new Double(0.0D)); }
  
  public class RuntimeComment extends Response.RuntimeResponse { public RuntimeComment() { super(); }
  }
}
