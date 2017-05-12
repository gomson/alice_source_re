package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class ToRadians
  extends UnaryNumberResultingInNumberQuestion
{
  public ToRadians() {}
  
  private static Class[] s_supportedCoercionClasses = { ToDegrees.class };
  
  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  protected double getValue(double aValue) {
    return Math.toRadians(aValue);
  }
}
