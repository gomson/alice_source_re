package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion;


















public class ATan2
  extends BinaryNumberResultingInNumberQuestion
{
  public ATan2() {}
  
  protected double getValue(double aValue, double bValue)
  {
    return Math.atan2(aValue, bValue);
  }
}
