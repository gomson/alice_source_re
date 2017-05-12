package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;




















public class ConditionalBehavior
  extends AbstractConditionalBehavior
{
  public ConditionalBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { ConditionalTriggerBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final BooleanProperty condition = new BooleanProperty(this, "condition", null);
  
  public void preSchedule(double t) {
    super.preSchedule(t);
    set(condition.booleanValue(false));
  }
  
  protected void internalSchedule(double t, double dt) {
    super.internalSchedule(t, dt);
  }
}
