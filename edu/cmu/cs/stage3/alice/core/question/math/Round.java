package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;

















public class Round
  extends UnaryNumberResultingInNumberQuestion
{
  public Round() {}
  
  protected double getValue(double aValue)
  {
    return Math.round(aValue);
  }
}
