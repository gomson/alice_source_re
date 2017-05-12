package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import javax.vecmath.Vector3d;



















public class RollAnimation
  extends RotateAnimation
{
  public RollAnimation() {}
  
  private static Class[] s_supportedCoercionClasses = { TurnAnimation.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  

  protected boolean acceptsDirection(Direction direction) { return direction.getRollAxis() != null; }
  
  public class RuntimeRollAnimation extends RotateAnimation.RuntimeRotateAnimation { public RuntimeRollAnimation() { super(); }
    
    protected Vector3d getAxis(Direction direction) {
      if (direction != null) {
        return direction.getRollAxis();
      }
      return null;
    }
  }
}
