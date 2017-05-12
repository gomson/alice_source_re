package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;













public class MoveAwayFromAnimation
  extends AbstractMoveInDirectionOfAnimation
{
  public MoveAwayFromAnimation() {}
  
  public class RuntimeMoveAwayFromAnimation
    extends AbstractMoveInDirectionOfAnimation.RuntimeAbstractMoveInDirectionOfAnimationAnimation
  {
    public RuntimeMoveAwayFromAnimation()
    {
      super();
    }
    
    protected double getActualAmountValue() { return -amount.doubleValue(); }
  }
}
