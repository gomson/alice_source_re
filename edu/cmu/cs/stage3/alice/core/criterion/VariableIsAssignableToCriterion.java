package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Variable;



















public class VariableIsAssignableToCriterion
  extends ExpressionIsAssignableToCriterion
{
  public VariableIsAssignableToCriterion(Class cls)
  {
    super(Variable.class, cls);
  }
}
