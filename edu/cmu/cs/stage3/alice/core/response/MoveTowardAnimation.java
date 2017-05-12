package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.NumberProperty;













public class MoveTowardAnimation
  extends AbstractMoveInDirectionOfAnimation
{
  public MoveTowardAnimation() {}
  
  public class RuntimeMoveTowardAnimation
    extends AbstractMoveInDirectionOfAnimation.RuntimeAbstractMoveInDirectionOfAnimationAnimation
  {
    public RuntimeMoveTowardAnimation()
    {
      super();
    }
    
    protected double getActualAmountValue() { return amount.doubleValue(); }
  }
}
