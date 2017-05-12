package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Abs
  extends UnaryNumberResultingInNumberQuestion
{
  public Abs() {}
  
  protected double getValue(double aValue)
  {
    return Math.abs(aValue);
  }
}
