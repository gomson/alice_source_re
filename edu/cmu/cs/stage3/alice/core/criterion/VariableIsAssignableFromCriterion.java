package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Variable;



















public class VariableIsAssignableFromCriterion
  extends ExpressionIsAssignableFromCriterion
{
  public VariableIsAssignableFromCriterion(Class cls)
  {
    super(Variable.class, cls);
  }
}
