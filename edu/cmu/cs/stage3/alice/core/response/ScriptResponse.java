package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;
import edu.cmu.cs.stage3.alice.scripting.CompileType;


















public class ScriptResponse
  extends Response
{
  public ScriptResponse() {}
  
  public final ScriptProperty script = new ScriptProperty(this, "script", "");
  

  protected Number getDefaultDuration() { return new Double(0.0D); }
  
  public class RuntimeScriptResponse extends Response.RuntimeResponse { public RuntimeScriptResponse() { super(); }
    
    public void update(double t) {
      super.update(t);
      exec(script.getCode(CompileType.EXEC_MULTIPLE));
    }
  }
}
