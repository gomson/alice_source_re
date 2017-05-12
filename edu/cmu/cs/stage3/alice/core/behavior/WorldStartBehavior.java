package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.World;


















public class WorldStartBehavior
  extends TriggerBehavior
{
  public WorldStartBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { WorldIsRunningBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  public void started(World world, double time) {
    super.started(world, time);
    trigger(time);
  }
}
