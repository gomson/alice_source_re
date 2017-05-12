package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;


















public class DoInOrder
  extends CompositeResponse
{
  public DoInOrder() {}
  
  private static Class[] s_supportedCoercionClasses = { DoTogether.class };
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public class RuntimeDoInOrder extends CompositeResponse.RuntimeCompositeResponse { public RuntimeDoInOrder() { super(); }
    private int m_index = 0;
    private double m_timeRemaining;
    
    protected boolean preLoopTest(double t) { return true; }
    
    protected boolean postLoopTest(double t) {
      return false;
    }
    
    protected int getChildCount() {
      Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      return runtimeResponses.length;
    }
    
    protected double getChildTimeRemaining(int index, double t) { Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      return runtimeResponses[index].getTimeRemaining(t);
    }
    
    protected void childPrologueIfNecessary(int index, double t) { Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      if (!runtimeResponses[index].isActive())
        runtimeResponses[index].prologue(t);
    }
    
    protected void childUpdate(int index, double t) {
      Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      runtimeResponses[index].update(t);
    }
    
    protected void childEpilogue(int index, double t) { Response.RuntimeResponse[] runtimeResponses = getRuntimeResponses();
      runtimeResponses[index].epilogue(t);
    }
    
    protected boolean isCullable() {
      return getChildCount() == 0;
    }
    
    public void prologue(double t) {
      super.prologue(t);
      m_index = -1;
    }
    
    public void update(double t) {
      super.update(t);
      
      if (isCullable()) {
        m_timeRemaining = 0.0D;
      } else {
        m_timeRemaining = Double.POSITIVE_INFINITY;
        double timeRemaining;
        do { if (m_index == -1) {
            if (preLoopTest(t)) {
              if (getChildCount() == 0) {
                if (postLoopTest(t)) {
                  m_index = -1;
                  break; }
                m_timeRemaining = 0.0D;
                
                break;
              }
              m_index = 0;
            }
            else {
              m_timeRemaining = 0.0D;
              break;
            }
          }
          
          childPrologueIfNecessary(m_index, t);
          childUpdate(m_index, t);
          
          timeRemaining = getChildTimeRemaining(m_index, t);
          
          if (timeRemaining <= 0.0D) {
            childEpilogue(m_index, t);
            m_index += 1;
          }
          
          if (m_index == getChildCount()) {
            if (postLoopTest(t)) {
              m_index = -1;
            } else {
              m_timeRemaining = 0.0D;
              break;
            }
          }
        } while (
        



































          timeRemaining < 0.0D);
      }
    }
    
    public double getTimeRemaining(double t) {
      return m_timeRemaining;
    }
  }
}
