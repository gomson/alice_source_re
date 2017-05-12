package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class ToDegrees
  extends UnaryNumberResultingInNumberQuestion
{
  public ToDegrees() {}
  
  private static Class[] s_supportedCoercionClasses = { ToRadians.class };
  
  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  protected double getValue(double aValue) {
    return Math.toDegrees(aValue);
  }
}
