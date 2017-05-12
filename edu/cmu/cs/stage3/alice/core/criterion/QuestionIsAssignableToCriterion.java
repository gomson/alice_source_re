package edu.cmu.cs.stage3.alice.core.criterion;

import edu.cmu.cs.stage3.alice.core.Question;



















public class QuestionIsAssignableToCriterion
  extends ExpressionIsAssignableToCriterion
{
  public QuestionIsAssignableToCriterion(Class cls)
  {
    super(Question.class, cls);
  }
}
