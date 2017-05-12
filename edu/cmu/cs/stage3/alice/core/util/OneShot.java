package edu.cmu.cs.stage3.alice.core.util;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Scheduler;
import edu.cmu.cs.stage3.alice.core.event.ScheduleEvent;
import edu.cmu.cs.stage3.alice.core.event.ScheduleListener;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;















public class OneShot
  implements ScheduleListener
{
  private Response m_response;
  private Response.RuntimeResponse m_runtimeResponse;
  private Scheduler m_scheduler;
  
  public OneShot() {}
  
  public Response getResponse()
  {
    return m_response;
  }
  
  public void setResponse(Response response) { m_response = response; }
  
  public void stopRunningResponse(double time) {
    if (m_runtimeResponse != null) {
      m_runtimeResponse.stop(time);
      m_runtimeResponse = null;
    }
  }
  
  public void scheduled(ScheduleEvent scheduleEvent) { double t = scheduleEvent.getTime();
    boolean done;
    boolean done; if ((m_response == null) || (m_response.isCommentedOut.booleanValue())) {
      done = true;
    } else {
      if (m_runtimeResponse == null) {
        m_runtimeResponse = m_response.manufactureRuntimeResponse();
        m_runtimeResponse.prologue(t);
      }
      m_runtimeResponse.update(t);
      double timeRemaining = m_runtimeResponse.getTimeRemaining(t);
      done = timeRemaining <= 0.0D;
      if (done) {
        m_runtimeResponse.epilogue(t);
        m_runtimeResponse = null;
      }
    }
    if ((done) && 
      (m_scheduler != null)) {
      m_scheduler.removeScheduleListener(this);
    }
  }
  
  public void start(Scheduler scheduler) {
    m_scheduler = scheduler;
    m_scheduler.addScheduleListener(this);
  }
}
