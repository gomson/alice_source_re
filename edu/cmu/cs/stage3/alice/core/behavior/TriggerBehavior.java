package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;
import java.util.Enumeration;
import java.util.Vector;
















public class TriggerBehavior
  extends Behavior
{
  public TriggerBehavior() {}
  
  public final ResponseProperty triggerResponse = new ResponseProperty(this, "triggerResponse", null);
  public final ObjectProperty multipleRuntimeResponsePolicy = new ObjectProperty(this, "multipleRuntimeResponsePolicy", MultipleRuntimeResponsePolicy.ENQUEUE_MULTIPLE, MultipleRuntimeResponsePolicy.class);
  private Vector m_runtimeResponses = new Vector();
  private Response.RuntimeResponse[] m_runtimeResponseArray = null;
  
  private Response.RuntimeResponse[] getRuntimeResponseArray() { if (m_runtimeResponseArray == null) {
      m_runtimeResponseArray = new Response.RuntimeResponse[m_runtimeResponses.size()];
      m_runtimeResponses.copyInto(m_runtimeResponseArray);
    }
    return m_runtimeResponseArray;
  }
  
  public void trigger(double time) {
    if ((m_runtimeResponses.size() > 0) && 
      (multipleRuntimeResponsePolicy.getValue() == MultipleRuntimeResponsePolicy.IGNORE_MULTIPLE)) {
      return;
    }
    
    Response response = triggerResponse.getResponseValue();
    if (response != null) {
      Response.RuntimeResponse runtimeResponse = response.manufactureRuntimeResponse();
      m_runtimeResponses.addElement(runtimeResponse);
      m_runtimeResponseArray = null;
    }
  }
  
  public void trigger() { trigger(System.currentTimeMillis() * 0.001D); }
  
  protected void internalSchedule(double time, double dt)
  {
    MultipleRuntimeResponsePolicy mrrp = (MultipleRuntimeResponsePolicy)multipleRuntimeResponsePolicy.getValue();
    Response.RuntimeResponse[] rra = getRuntimeResponseArray();
    for (int i = 0; i < rra.length; i++) {
      Response.RuntimeResponse runtimeResponse = rra[i];
      if (!runtimeResponse.isActive()) {
        runtimeResponse.prologue(time);
      }
      runtimeResponse.update(time);
      double timeRemaining = runtimeResponse.getTimeRemaining(time);
      if (timeRemaining <= 0.0D) {
        runtimeResponse.epilogue(time);
        runtimeResponse.HACK_markForRemoval();
      } else {
        if (mrrp != MultipleRuntimeResponsePolicy.INTERLEAVE_MULTIPLE) {
          break;
        }
      }
    }
    if (m_runtimeResponses.size() > 0) {
      synchronized (m_runtimeResponses) {
        Enumeration enum0 = m_runtimeResponses.elements();
        while (enum0.hasMoreElements()) {
          Response.RuntimeResponse runtimeResponse = (Response.RuntimeResponse)enum0.nextElement();
          if (runtimeResponse.HACK_isMarkedForRemoval()) {
            m_runtimeResponses.removeElement(runtimeResponse);
            m_runtimeResponseArray = null;
          }
        }
      }
    }
  }
  
  public void stopAllRuntimeResponses(double time) {
    Response.RuntimeResponse[] rra = getRuntimeResponseArray();
    for (int i = 0; i < rra.length; i++) {
      Response.RuntimeResponse runtimeResponse = rra[i];
      runtimeResponse.stop(time);
    }
    m_runtimeResponses.removeAllElements();
    m_runtimeResponseArray = null;
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    m_runtimeResponses.removeAllElements();
    m_runtimeResponseArray = null;
  }
}
