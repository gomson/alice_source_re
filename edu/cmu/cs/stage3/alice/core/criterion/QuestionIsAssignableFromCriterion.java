package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Question;



















public class QuestionIsAssignableFromCriterion
  extends ExpressionIsAssignableFromCriterion
{
  public QuestionIsAssignableFromCriterion(Class cls)
  {
    super(Question.class, cls);
  }
}
