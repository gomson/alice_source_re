package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Sin
  extends UnaryNumberResultingInNumberQuestion
{
  public Sin() {}
  
  private static Class[] s_supportedCoercionClasses = { Cos.class, Tan.class, ACos.class, ASin.class, ATan.class };
  
  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  protected double getValue(double aValue) {
    return Math.sin(aValue);
  }
}
