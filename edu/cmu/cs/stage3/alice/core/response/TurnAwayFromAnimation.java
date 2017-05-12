package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.math.Matrix33;













public class TurnAwayFromAnimation
  extends AbstractPointAtAnimation
{
  public TurnAwayFromAnimation() {}
  
  public class RuntimeTurnAwayFromAnimation
    extends AbstractPointAtAnimation.RuntimeAbstractPointAtAnimation
  {
    public RuntimeTurnAwayFromAnimation()
    {
      super();
    }
    
    protected boolean onlyAffectYaw() { return true; }
    
    protected Matrix33 getTargetMatrix33()
    {
      Matrix33 m = super.getTargetMatrix33();
      m.rotateY(3.141592653589793D);
      return m;
    }
  }
}
