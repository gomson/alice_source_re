package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Tan
  extends UnaryNumberResultingInNumberQuestion
{
  public Tan() {}
  
  private static Class[] s_supportedCoercionClasses = { Cos.class, Sin.class, ACos.class, ASin.class, ATan.class };
  
  public Class[] getSupportedCoercionClasses() { return s_supportedCoercionClasses; }
  
  protected double getValue(double aValue) {
    return Math.tan(aValue);
  }
}
