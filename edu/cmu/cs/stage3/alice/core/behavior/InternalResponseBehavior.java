package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Behavior;

public abstract class InternalResponseBehavior
  extends Behavior
{
  public InternalResponseBehavior() {}
  
  public void stopAllRuntimeResponses(double time) {}
  
  public void internalSchedule(double time, double dt) {}
}
