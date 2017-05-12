package edu.cmu.cs.stage3.alice.core.response;








public class TurnToFaceAnimation
  extends AbstractPointAtAnimation
{
  public TurnToFaceAnimation() {}
  







  public class RuntimeTurnToFaceAnimation
    extends AbstractPointAtAnimation.RuntimeAbstractPointAtAnimation
  {
    public RuntimeTurnToFaceAnimation()
    {
      super();
    }
    
    protected boolean onlyAffectYaw() { return true; }
  }
}
