package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;




















public class WhileLoopInOrder
  extends DoInOrder
{
  public WhileLoopInOrder() {}
  
  public final BooleanProperty condition = new BooleanProperty(this, "condition", Boolean.TRUE);
  public final BooleanProperty testBeforeAsOpposedToAfter = new BooleanProperty(this, "testBeforeAsOpposedToAfter", Boolean.TRUE);
  private static Class[] s_supportedCoercionClasses = new Class[0];
  

  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public class RuntimeWhileLoopInOrder extends DoInOrder.RuntimeDoInOrder { public RuntimeWhileLoopInOrder() { super(); }
    
    protected boolean preLoopTest(double t) {
      if (testBeforeAsOpposedToAfter.booleanValue()) {
        return condition.booleanValue();
      }
      return true;
    }
    
    protected boolean postLoopTest(double t)
    {
      if (testBeforeAsOpposedToAfter.booleanValue()) {
        return true;
      }
      return condition.booleanValue();
    }
    
    protected boolean isCullable()
    {
      return false;
    }
  }
}
