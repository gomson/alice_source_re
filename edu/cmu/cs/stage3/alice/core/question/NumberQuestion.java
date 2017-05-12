package edu.cmu.cs.stage3.alice.core.question;

import edu.cmu.cs.stage3.alice.core.Question;




















public abstract class NumberQuestion
  extends Question
{
  public NumberQuestion() {}
  
  public Class getValueClass()
  {
    return Number.class;
  }
}
