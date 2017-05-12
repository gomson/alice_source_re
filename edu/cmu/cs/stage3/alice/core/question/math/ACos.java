package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class ACos
  extends UnaryNumberResultingInNumberQuestion
{
  public ACos() {}
  
  private static Class[] s_supportedCoercionClasses = { Cos.class, Sin.class, Tan.class, ASin.class, ATan.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue) {
    return Math.acos(aValue);
  }
}
