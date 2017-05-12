package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.World;















public class DoTogether
  extends CompositeResponse
{
  public DoTogether() {}
  
  private static Class[] s_supportedCoercionClasses = { DoInOrder.class };
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  public class RuntimeDoTogether extends CompositeResponse.RuntimeCompositeResponse { private double m_timeRemaining;
    public RuntimeDoTogether() { super(); }
    
    private Behavior m_currentBehavior = null;
    
    public void prologue(double t) {
      super.prologue(t);
      Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      if (runtimeResponses.length > 0) {
        World world = getWorld();
        if (world != null) {
          Sandbox sandbox = world.getCurrentSandbox();
          if (sandbox != null) {
            m_currentBehavior = sandbox.getCurrentBehavior();
            m_currentBehavior.openFork(this, runtimeResponses.length);
          }
        }
      } else {
        m_currentBehavior = null;
      }
      for (int i = 0; i < runtimeResponses.length; i++) {
        if (m_currentBehavior != null) {
          m_currentBehavior.setForkIndex(this, i);
        }
        runtimeResponses[i].prologue(t);
      }
      if (m_currentBehavior != null) {
        m_currentBehavior.setForkIndex(this, -1);
      }
    }
    
    public void update(double t) {
      super.update(t);
      Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      m_timeRemaining = (-getTimeElapsed(t));
      for (int i = 0; i < runtimeResponses.length; i++) {
        if (m_currentBehavior != null) {
          m_currentBehavior.setForkIndex(this, i);
        }
        if (runtimeResponses[i].isActive()) {
          runtimeResponses[i].update(t);
          double timeRemaining = runtimeResponses[i].getTimeRemaining(t);
          if (timeRemaining <= 0.0D) {
            runtimeResponses[i].epilogue(t);
          }
          m_timeRemaining = Math.max(timeRemaining, m_timeRemaining);
        }
      }
      if (m_currentBehavior != null) {
        m_currentBehavior.setForkIndex(this, -1);
      }
    }
    
    public void epilogue(double t) {
      super.epilogue(t);
      if (m_currentBehavior != null)
      {
        m_currentBehavior.setForkIndex(this, 0);
        m_currentBehavior.closeFork(this);
        m_currentBehavior = null;
      }
    }
    
    public double getTimeRemaining(double t)
    {
      return m_timeRemaining;
    }
  }
}
