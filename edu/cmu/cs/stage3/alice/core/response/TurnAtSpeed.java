package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import javax.vecmath.Vector3d;



















public class TurnAtSpeed
  extends RotateAtSpeed
{
  public TurnAtSpeed() {}
  
  private static Class[] s_supportedCoercionClasses = { RollAtSpeed.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  

  protected boolean acceptsDirection(Direction direction) { return direction.getTurnAxis() != null; }
  
  public class RuntimeTurnAtSpeed extends RotateAtSpeed.RuntimeRotateAtSpeed { public RuntimeTurnAtSpeed() { super(); }
    
    protected Vector3d getAxis(Direction direction) {
      if (direction != null) {
        return direction.getTurnAxis();
      }
      return null;
    }
  }
}
