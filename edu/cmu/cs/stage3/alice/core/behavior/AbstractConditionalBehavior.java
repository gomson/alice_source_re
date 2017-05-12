package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ResponseProperty;

















public abstract class AbstractConditionalBehavior
  extends Behavior
{
  public AbstractConditionalBehavior() {}
  
  public final ResponseProperty beginResponse = new ResponseProperty(this, "beginResponse", null);
  public final ResponseProperty duringResponse = new ResponseProperty(this, "duringResponse", null);
  public final ResponseProperty endResponse = new ResponseProperty(this, "endResponse", null);
  
  private static final int RUNTIME_STATE_CHECKING_FOR_TRUE = 0;
  private static final int RUNTIME_STATE_BEGINNING = 1;
  private static final int RUNTIME_STATE_CHECKING_FOR_FALSE = 2;
  private static final int RUNTIME_STATE_ENDING = 3;
  
  private int m_runtimeState = 0;
  
  private boolean m_booleanValue;
  private Response.RuntimeResponse m_runtimeBeginResponse;
  private Response.RuntimeResponse m_runtimeDuringResponse;
  private Response.RuntimeResponse m_runtimeEndResponse;
  public boolean listeningToKeypress = false;
  

  protected boolean invokeEndOnStop() { return false; }
  
  public void stopAllRuntimeResponses(double time) {
    if ((m_runtimeBeginResponse != null) && 
      (m_runtimeBeginResponse.isActive())) {
      m_runtimeBeginResponse.stop(time);
    }
    
    if ((m_runtimeDuringResponse != null) && 
      (m_runtimeDuringResponse.isActive())) {
      m_runtimeDuringResponse.stop(time);
    }
    
    if (m_runtimeEndResponse != null) {
      if (m_runtimeEndResponse.isActive()) {
        m_runtimeEndResponse.stop(time);
      }
      else if (invokeEndOnStop()) {
        m_runtimeEndResponse.prologue(time);
        m_runtimeEndResponse.update(time);
        m_runtimeEndResponse.epilogue(time);
      }
    }
  }
  
  protected void set(boolean booleanValue)
  {
    m_booleanValue = booleanValue;
  }
  
  protected void internalSchedule(double t, double dt) {
    double timeRemaining = 0.0D;
    if (m_runtimeState == 0)
    {
      if (m_booleanValue) {
        if (m_runtimeBeginResponse != null) {
          m_runtimeState = 1;
          m_runtimeBeginResponse.prologue(t);
        } else {
          m_runtimeState = 2;
        }
      }
    }
    if (m_runtimeState == 1) {
      m_runtimeBeginResponse.update(t);
      timeRemaining = m_runtimeBeginResponse.getTimeRemaining(t);
      if (timeRemaining <= 0.0D) {
        m_runtimeBeginResponse.epilogue(t);
        m_runtimeState = 2;
      }
    }
    if (m_runtimeState == 2)
    {
      if (m_booleanValue) {
        if (m_runtimeDuringResponse != null) {
          if (!m_runtimeDuringResponse.isActive()) {
            m_runtimeDuringResponse.prologue(t + timeRemaining);
          }
          m_runtimeDuringResponse.update(t);
          timeRemaining = m_runtimeDuringResponse.getTimeRemaining(t);
          if (timeRemaining <= 0.0D) {
            m_runtimeDuringResponse.epilogue(t + timeRemaining);
            m_runtimeDuringResponse.prologue(t + timeRemaining);
          }
        }
      } else {
        if (m_runtimeDuringResponse != null) {
          m_runtimeDuringResponse.epilogue(t);
        }
        if (m_runtimeEndResponse != null) {
          m_runtimeState = 3;
          m_runtimeEndResponse.prologue(t + timeRemaining);
        } else {
          m_runtimeState = 0;
        }
      }
    }
    if (m_runtimeState == 3) {
      m_runtimeEndResponse.update(t);
      timeRemaining = m_runtimeEndResponse.getTimeRemaining(t);
      if (timeRemaining <= 0.0D) {
        m_runtimeEndResponse.epilogue(t);
        m_runtimeState = 0;
      }
    }
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    
    Response beginResponseValue = beginResponse.getResponseValue();
    Response duringResponseValue = duringResponse.getResponseValue();
    Response endResponseValue = endResponse.getResponseValue();
    if ((beginResponseValue != null) && (isCommentedOut.booleanValue())) {
      beginResponseValue = null;
    }
    if ((duringResponseValue != null) && (isCommentedOut.booleanValue())) {
      duringResponseValue = null;
    }
    if ((endResponseValue != null) && (isCommentedOut.booleanValue())) {
      endResponseValue = null;
    }
    if (beginResponseValue != null) {
      m_runtimeBeginResponse = beginResponseValue.manufactureRuntimeResponse();
    } else {
      m_runtimeBeginResponse = null;
    }
    if (duringResponseValue != null) {
      m_runtimeDuringResponse = duringResponseValue.manufactureRuntimeResponse();
    } else {
      m_runtimeDuringResponse = null;
    }
    if (endResponseValue != null) {
      m_runtimeEndResponse = endResponseValue.manufactureRuntimeResponse();
    } else {
      m_runtimeEndResponse = null;
    }
    m_runtimeState = 0;
  }
}
