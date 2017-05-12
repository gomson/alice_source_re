package edu.cmu.cs.stage3.alice.core.question;










/**
 * @deprecated
 */
public class NumberIsNotEqualTo
  extends BinaryNumberResultingInBooleanQuestion
{
  public NumberIsNotEqualTo() {}
  








  private static Class[] s_supportedCoercionClasses = { NumberIsEqualTo.class, NumberIsGreaterThan.class, NumberIsGreaterThanOrEqualTo.class, NumberIsLessThan.class, NumberIsLessThanOrEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(double aValue, double bValue) {
    return aValue != bValue;
  }
}
