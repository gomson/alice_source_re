package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;




















public abstract class BooleanQuestion
  extends Question
{
  public BooleanQuestion() {}
  
  public Class getValueClass()
  {
    return Boolean.class;
  }
}
