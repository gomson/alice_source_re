package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.bubble.Bubble;
import edu.cmu.cs.stage3.alice.core.bubble.NarrateBubble;

public class NarrateAnimation
  extends AbstractBubbleAnimation
{
  public NarrateAnimation() {}
  
  public class RuntimeNarrateAnimation
    extends AbstractBubbleAnimation.RuntimeAbstractBubbleAnimation
  {
    public RuntimeNarrateAnimation()
    {
      super();
    }
    
    protected Bubble createBubble() { return new NarrateBubble(); }
  }
}
