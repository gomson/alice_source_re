package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.Style;
import edu.cmu.cs.stage3.alice.core.property.StyleProperty;
import edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle;



















public abstract class Animation
  extends Response
{
  public final StyleProperty style = new StyleProperty(this, "style", TraditionalAnimationStyle.BEGIN_AND_END_GENTLY);
  public abstract class RuntimeAnimation extends Response.RuntimeResponse { public RuntimeAnimation() { super(); }
    
    protected Style m_style;
    public void prologue(double t) {
      super.prologue(t);
      m_style = style.getStyleValue();
    }
    
    protected double getPortion(double t) { double duration = getDuration();
      return m_style.getPortion(Math.min(getTimeElapsed(t), duration), duration);
    }
  }
  
  public Animation() {}
}
