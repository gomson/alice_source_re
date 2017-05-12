package edu.cmu.cs.stage3.alice.core.question;











public class NumberSubtraction
  extends BinaryNumberResultingInNumberQuestion
{
  public NumberSubtraction() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberAddition.class, NumberMultiplication.class, NumberDivision.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue, double bValue) {
    return aValue - bValue;
  }
}
