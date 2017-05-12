package edu.cmu.cs.stage3.alice.authoringtool.event;

import edu.cmu.cs.stage3.alice.core.World;
























public class AuthoringToolStateChangedEvent
{
  public static final int AUTHORING_STATE = 1;
  public static final int DEBUG_STATE = 2;
  public static final int RUNTIME_STATE = 3;
  private int currentState;
  private int previousState;
  private World world;
  
  public AuthoringToolStateChangedEvent(int previousState, int currentState, World world)
  {
    this.currentState = currentState;
    this.previousState = previousState;
    this.world = world;
  }
  
  /**
   * @deprecated
   */
  public int getState() {
    return getCurrentState();
  }
  
  public int getCurrentState() {
    return currentState;
  }
  
  public int getPreviousState() {
    return previousState;
  }
  
  public World getWorld() {
    return world;
  }
}
