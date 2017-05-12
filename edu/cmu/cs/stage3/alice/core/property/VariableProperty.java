package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Variable;





















public class VariableProperty
  extends ExpressionProperty
{
  public VariableProperty(Element owner, String name, Variable defaultValue)
  {
    super(owner, name, defaultValue, Variable.class);
  }
  
  public Variable getVariableValue() { return (Variable)getExpressionValue(); }
}
