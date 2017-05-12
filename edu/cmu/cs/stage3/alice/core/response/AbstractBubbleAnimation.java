package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.SimulationPropertyException;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.bubble.Bubble;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.lang.Messages;
import edu.cmu.cs.stage3.math.Box;
import java.awt.Font;












public abstract class AbstractBubbleAnimation
  extends Response
{
  public final TransformableProperty subject = new TransformableProperty(this, "subject", null);
  public final StringProperty what = new StringProperty(this, "what", "hello");
  public final ColorProperty bubbleColor = new ColorProperty(this, "bubbleColor", Color.WHITE);
  public final ColorProperty textColor = new ColorProperty(this, "textColor", Color.BLACK);
  public final NumberProperty fontSize = new NumberProperty(this, "fontSize", new Integer(20));
  public final StringProperty fontName = new StringProperty(this, "fontName", "Arial");
  private World m_world;
  
  public abstract class RuntimeAbstractBubbleAnimation extends Response.RuntimeResponse {
    public RuntimeAbstractBubbleAnimation() { super(); }
    
    protected abstract Bubble createBubble();
    
    public void prologue(double t)
    {
      super.prologue(t);
      Transformable subjectValue = subject.getTransformableValue();
      if (subjectValue == null) {
        throw new SimulationPropertyException(Messages.getString("subject_must_not_be_null_"), getCurrentStack(), subject);
      }
      String whatValue = what.getStringValue();
      if ((whatValue == null) || (whatValue.length() == 0)) {
        throw new SimulationPropertyException(Messages.getString("what_must_not_be_null_"), getCurrentStack(), what);
      }
      
      if (m_bubble == null) {
        m_bubble = createBubble();
      }
      
      m_bubble.setReferenceFrame(subjectValue);
      m_bubble.setOffsetFromReferenceFrame(subjectValue.getBoundingBox().getCenterOfTopFace());
      m_bubble.setText(whatValue);
      m_bubble.setFont(new Font(fontName.getStringValue(), 0, fontSize.intValue()));
      m_bubble.setBackgroundColor(bubbleColor.getColorValue().createAWTColor());
      m_bubble.setForegroundColor(textColor.getColorValue().createAWTColor());
      m_bubble.setIsShowing(true);
      m_world = getWorld();
      if (m_world != null) {
        m_world.bubbles.add(m_bubble);
      }
    }
    









    private World m_world;
    








    private Bubble m_bubble;
    







    public void epilogue(double t)
    {
      if (m_bubble != null) {
        m_bubble.setIsShowing(false);
        if (m_world != null) {
          m_world.bubbles.remove(m_bubble);
          m_world = null;
        }
      }
      super.epilogue(t);
    }
  }
  
  public AbstractBubbleAnimation() {}
}
