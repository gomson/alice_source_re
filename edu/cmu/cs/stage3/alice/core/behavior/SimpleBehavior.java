package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.event.ScheduleEvent;
import edu.cmu.cs.stage3.alice.core.event.ScheduleListener;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

















/**
 * @deprecated
 */
public class SimpleBehavior
  extends Behavior
  implements ScheduleListener
{
  public final ResponseProperty response = new ResponseProperty(this, "response", null);
  
  public SimpleBehavior() {}
  
  public Response.RuntimeResponse[] getAllRunningResponses() { if (m_runtimeResponse != null) {
      return new Response.RuntimeResponse[] { m_runtimeResponse };
    }
    return null;
  }
  
  private Response.RuntimeResponse m_runtimeResponse;
  private Scheduler m_scheduler;
  public void stopAllRuntimeResponses(double time) { if (m_runtimeResponse != null) {
      m_runtimeResponse.stop(time);
    }
    m_runtimeResponse = null;
  }
  
  protected void internalSchedule(double t, double dt) {}
  
  public void scheduled(ScheduleEvent scheduleEvent) {
    double t = scheduleEvent.getTime();
    Response responseValue = response.getResponseValue();
    if ((responseValue != null) && (isCommentedOut.booleanValue())) {
      responseValue = null;
    }
    if (responseValue != null) {
      if (m_runtimeResponse == null) {
        m_runtimeResponse = responseValue.manufactureRuntimeResponse();
        m_runtimeResponse.prologue(t);
      }
      m_runtimeResponse.update(t);
      double timeRemaining = m_runtimeResponse.getTimeRemaining(t);
      if (timeRemaining <= 0.0D) {
        m_runtimeResponse.epilogue(t);
        if (m_scheduler != null) {
          m_scheduler.removeScheduleListener(this);
        }
        m_runtimeResponse = null;
      }
    }
  }
  
  public void start(Scheduler scheduler) { m_scheduler = scheduler;
    m_scheduler.addScheduleListener(this);
  }
}
