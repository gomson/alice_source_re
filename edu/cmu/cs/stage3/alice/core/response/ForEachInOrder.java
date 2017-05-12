package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Behavior;




















public class ForEachInOrder
  extends ForEach
{
  public ForEachInOrder() {}
  
  private static Class[] s_supportedCoercionClasses = new Class[0];
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public class RuntimeForEachInOrder extends ForEach.RuntimeForEach { public RuntimeForEachInOrder() { super(); }
    
    private int m_loopIndex;
    protected boolean preLoopTest(double t) {
      m_loopIndex += 1;
      if (m_loopIndex < m_listSize) {
        Behavior currentBehavior = getCurrentBehavior();
        if (currentBehavior != null) {
          currentBehavior.setForkIndex(this, m_loopIndex);
        }
        return true;
      }
      return false;
    }
    
    protected boolean postLoopTest(double t)
    {
      return true;
    }
    
    public void prologue(double t)
    {
      super.prologue(t);
      m_loopIndex = -1;
    }
  }
}
