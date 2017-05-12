package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Floor
  extends UnaryNumberResultingInNumberQuestion
{
  public Floor() {}
  
  private static Class[] s_supportedCoercionClasses = { Ceil.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue) {
    return Math.floor(aValue);
  }
}
