package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.ScriptProperty;
import edu.cmu.cs.stage3.alice.scripting.CompileType;
import edu.cmu.cs.stage3.lang.Messages;




















public class ScriptDefinedResponse
  extends Response
{
  public final ScriptProperty script = new ScriptProperty(this, "script", "");
  public class RuntimeScriptDefinedResponse extends Response.RuntimeResponse { public RuntimeScriptDefinedResponse() { super(); }
    Response.RuntimeResponse m_actual = null;
    
    public double getTimeRemaining(double t) {
      if (m_actual != null) {
        return m_actual.getTimeRemaining(t);
      }
      return super.getTimeRemaining(t);
    }
    
    public void prologue(double t)
    {
      super.prologue(t);
      m_actual = null;
      Object o = eval(script.getCode(CompileType.EVAL));
      if ((o instanceof Response)) {
        m_actual = ((Response)o).manufactureRuntimeResponse();
        if (m_actual != null) {
          m_actual.prologue(t);
        }
      }
      else
      {
        throw new RuntimeException(script.getStringValue() + " " + Messages.getString("does_not_evaluate_to_a_response_"));
      }
    }
    
    public void update(double t) {
      super.update(t);
      if (m_actual != null) {
        m_actual.update(t);
      }
    }
    

    public void epilogue(double t)
    {
      super.epilogue(t);
      if (m_actual != null) {
        m_actual.epilogue(t);
        m_actual = null;
      }
    }
  }
  
  public ScriptDefinedResponse() {}
}
