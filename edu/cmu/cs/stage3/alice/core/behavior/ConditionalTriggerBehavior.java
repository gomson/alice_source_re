package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;



















public class ConditionalTriggerBehavior
  extends TriggerBehavior
{
  public ConditionalTriggerBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { ConditionalBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses; }
  
  public final BooleanProperty condition = new BooleanProperty(this, "condition", null);
  private boolean m_previousValue;
  
  protected void internalSchedule(double t, double dt) { Object conditionValue = condition.get();
    if ((conditionValue instanceof Expression)) {
      conditionValue = ((Expression)conditionValue).getValue();
    }
    if (conditionValue != null) {
      boolean currentValue = ((Boolean)conditionValue).booleanValue();
      if (!m_previousValue)
      {

        if (currentValue) {
          trigger();
        }
      }
      m_previousValue = currentValue;
    } else {
      m_previousValue = false;
    }
    super.internalSchedule(t, dt);
  }
  
  protected void started(World world, double time)
  {
    super.started(world, time);
    m_previousValue = false;
  }
}
