package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion;

















public class Pow
  extends BinaryNumberResultingInNumberQuestion
{
  public Pow() {}
  
  protected double getValue(double aValue, double bValue)
  {
    return Math.pow(aValue, bValue);
  }
}
