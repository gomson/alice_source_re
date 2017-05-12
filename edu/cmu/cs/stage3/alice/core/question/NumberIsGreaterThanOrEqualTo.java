package edu.cmu.cs.stage3.alice.core.question;











public class NumberIsGreaterThanOrEqualTo
  extends BinaryNumberResultingInBooleanQuestion
{
  public NumberIsGreaterThanOrEqualTo() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberIsGreaterThan.class, NumberIsLessThan.class, NumberIsLessThanOrEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(double aValue, double bValue) {
    return aValue >= bValue;
  }
}
