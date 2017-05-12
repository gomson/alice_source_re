package edu.cmu.cs.stage3.alice.core.question;











public class NumberIsGreaterThan
  extends BinaryNumberResultingInBooleanQuestion
{
  public NumberIsGreaterThan() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberIsGreaterThanOrEqualTo.class, NumberIsLessThan.class, NumberIsLessThanOrEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(double aValue, double bValue) {
    return aValue > bValue;
  }
}
