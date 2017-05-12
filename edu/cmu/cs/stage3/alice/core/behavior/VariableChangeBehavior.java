package edu.cmu.cs.stage3.alice.core.behavior;

import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ExpressionEvent;
import edu.cmu.cs.stage3.alice.core.event.ExpressionListener;
import edu.cmu.cs.stage3.alice.core.property.VariableProperty;















public class VariableChangeBehavior
  extends TriggerBehavior
  implements ExpressionListener
{
  public VariableChangeBehavior() {}
  
  public final VariableProperty variable = new VariableProperty(this, "variable", null);
  

  public void expressionChanged(ExpressionEvent expressionEvent) { trigger(System.currentTimeMillis() * 0.001D); }
  
  private Expression m_expressionValue;
  protected void started(World world, double time) {
    super.started(world, time);
    m_expressionValue = ((Expression)variable.get());
    if (m_expressionValue != null) {
      m_expressionValue.addExpressionListener(this);
    }
  }
  
  protected void stopped(World world, double time) {
    super.stopped(world, time);
    if (m_expressionValue != null) {
      m_expressionValue.removeExpressionListener(this);
    }
  }
}
