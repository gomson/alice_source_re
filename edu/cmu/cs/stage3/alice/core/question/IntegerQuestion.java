package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;




















public abstract class IntegerQuestion
  extends Question
{
  public IntegerQuestion() {}
  
  public Class getValueClass()
  {
    return Integer.class;
  }
}
