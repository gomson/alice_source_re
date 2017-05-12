package edu.cmu.cs.stage3.alice.core.question.mouse;

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;
import edu.cmu.cs.stage3.awt.AWTUtilities;
import java.awt.Component;
import java.awt.Point;














public class DistanceFromLeftEdge
  extends NumberQuestion
{
  public DistanceFromLeftEdge() {}
  
  public final BooleanProperty relativeToRenderTarget = new BooleanProperty(this, "relativeToRenderTarget", Boolean.TRUE);
  private static Class[] s_supportedCoercionClasses = { DistanceFromTopEdge.class };
  private RenderTarget[] m_renderTargets = null;
  
  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  public Object getValue() {
    Point p = AWTUtilities.getCursorLocation();
    if (p != null) {
      int x = x;
      if ((relativeToRenderTarget.booleanValue()) && 
        (m_renderTargets != null) && 
        (m_renderTargets.length > 0)) {
        x -= m_renderTargets[0].getAWTComponent().getLocationOnScreen().x;
      }
      

      return new Double(x);
    }
    return null;
  }
  
  protected void started(World world, double time) {
    super.started(world, time);
    m_renderTargets = ((RenderTarget[])world.getDescendants(RenderTarget.class));
  }
  
  protected void stopped(World world, double time) { super.stopped(world, time);
    m_renderTargets = null;
  }
}
