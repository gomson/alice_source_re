package edu.cmu.cs.stage3.alice.core.question;











public class NumberAddition
  extends BinaryNumberResultingInNumberQuestion
{
  public NumberAddition() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberSubtraction.class, NumberMultiplication.class, NumberDivision.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue, double bValue) {
    return aValue + bValue;
  }
}
