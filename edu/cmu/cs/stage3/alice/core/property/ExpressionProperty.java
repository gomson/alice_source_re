package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;





















public class ExpressionProperty
  extends ElementProperty
{
  protected ExpressionProperty(Element owner, String name, Expression defaultValue, Class valueClass)
  {
    super(owner, name, defaultValue, valueClass);
  }
  
  public Expression getExpressionValue() { return (Expression)getElementValue(); }
}
