package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Exp
  extends UnaryNumberResultingInNumberQuestion
{
  public Exp() {}
  
  protected double getValue(double aValue)
  {
    return Math.exp(aValue);
  }
}
