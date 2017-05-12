package edu.cmu.cs.stage3.alice.core.question.math;

import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;


















public class Cos
  extends UnaryNumberResultingInNumberQuestion
{
  public Cos() {}
  
  private static Class[] s_supportedCoercionClasses = { Sin.class, Tan.class, ACos.class, ASin.class, ATan.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue) {
    return Math.cos(aValue);
  }
}
