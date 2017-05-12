package edu.cmu.cs.stage3.alice.core.response;



public class FadeUpFromBlackAnimation
  extends AbstractFadeAnimation
{
  public FadeUpFromBlackAnimation() {}
  

  public class RuntimeFadeUpFromBlackAnimation
    extends AbstractFadeAnimation.RuntimeAbstractFadeAnimation
  {
    public RuntimeFadeUpFromBlackAnimation()
    {
      super();
    }
    
    protected boolean endsBlack() {
      return false;
    }
  }
}
