package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.bubble.Bubble;
import edu.cmu.cs.stage3.alice.core.bubble.SpeechBubble;












public class SayAnimation
  extends AbstractBubbleAnimation
{
  public SayAnimation() {}
  
  public class RuntimeSayAnimation
    extends AbstractBubbleAnimation.RuntimeAbstractBubbleAnimation
  {
    public RuntimeSayAnimation()
    {
      super();
    }
    
    protected Bubble createBubble() { return new SpeechBubble(); }
  }
}
