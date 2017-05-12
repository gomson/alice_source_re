package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.World;


















public class WorldIsRunningBehavior
  extends AbstractConditionalBehavior
{
  public WorldIsRunningBehavior() {}
  
  private static Class[] s_supportedCoercionClasses = { WorldStartBehavior.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean invokeEndOnStop() {
    return true;
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    set(true);
  }
  
  protected void stopped(World world, double time) {
    set(false);
    super.stopped(world, time);
  }
}
