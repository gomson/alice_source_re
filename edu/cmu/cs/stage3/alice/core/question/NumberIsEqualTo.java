package edu.cmu.cs.stage3.alice.core.question;










/**
 * @deprecated
 */
public class NumberIsEqualTo
  extends BinaryNumberResultingInBooleanQuestion
{
  public NumberIsEqualTo() {}
  








  private static Class[] s_supportedCoercionClasses = { NumberIsNotEqualTo.class, NumberIsGreaterThan.class, NumberIsGreaterThanOrEqualTo.class, NumberIsLessThan.class, NumberIsLessThanOrEqualTo.class };
  
  public Class[] getSupportedCoercionClasses() {
    return s_supportedCoercionClasses;
  }
  
  protected boolean getValue(double aValue, double bValue) {
    return aValue == bValue;
  }
}
