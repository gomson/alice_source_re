package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;

















public class Sqrt
  extends UnaryNumberResultingInNumberQuestion
{
  public Sqrt() {}
  
  protected double getValue(double aValue)
  {
    return Math.sqrt(aValue);
  }
}
