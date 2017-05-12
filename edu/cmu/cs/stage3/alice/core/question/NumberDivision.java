package edu.cmu.cs.stage3.alice.core.question;











public class NumberDivision
  extends BinaryNumberResultingInNumberQuestion
{
  public NumberDivision() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberAddition.class, NumberSubtraction.class, NumberMultiplication.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected double getValue(double aValue, double bValue) {
    return aValue / bValue;
  }
}
