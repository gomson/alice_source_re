package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.BinaryNumberResultingInNumberQuestion;


















public class Min
  extends BinaryNumberResultingInNumberQuestion
{
  public Min() {}
  
  private static Class[] s_supportedCoercionClasses = { Max.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue, double bValue) {
    return Math.min(aValue, bValue);
  }
}
