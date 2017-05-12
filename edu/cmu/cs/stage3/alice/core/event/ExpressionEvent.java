package edu.cmu.cs.stage3.alice.core.event;

import edu.cmu.cs.stage3.alice.core.Expression;
import java.util.EventObject;


















public class ExpressionEvent
  extends EventObject
{
  public ExpressionEvent(Expression source)
  {
    super(source);
  }
  
  public Expression getExpression() { return (Expression)getSource(); }
}
