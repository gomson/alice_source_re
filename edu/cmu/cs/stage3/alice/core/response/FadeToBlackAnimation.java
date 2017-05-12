package edu.cmu.cs.stage3.alice.core.response;



public class FadeToBlackAnimation
  extends AbstractFadeAnimation
{
  public FadeToBlackAnimation() {}
  

  public class RuntimeFadeToBlackAnimation
    extends AbstractFadeAnimation.RuntimeAbstractFadeAnimation
  {
    public RuntimeFadeToBlackAnimation()
    {
      super();
    }
    
    protected boolean endsBlack() {
      return true;
    }
  }
}
