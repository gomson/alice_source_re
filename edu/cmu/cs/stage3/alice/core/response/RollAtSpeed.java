package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import javax.vecmath.Vector3d;



















public class RollAtSpeed
  extends RotateAtSpeed
{
  public RollAtSpeed() {}
  
  private static Class[] s_supportedCoercionClasses = { TurnAtSpeed.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  

  protected boolean acceptsDirection(Direction direction) { return direction.getRollAxis() != null; }
  
  public class RuntimeRollAtSpeed extends RotateAtSpeed.RuntimeRotateAtSpeed { public RuntimeRollAtSpeed() { super(); }
    
    protected Vector3d getAxis(Direction direction) {
      if (direction != null) {
        return direction.getRollAxis();
      }
      return null;
    }
  }
}
