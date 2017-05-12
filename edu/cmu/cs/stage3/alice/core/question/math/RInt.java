package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;

















public class RInt
  extends UnaryNumberResultingInNumberQuestion
{
  public RInt() {}
  
  protected double getValue(double aValue)
  {
    return Math.rint(aValue);
  }
}
