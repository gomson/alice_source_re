package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class ATan
  extends UnaryNumberResultingInNumberQuestion
{
  public ATan() {}
  
  private static Class[] s_supportedCoercionClasses = { Cos.class, Sin.class, Tan.class, ACos.class, ASin.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue) {
    return Math.atan(aValue);
  }
}
