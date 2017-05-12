package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.UserDefinedResponseProperty;
import java.io.PrintStream;


















public class CallToUserDefinedResponse
  extends Response
{
  public final UserDefinedResponseProperty userDefinedResponse = new UserDefinedResponseProperty(this, "userDefinedResponse", null);
  public final ElementArrayProperty requiredActualParameters = new ElementArrayProperty(this, "requiredActualParameters", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  public final ElementArrayProperty keywordActualParameters = new ElementArrayProperty(this, "keywordActualParameters", null, [Ledu.cmu.cs.stage3.alice.core.Variable.class);
  public class RuntimeCallToUserDefinedResponse extends Response.RuntimeResponse { public RuntimeCallToUserDefinedResponse() { super(); }
    private UserDefinedResponse.RuntimeUserDefinedResponse m_actual = null;
    private Behavior m_currentBehavior = null;
    
    public double getTimeRemaining(double t) {
      if (m_actual != null) {
        return m_actual.getTimeRemaining(t);
      }
      return super.getTimeRemaining(t);
    }
    
    public void prologue(double t)
    {
      super.prologue(t);
      UserDefinedResponse userDefinedResponseValue = userDefinedResponse.getUserDefinedResponseValue();
      if (userDefinedResponseValue != null) {
        m_actual = ((UserDefinedResponse.RuntimeUserDefinedResponse)userDefinedResponseValue.manufactureRuntimeResponse());
        m_currentBehavior = getWorld().getCurrentSandbox().getCurrentBehavior();
        Variable[] rap = (Variable[])requiredActualParameters.getArrayValue();
        for (int i = 0; i < rap.length; i++) {
          Variable runtime = m_currentBehavior.stackLookup(rap[i]);
          if (runtime != null) {
            rap[i] = runtime;
          }
        }
        Variable[] kap = (Variable[])keywordActualParameters.getArrayValue();
        for (int i = 0; i < kap.length; i++) {
          Variable runtime = m_currentBehavior.stackLookup(kap[i]);
          if (runtime != null) {
            kap[i] = runtime;
          }
        }
        try {
          m_currentBehavior.pushStack(
            rap, 
            kap, 
            (Variable[])requiredFormalParameters.getArrayValue(), 
            (Variable[])keywordFormalParameters.getArrayValue(), 
            (Variable[])localVariables.getArrayValue(), 
            true);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
          System.err.println(CallToUserDefinedResponse.this);
          throw aioobe;
        }
        m_actual.prologue(t);
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
        m_currentBehavior.popStack();
        m_currentBehavior = null;
        m_actual = null;
      }
    }
  }
  
  public CallToUserDefinedResponse() {}
}
