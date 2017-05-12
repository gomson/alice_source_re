package edu.cmu.cs.stage3.alice.core.question;











public class NumberIsLessThan
  extends BinaryNumberResultingInBooleanQuestion
{
  public NumberIsLessThan() {}
  









  private static Class[] s_supportedCoercionClasses = { NumberIsGreaterThan.class, NumberIsGreaterThanOrEqualTo.class, NumberIsLessThanOrEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(double aValue, double bValue) {
    return aValue < bValue;
  }
}
