package edu.cmu.cs.stage3.alice.core.question;











public class NumberIsLessThanOrEqualTo
  extends BinaryNumberResultingInBooleanQuestion
{
  public NumberIsLessThanOrEqualTo() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberIsGreaterThan.class, NumberIsGreaterThanOrEqualTo.class, NumberIsLessThan.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(double aValue, double bValue) {
    return aValue <= bValue;
  }
}
