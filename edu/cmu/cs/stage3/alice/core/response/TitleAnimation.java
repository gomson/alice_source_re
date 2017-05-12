package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.bubble.Bubble;
import edu.cmu.cs.stage3.alice.core.bubble.TitleBubble;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Color;





public class TitleAnimation
  extends AbstractBubbleAnimation
{
  public TitleAnimation()
  {
    fontName.set("Arial");
    fontSize.set(new Integer(60));
    bubbleColor.set(Color.BLACK);
    textColor.set(Color.WHITE); }
  
  public class RuntimeTitleAnimation extends AbstractBubbleAnimation.RuntimeAbstractBubbleAnimation { public RuntimeTitleAnimation() { super(); }
    
    protected Bubble createBubble() {
      return new TitleBubble();
    }
  }
}
