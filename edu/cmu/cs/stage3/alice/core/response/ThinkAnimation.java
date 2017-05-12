package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.bubble.Bubble;
import edu.cmu.cs.stage3.alice.core.bubble.ThoughtBubble;












public class ThinkAnimation
  extends AbstractBubbleAnimation
{
  public ThinkAnimation() {}
  
  public class RuntimeThinkAnimation
    extends AbstractBubbleAnimation.RuntimeAbstractBubbleAnimation
  {
    public RuntimeThinkAnimation()
    {
      super();
    }
    
    protected Bubble createBubble() { return new ThoughtBubble(); }
  }
}
