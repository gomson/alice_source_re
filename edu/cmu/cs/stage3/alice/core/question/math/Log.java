package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;

















public class Log
  extends UnaryNumberResultingInNumberQuestion
{
  public Log() {}
  
  protected double getValue(double aValue)
  {
    return Math.log(aValue);
  }
}
