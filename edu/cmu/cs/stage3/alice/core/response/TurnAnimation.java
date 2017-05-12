package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Direction;
import javax.vecmath.Vector3d;



















public class TurnAnimation
  extends RotateAnimation
{
  public TurnAnimation() {}
  
  private static Class[] s_supportedCoercionClasses = { RollAnimation.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  

  protected boolean acceptsDirection(Direction direction) { return direction.getTurnAxis() != null; }
  
  public class RuntimeTurnAnimation extends RotateAnimation.RuntimeRotateAnimation { public RuntimeTurnAnimation() { super(); }
    
    protected Vector3d getAxis(Direction direction) {
      if (direction != null) {
        return direction.getTurnAxis();
      }
      return null;
    }
  }
}
