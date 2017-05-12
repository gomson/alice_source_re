package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;

















public class Random
  extends NumberQuestion
{
  public Random() {}
  
  public Object getValue()
  {
    return new Double(Math.random());
  }
}
