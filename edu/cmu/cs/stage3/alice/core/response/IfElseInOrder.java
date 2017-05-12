package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;


















public class IfElseInOrder
  extends DoInOrder
{
  public IfElseInOrder() {}
  
  public final BooleanProperty condition = new BooleanProperty(this, "condition", Boolean.TRUE);
  public final ElementArrayProperty elseComponentResponses = new ElementArrayProperty(this, "elseComponentResponses", null, [Ledu.cmu.cs.stage3.alice.core.Response.class);
  private static Class[] s_supportedCoercionClasses = new Class[0];
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public class RuntimeIfElseInOrder extends DoInOrder.RuntimeDoInOrder {
    private Response.RuntimeResponse[] m_runtimeElseComponentResponses = null;
    private boolean m_condition;
    
    public RuntimeIfElseInOrder() { super();
      m_runtimeElseComponentResponses = manufactureComponentRuntimeResponses(elseComponentResponses);
    }
    
    protected Response.RuntimeResponse[] getRuntimeResponses() {
      if (m_condition) {
        return super.getRuntimeResponses();
      }
      return m_runtimeElseComponentResponses;
    }
    
    public void prologue(double t)
    {
      m_condition = condition.booleanValue();
      super.prologue(t);
    }
  }
}
