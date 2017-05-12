package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Ceil
  extends UnaryNumberResultingInNumberQuestion
{
  public Ceil() {}
  
  private static Class[] s_supportedCoercionClasses = { Floor.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue) {
    return Math.ceil(aValue);
  }
}
