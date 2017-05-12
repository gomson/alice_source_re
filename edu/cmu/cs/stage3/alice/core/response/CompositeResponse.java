package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;



















public abstract class CompositeResponse
  extends Response
{
  public final ElementArrayProperty componentResponses = new ElementArrayProperty(this, "componentResponses", null, [Ledu.cmu.cs.stage3.alice.core.Response.class);
  protected static Response.RuntimeResponse[] s_emptyRuntimeResponses = new Response.RuntimeResponse[0];
  public abstract class RuntimeCompositeResponse extends Response.RuntimeResponse { public RuntimeCompositeResponse() { super(); }
    private Response.RuntimeResponse[] m_runtimeResponses = null;
    
    protected Response.RuntimeResponse[] manufactureComponentRuntimeResponses(ElementArrayProperty property) { Response.RuntimeResponse[] runtimeResponses = new Response.RuntimeResponse[property.size()];
      int actualRuntimeResponseCount = 0;
      for (int i = 0; i < property.size(); i++) {
        Response response = (Response)property.get(i);
        if ((response != null) && 
          (!isCommentedOut.booleanValue()))
        {

          runtimeResponses[actualRuntimeResponseCount] = response.manufactureRuntimeResponse();
          actualRuntimeResponseCount++;
        }
      }
      


      if (actualRuntimeResponseCount < runtimeResponses.length) {
        Response.RuntimeResponse[] trimmedRuntimeResponses = new Response.RuntimeResponse[actualRuntimeResponseCount];
        System.arraycopy(runtimeResponses, 0, trimmedRuntimeResponses, 0, actualRuntimeResponseCount);
        return trimmedRuntimeResponses;
      }
      return runtimeResponses;
    }
    
    protected Response.RuntimeResponse[] getRuntimeResponses() {
      if (m_runtimeResponses == null) {
        m_runtimeResponses = manufactureComponentRuntimeResponses(componentResponses);
      }
      return m_runtimeResponses;
    }
    
    protected void childrenEpiloguesIfNecessary(double t) { Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      for (int i = 0; i < runtimeResponses.length; i++) {
        if (runtimeResponses[i].isActive()) {
          runtimeResponses[i].epilogue(t);
        }
      }
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      childrenEpiloguesIfNecessary(t);
    }
  }
  
  public CompositeResponse() {}
}
