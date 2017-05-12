package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion;


















public class IEEERemainder
  extends BinaryNumberResultingInNumberQuestion
{
  public IEEERemainder() {}
  
  protected double getValue(double aValue, double bValue)
  {
    return Math.IEEEremainder(aValue, bValue);
  }
}
